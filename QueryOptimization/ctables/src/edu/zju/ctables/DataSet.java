package edu.zju.ctables;

import edu.zju.GlobalVariables;
import edu.zju.ctables.Expressions.Expression;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by TLD7040A on 2017/5/31.
 */
public class DataSet
{
    public String FilesPath;
    public DataItem[] TotalData;
    public int rows=0;
    public int columns=0;
    public int TotalMissingNum=0;
    public int[] MaxNum;
    public ArrayList<ArrayList<Integer>> MissedObjs;//int[columns], every dim records objects which is lacking value in corresponding dim.
    public HashMap<Expression,Float[]> varsDistribution=new HashMap<>();//expression表示为Var

    public DataSet()
    {

    }
    public DataSet(String FilePath,int dim) throws Exception
    {
        FilesPath=FilePath;
        columns=dim;
        int i=0,j=0;
        //分别读取原始数据集(org)、位图数据集(b)、正则化数据集(dat)
        File datInputFile=new File(FilePath+".dat");
        File bitmapInputFile=new File(FilePath+".b");
        File originInputFile=new File(FilePath+".org");
        if(!datInputFile.exists())
        {
            System.out.println("未能找到指定.dat文件");
            return ;
        }
        if(!bitmapInputFile.exists())
        {
            System.out.println("未能找到指定.b文件");
            return ;
        }
        if(!originInputFile.exists())
        {
            System.out.println("未能找到指定.org文件");
            return ;
        }
        MissedObjs=new ArrayList<ArrayList<Integer>>(columns);
        for(i=0;i<columns;i++)
        {
            MissedObjs.add(new ArrayList<Integer>());
        }
        MaxNum=new int[dim];
        Scanner datScanner=new Scanner(datInputFile);
        Scanner bitmapScanner=new Scanner(bitmapInputFile);
        Scanner orgScanner=new Scanner(originInputFile);
        while(orgScanner.hasNextLine())
        {
            rows++;
            orgScanner.nextLine();
        }
        rows--;//去掉.org文件的第一行（因为这一行是注释）
        TotalData=new DataItem[rows];
        orgScanner.close();
        orgScanner=new Scanner(originInputFile);
        orgScanner.nextLine();
        for(i=0;i<rows;i++)
        {
            String dattmp=datScanner.nextLine();
            if(dattmp==null)
            {
                System.out.println(".dat数据读取过程中意外中止："+i+"th line.");
                break;
            }
            if(dattmp.charAt(0)=='#')
            {
                i--;
                continue;
            }
            String bittmp=bitmapScanner.nextLine();
            if(bittmp==null)
            {
                System.out.println(".b数据读取过程中意外中止："+i+"th line.");
                break;
            }

            String orgtmp=orgScanner.nextLine();
            if(orgtmp==null)
            {
                System.out.println(".org数据读取过程中意外中止："+i+"th line.");
                break;
            }
            String[] datStrings=dattmp.split(" |\t");//多个分隔符，用 | 作为连字符， |	表示分隔符可以是空格也可以是\t
            String[] bitStrings=bittmp.split(" |\t");
            String[] orgStrings=orgtmp.split(" |\t");
            TotalData[i]=new DataItem(i,columns,orgStrings[0],Integer.parseInt(orgStrings[1]));
            TotalData[i].id=i;
            for(j=0;j<columns;j++)
            {
                TotalData[i].data[j]=Integer.parseInt(datStrings[j]);
                TotalData[i].realData[j]=Integer.parseInt(orgStrings[j+2]);
                int flag=Integer.parseInt(bitStrings[j]);
                if(flag==0)//missing
                {
                    TotalData[i].missingValuesIndicator[j]=-1;
                    TotalMissingNum++;
                    MissedObjs.get(j).add(i);
                }
                else if(TotalData[i].data[j]>MaxNum[j])
                {
                    MaxNum[j]=TotalData[i].data[j];//update the best value in j th dimension
                }
            }
        }
        System.out.println("rows: "+rows+", i: "+i);
        double missingRate=(double)TotalMissingNum/(double)(rows*columns);
        System.out.println("MissingRate: "+missingRate);
        if(!ReadVarsDistribution())
            throw new Exception("ReadVarDistribution Failed!");
    }

