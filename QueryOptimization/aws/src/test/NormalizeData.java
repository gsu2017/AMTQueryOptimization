package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * Created by TLD7040A on 2017/7/9.
 */
public class NormalizeData
{
    public static String file="C:\\Users\\TLD7040A\\IdeaProjects\\QueryOptimization\\ctables\\src\\data\\nba\\nba_8d_5000.org";
    public static boolean FixedIntervalNumber=true;
    public static int IntervalNum=10;
    public static int IntervalSize=0;
    public static int Dimension=8;
    public static int Sum=5000;

    public static void main(String[] args) throws Exception
    {
        int i=0,j=0;
//        String nfilename=file.substring(0,file.lastIndexOf("."))+"_"+IntervalNum+"i.dat";
        String nfilename=file.substring(0,file.lastIndexOf("."))+".dat";
        Scanner scanner=new Scanner(new File(file));
        BufferedWriter bfw=new BufferedWriter(new FileWriter(new File(nfilename)));
        ArrayList<ArrayList<Integer>> data=new ArrayList<>(Dimension);//每个维度一个arraylist
        //scanner.nextLine();
        for(i=0;i<Dimension;i++)
        {
            data.add(new ArrayList<Integer>());
        }
        while(scanner.hasNextLine())
        {
            String str=scanner.nextLine();
            if(str.charAt(0)=='#')
                continue;
            String[] strs= str.split("\t");
            if(strs.length==10)
            {
                for(i=0;i<Dimension;i++)
                {
                    data.get(i).add(Integer.parseInt(strs[i+2]));
                }
            }
        }

        scanner.close();
        ArrayList<Integer> intervals=new ArrayList<>();
        int min,max,interval;
        for(i=0;i<Dimension;i++)
        {
            max=Collections.max(data.get(i));
            min=Collections.min(data.get(i));
            interval=(int)Math.ceil((max-min)/(double)IntervalNum);
            bfw.write("###\t"+i+"\t"+min+"\t"+max+"\t"+interval+"\n");
            intervals.add(interval);
        }


        for(i=0;i<Sum;i++)
        {
            StringBuilder sb=new StringBuilder();
            for(j=0;j<Dimension;j++)
            {
                int tmp=data.get(j).get(i)/intervals.get(j);
                if(j==Dimension-1)
                    sb.append(tmp).append("\n");
                else
                    sb.append(tmp).append("\t");
            }
            bfw.write(sb.toString());
        }
        bfw.flush();
        bfw.close();
    }
}
