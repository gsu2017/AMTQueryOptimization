package edu.zju;

/**
 * Created by TLD7040A on 2017/7/8.
 */
public class GlobalVariables
{
    public static String FILEPATH="C:\\Users\\TLD7040A\\IdeaProjects\\QueryOptimization\\ctables\\src\\data\\nba\\nba_8d_5000";
    public static String INSTURL="https://s3-us-west-2.amazonaws.com/zju-crowdsourcing-instructions/NBAInstruction0.png";//url of instruction pictures
    public static int DIMENSION=8;
    public static int QNUMPERROUND=50;
    public static int MAXQNUM=2000;
    public static double MISSINGRATE=0.1;

    public static int MaxExpNum=0;
    /**
     * Created by TLD7040A on 2017/7/2.
     */

    public static enum AttributeName
    {
        //gp,minutes,pts,oreb,dreb,reb,asts,stl,blk,turnover,pf,fgm,ftm,tpm,fgmiss,ftmiss,tpmiss;
        gp, pts, oreb, dreb, asts, stl, blk, fgm;
    }
}