    public boolean ReadVarsDistribution() throws Exception
    {
        File file=new File(FilesPath+".dist");
        if(!file.exists())
        {
            String filename=FilesPath+"_tmp.dist";
            GenerateVarFile(filename);
            System.out.println("Distribution File doesn't exist. Use InferModel to generate distribution file first!");
            System.out.println("Intermediate File: "+filename);
            return false;
        }
        Scanner scanner=new Scanner(file);
        while (scanner.hasNextLine())
        {
            String tmpstr=scanner.nextLine();
            String[] strs=tmpstr.trim().split(" |\t");
            Expression tmpvar=Expression.makeVar(Integer.parseInt(strs[0]),Integer.parseInt(strs[1]));
            Float[] tmpdist=new Float[strs.length-2];
            for(int i=0;i<strs.length-2;i++)
            {
                tmpdist[i]=Float.parseFloat(strs[i+2]);
            }
            varsDistribution.put(tmpvar,tmpdist);
        }
        return true;
    }
    public void GenerateVarFile(String filename) throws Exception
    {
        int i=0,j=0;
        File bFile=new File(FilesPath+".b");
        File vFile=new File(filename);
        Scanner bitmapScanner=new Scanner(bFile);
        BufferedWriter bfw=new BufferedWriter(new FileWriter(vFile));
        for(i=0;i<rows;i++)
        {
            String bittmp=bitmapScanner.nextLine();
            if(bittmp==null)
            {
                System.out.println("生成Var_tmp文件时，.b数据读取过程中意外中止："+i+"th line.");
                break;
            }
            String[] bitStrings=bittmp.split(" \t");
            for(j=0;j<columns;j++)
            {
                int flag=Integer.parseInt(bitStrings[j]);
                if(flag==0)//missing
                {
//                    int k=0;
//                    for(k=0;k< GlobalVariables.DIMENSION-1;k++)
//                    {
//                        sb.append(TotalData[i].data[k]).append("\t");
//                    }
//                    sb.append(TotalData[i].data[k]).append("\n");
                    bfw.write(i + "\t" + j + "\n");
                }
            }
        }

        bfw.flush();
        bfw.close();
    }
    public static void CreateBitmapFile(String filepath,double missingrate,int dimension) throws Exception
    {
        int instNum=0,i=0,j=0, ExistedInteger=0;
        int[][] bitmap;
        File datInputFile=new File(filepath+".dat");
        if(!datInputFile.exists())
        {
            System.out.println("未能找到指定.dat文件");
            return ;
        }
        Scanner datScanner=new Scanner(datInputFile);
        while(datScanner.hasNextLine())
        {
            String str = datScanner.nextLine();
            if(str.charAt(0)=='#')
                continue;
            instNum++;
        }
        datScanner.close();
        System.out.println("instNum: "+instNum+", dim: "+dimension+", rate: "+missingrate);

        ExistedInteger=(int)(instNum*dimension*(1-missingrate));
        if(ExistedInteger<instNum) throw new Exception("缺失率太高！");//存在数据点所有维度数据全部缺失

        bitmap=new int[instNum][dimension];
        Random r=new Random(System.currentTimeMillis());
        for(i=0;i<instNum;i++)
        {
            int tmp=r.nextInt(dimension);
            bitmap[i][tmp]=1;//每个点至少存在一维数据
        }
        int ones=(int)(instNum*dimension*(1-missingrate)-instNum);
        while(ones!=0)
        {
            int tmp=r.nextInt(instNum*dimension);
            int row=tmp/dimension;
            int column=tmp%dimension;
            if(bitmap[row][column]!=1)
            {
                bitmap[row][column]=1;
                ones--;
            }
        }

        WriteBitmapToFile(bitmap,filepath,instNum,dimension );
    }

    private static void WriteBitmapToFile(int[][] bitmap, String filepath, int instnum, int dimension) throws IOException
    {
        FileWriter fw=new FileWriter(new File(filepath+".b"));
        for(int i=0;i<instnum;i++){
            for(int j=0;j<dimension;j++){
                fw.write(""+bitmap[i][j]);
                if(j==dimension-1)
                    fw.write("\n");
                else
                    fw.write("\t");
            }
        }
        fw.flush();
        fw.close();
    }

    public static void main(String[] args)
    {
        try
        {
            CreateBitmapFile(GlobalVariables.FILEPATH,GlobalVariables.MISSINGRATE,GlobalVariables.DIMENSION);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
