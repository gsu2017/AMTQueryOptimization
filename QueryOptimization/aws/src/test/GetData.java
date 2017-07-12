package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

/**
 * Created by TLD7040A on 2017/7/9.
 */
public class GetData
{
    public static String file="C:\\Users\\TLD7040A\\IdeaProjects\\QueryOptimization\\ctables\\src\\data\\nba\\nba.txt";
    public static int Dimension=8;
    public static int Sum=5000;

    public static void main(String[] args) throws Exception
    {
        int i=0,j=0;
        Scanner scanner=new Scanner(new File(file));
        String nfilename= file.substring(0,file.lastIndexOf(".")) +"_"+String.valueOf(Dimension)+"d_"+String.valueOf(Sum)+".org";
        BufferedWriter bfw=new BufferedWriter(new FileWriter(new File(nfilename),true));
        String attrstr=scanner.nextLine();
        String[] attrs=attrstr.split("\t");
        bfw.write("#\tName\tYear\t"+attrs[4]+"\t"+attrs[6]+"\t"+attrs[7]+"\t"+attrs[8]+"\t"+attrs[10]+"\t"+attrs[11]+"\t"+attrs[12]+"\t"+attrs[15]+"\n");
        while(scanner.hasNextLine())
        {
            String str=scanner.nextLine();
            String[] strAry=str.split("\t");
            if(strAry.length==21&&Integer.parseInt(strAry[3]) >=1979 && i<5000)
            {
                StringBuilder sb=new StringBuilder();
                sb.append(strAry[1]).append(strAry[2]).append("\t").append(strAry[3]).append("\t");
                sb.append(strAry[4]).append("\t").append(strAry[6]).append("\t").append(strAry[7]).append("\t").append(strAry[8]).append("\t").append(strAry[10]).append("\t").append(strAry[11]).append("\t").append(strAry[12]).append("\t").append(strAry[15]).append("\n");
//                for(j=3;j<strAry.length;j++)
//                {
//                    if(j==strAry.length-1)
//                    {
//                        sb.append(strAry[j]).append("\n");
//                    }
//                    else
//                    {
//                        sb.append(strAry[j]).append("\t");
//                    }
//                }
                bfw.write(sb.toString());
                i++;
            }
        }
        bfw.flush();
        bfw.close();
        scanner.close();
    }
}
