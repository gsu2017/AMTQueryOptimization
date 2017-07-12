package edu.zju.ctables;

import edu.zju.GlobalVariables;
import edu.zju.amt.HITUtils;
import edu.zju.amt.Task;
import edu.zju.ctables.BPlusTree.BPlusTree;
import edu.zju.ctables.BPlusTree.Key;
import edu.zju.ctables.BPlusTree.LeafNode;
import edu.zju.ctables.BPlusTree.SearchResult;
import edu.zju.ctables.Expressions.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * For the skyline query, the bigger, the better (Considering BplusTree index)
 * Created by TLD7040A on 2017/5/22.
 */


public class skylineQuery
{
    public ArrayList<Integer> skpoints=new ArrayList<>();

    public DataSet AllData;
    public int Dimension;
    public static String FilePath;
    public static String HITFile;
    public static String TaskFile;
    //public BPlusTree[] BPTrees;

    public HITUtils hitutils;
    public HashMap<String,Task> tasklist=new HashMap<>();
    public HashMap<Integer, ArrayList<ArrayList<Expression>>> conditionlist;
    public static String hittypeid="3OQLHYCQDKUGTTEYV6JVXK1BCXDDF1";
    public static int hitnum=0;

    //return whether pt1 dominates pt2
    public boolean dominate(int dim, int[] pt1, int[] pt2)
    {
        boolean ret=true;
        int flag=0;
        if(flag==dim)
            return false;
        for(int i=0;i<dim;i++)
        {
            if(pt1[i]<pt2[i])
            {
                ret=false;
                break;
            }
        }
        return ret;
    }

    public void BasicSkylineQuery()
    {
        int i=0,j=0;
        boolean flag=false;
        for(i=0;i<AllData.TotalData.length;i++)
        {
            flag=true;
            for(j=0;j<AllData.TotalData.length;j++)
            {
                if(j!=i)
                {
                    if(dominate(GlobalVariables.DIMENSION,AllData.TotalData[j].data,AllData.TotalData[i].data))
                    {
                        flag=false;
                        break;
                    }
                }
            }
            if(flag)
            {
                System.out.print(i+"\t");
            }
        }

        System.out.println("\n\n================= basic skyline query completed =====================\n\n");
    }


    public void LoadDataFromFile(String filepath,int dim) throws Exception
    {
        FilePath=filepath;
        HITFile=FilePath+"_hits.txt";
        TaskFile=FilePath+"_tasks.txt";
        Dimension=dim;
        AllData=new DataSet(FilePath,Dimension);
    }
    public BPlusTree[] BuildBplusTrees() throws Exception
    {
        int i=0,j=0;
        BPlusTree[] BPTrees=new BPlusTree[Dimension];
        for(i=0;i<Dimension;i++)
        {
            BPTrees[i]=new BPlusTree(2,4);
            for(j=0;j< AllData.rows;j++)
            {
                if(AllData.TotalData[j].data[i]!=-1)//insert into the bptree index
                {
                    Key k=new Key(AllData.TotalData[j].data[i]);
                    ArrayList<Integer> a=new ArrayList<Integer>();
                    a.add(AllData.TotalData[j].id);
                    BPTrees[i].insert(k,a);
                }
            }
        }
        return BPTrees;
    }

    //find all objects which may dominate the t th object in AllData
    //if no such object exist, corresponding ctable condition is set to be true,
    //otherwise, generate conditions
    //consider each no missing dimension respectively, then intersect result
    public ArrayList<Integer> FindAllPossibleObjects(int t,BPlusTree[] BPTrees)
    {
        int i=0,j=0,k=0,flag=-1;
        ArrayList<Integer> rslt=new ArrayList<Integer>();
        BitSet[] BitsetArray=new BitSet[Dimension];
        BitSet rsltBitset=new BitSet();//variable "flag" indicates whether rsltBitset is initialized, -1 means haven't, otherwise, means it's initialized by bitsetArray[i]
        //intersection
        for(i=0;i<Dimension;i++)
        {
            if(AllData.TotalData[t].missingValuesIndicator[i]==-1)//the i th dimension data of the t th object is missing, leave it alone
            {
                continue;
            }
            //search the corresponding b+ tree index, find objects whose values are better than the t th object in this b+ tree index
            BitsetArray[i]=new BitSet(AllData.rows);
            Key key=new Key(AllData.TotalData[t].data[i]);

            SearchResult sr=BPTrees[i].searchInfo(key);
            LeafNode lnode=sr.q;
            int idx=sr.i;
            while(lnode!=null)
            {
                for(j=idx;j<lnode.pointers.size();j++)
                {
                    for(k=0;k<lnode.pointers.get(j).idList.size();k++)
                    {
                        BitsetArray[i].set(Integer.parseInt(lnode.pointers.get(j).idList.get(k).toString()), true);
                    }
                }
                lnode=lnode.next;
                idx=0;
            }
//            for(j=sr.i;j<lnode.pointers.size();j++)
//            {
//                for(k=0;k<lnode.pointers.get(j).idList.size();k++)
//                {
//                    BitsetArray[i].set(Integer.parseInt( lnode.pointers.get(j).idList.get(k).toString()),true);
//                }
//            }
//            lnode=lnode.next;
//            while(lnode!=null)
//            {
//                for(k=0)
//            }
            if(flag==-1)
            {
                rsltBitset=BitsetArray[i];
                flag=i;
            }
            for(j=0;j<AllData.MissedObjs.get(i).size();j++)//objects that miss values in the i th dimension
            {
                BitsetArray[i].set(AllData.MissedObjs.get(i).get(j),true);
            }
        }

        //input bitsetarray matrix
//        for(i=0;i<Dimension;i++)
//        {
//            for(j=0;j<BitsetArray[i].size();j++)
//            {
//                if(BitsetArray[i].get(j))
//                {
//                    System.out.print(j+"\t");
//                }
//            }
//            System.out.println();
//        }

        //System.out.println("flag="+flag);
        //System.out.println(rsltBitset);
        for(i=0;i<Dimension;i++)
        {
            if(AllData.TotalData[t].missingValuesIndicator[i]!=-1 && flag!=i)//intersection
            {
                rsltBitset.and(BitsetArray[i]);
            }
        }
        //System.out.println(rsltBitset);
        for(i=0;i<rsltBitset.size();i++)
        {
            if(rsltBitset.get(i))
                rslt.add(i);
        }
        rslt.remove(Integer.valueOf(t));
        //System.out.println(rslt);
        return rslt;
    }

    //generate c-table conditions for the t th object, domSet is the set of all possible objects that dominate the t th object
    //得到一个表达式
    public ArrayList<ArrayList<Expression>> GenerateConditionsForObject(int t, ArrayList<Integer> domSet)
    {
        ArrayList<ArrayList<Expression>> rslt=new ArrayList<>();
        int expsize=0;
//        if(domSet.size()==0)
//        {
//            ArrayList<Expression> l=new ArrayList<>();
//            l.add(Expression.makePrimitiveValue(true));
//            rslt.add(l);
//            return rslt;
//        }
        DataItem tObj=AllData.TotalData[t];
        int i=0,j=0;

        //not dominated by any object in domSet
//        Expression exp=Expression.makePrimitiveValue(true);//as for AND, initiative true won't effect the result
        ArrayList<Expression> l=new ArrayList<>();
        DataItem tmpObj;
        for(i=0;i<domSet.size();i++)
        {
            l.clear();
            //ArrayList<Expression> l=new ArrayList<>();
            tmpObj=AllData.TotalData[domSet.get(i)];
//            Expression exp0=Expression.makePrimitiveValue(false);//as for OR, initiative false won't effect the result
            for(j=0;j<Dimension;j++)
            {
                Expression exp1;
                if(tObj.missingValuesIndicator[j]==-1&& tmpObj.missingValuesIndicator[j]==-1)
                {
                    exp1=Expression.makeComparison(Expression.makeVar(t,j),Expression.makeVar(tmpObj.id,j),Cmp.GT);
                    expsize++;
                }
                else if(tObj.missingValuesIndicator[j]!=-1&& tmpObj.missingValuesIndicator[j]==-1)
                {
                    exp1=Expression.makeComparison(Expression.makeVar(tmpObj.id,j),Expression.makePrimitiveValue(tObj.data[j]),Cmp.LT);
                    expsize++;
                }
                else if(tObj.missingValuesIndicator[j]==-1&& tmpObj.missingValuesIndicator[j]!=-1)
                {
                    exp1=Expression.makeComparison(Expression.makeVar(t,j),Expression.makePrimitiveValue(tmpObj.data[j]), Cmp.GT);
                    expsize++;
                }
                else//点t在维度j上的属性值必然小于点i在该维度上的值
                {
                    continue;
                }
                l.add(exp1);
            }
            if(l.size()!=0)
            {
                rslt.add(l);
            }
            else//l.size()=0,意味着点t必然被当前点i支配
            {
                rslt.clear();
                l.add(Expression.makePrimitiveValue(false));
                rslt.add(l);
                return rslt;
            }
        }
        System.out.println(expsize+"\t");
        if(expsize>GlobalVariables.MaxExpNum)
            GlobalVariables.MaxExpNum=expsize;
        return rslt;
    }

    //将tasklist中的表达式设置为得到的crowdsourcing 结果
    //直接根据表达式的真值更新相应变量的概率分布
    //直接根据表达式的真值移除表达式，更新相应对象的condition
    public void SetTaskResults(HashMap<String,Boolean> results)
    {
//        ArrayList<ArrayList<Expression>> rslt=new ArrayList<>();
//        ArrayList<Expression> trueList=new ArrayList<>();
//        ArrayList<Expression> falseList=new ArrayList<>();
        for(Map.Entry<String,Boolean> entry:results.entrySet())
        {
            Task tsk = tasklist.get(entry.getKey());
            tsk.isTrue=entry.getValue();
            if(tsk.isTrue)
            {
//                trueList.add(tsk.question);
                RemoveConstExp(tsk.question,this.conditionlist.get(tsk.oid));
            }
            else
            {
//                falseList.add(tsk.question);
                RemoveConstExp(Expression.makeLogicFormula(tsk.question,Conj.NEGATE),this.conditionlist.get(tsk.oid));
            }
            UpdateDistributionGivenTask(tsk);
        }
//        rslt.add(trueList);
//        rslt.add(falseList);
//        return rslt;
        UpdateSkylintPoints();
    }

    public void RemoveConstExp(Expression e,ArrayList<ArrayList<Expression>> list)
    {
        ExpressionUtils.RemoveConstExp(e,list);
    }

    public void UpdateDistributionGivenTask(Task tsk)
    {
        HashMap<Expression,Float[]> rslt=AllData.varsDistribution;

        Expression condition=tsk.question;
        if(!tsk.isTrue)
        {
            condition= ExpressionUtils.CmpNegate(condition);
        }
        //更新condition中涉及变量的概率分布情况
        if(condition.lop.EXPTYPE==ExpressionType.VARIABLE && condition.rop.EXPTYPE==ExpressionType.VARIABLE)
        {
            Float[] var1new=ExpressionUtils.updateProbDistributionForVarInSky((Var) condition.lop,(Comparison) condition,rslt.get(condition.lop),rslt.get(condition.rop));
            Float[] var2new=ExpressionUtils.updateProbDistributionForVarInSky((Var) condition.rop,(Comparison) condition,rslt.get(condition.rop),rslt.get(condition.lop));
            rslt.put(condition.lop,var1new);
            rslt.put(condition.rop,var2new);
        }
        else
        {
            Var tmpVar;
            int tmpNum;
            if(condition.lop.getClass()== Var.class)
            {
                tmpVar=(Var)condition.lop;
                tmpNum=Integer.parseInt(condition.rop.toString());
            }
            else
            {
                tmpVar=(Var)condition.rop;
                tmpNum=Integer.parseInt(condition.lop.toString());
            }
            Float[] tmpNumDist=new Float[tmpNum+1];
            for(int i=0;i<tmpNum;i++) tmpNumDist[i]=(float)0;
            tmpNumDist[tmpNum]=(float)1;
            Float[] varNewDist=ExpressionUtils.updateProbDistributionForVarInSky(tmpVar,(Comparison) condition,rslt.get(tmpVar),tmpNumDist);
            rslt.put(tmpVar,varNewDist);
        }
    }

    //对每个condition计算概率，更新表达式
    public void ComputeProbability() throws Exception
    {
        //int i=0;
        for(Map.Entry<Integer,ArrayList<ArrayList<Expression>>> e:conditionlist.entrySet())
        {
            ArrayList<ArrayList<Expression>> tmp=e.getValue();
            HashSet<Expression> exps=ExpressionUtils.GetAllExps(tmp);
            Map<Expression,Float> probs=ExpressionUtils.computeProbsForAllChildrenInSky(exps,AllData.varsDistribution);

            for(Map.Entry<Expression,Float> entry:probs.entrySet())
            {
                if(Math.abs(entry.getValue()-0)<1.0E-4)// TODO: 2017/7/7 考虑精度问题，利用近似等于
                {
                    RemoveConstExp(Expression.makeLogicFormula(entry.getKey(),Conj.NEGATE),tmp);
                }
                else if(Math.abs(entry.getValue()-1)<1.0E-4)
                {
                    RemoveConstExp(entry.getKey(),tmp);
                }
            }
        }
        UpdateSkylintPoints();
//        ArrayList<Expression> allExps=new ArrayList<>();
//        for(i=0;i<conditionlist.size();i++)
//        {
//            allExps.addAll(ExpressionUtils.GetAllExps(conditionlist.get(i)));
//        }
//        HashSet<Expression> exps=new HashSet<>(allExps);
//        Map<Expression,Float> probs=ExpressionUtils.computeProbsForAllChildrenInSky(exps,AllData.varsDistribution);
//        for(Map.Entry<Expression,Float> entry:probs.entrySet())
//        {
//            if(entry.getValue()==0)
//            {
//
//            }
//            else if(entry.getValue()==1)
//            {
//
//            }
//        }
    }
    public void WriteTasksToFile(int round) throws IOException
    {
        File file=new File(FilePath+"tasks.txt");
        BufferedWriter bfw=new BufferedWriter(new FileWriter(file,true));
        StringBuilder sb=new StringBuilder("Round");
        sb.append(String.valueOf(round)).append("\n");
        for(Map.Entry<String,Task> entry:tasklist.entrySet())
        {
            Task tsk=entry.getValue();
            sb.append(entry.getKey()).append("\t");
            sb.append(tsk.oid).append("\t").append(tsk.aid).append("\t").append(tsk.question).append("\t").append(tsk.isTrue).append("\n");
        }
        bfw.write(sb.toString());
        bfw.write("\n\n");
        bfw.flush();
        bfw.close();
    }

    public void PrintConditionList()
    {
        for(Map.Entry<Integer, ArrayList<ArrayList<Expression>>> entry:conditionlist.entrySet())
        {
            System.out.println("i="+entry.getKey()+"\texps="+entry.getValue());
        }
    }

    public void UpdateSkylintPoints()
    {
//        for(Map.Entry<Integer,ArrayList<ArrayList<Expression>>> entry:conditionlist.entrySet())
//        {
//            if(entry.getValue().get(0).get(0).equals(Expression.makePrimitiveValue(true)))
//            {
//                skpoints.add(entry.getKey());
//            }
//        }
        Iterator<Map.Entry<Integer, ArrayList<ArrayList<Expression>>>> iter=conditionlist.entrySet().iterator();
        while(iter.hasNext())
        {
            Map.Entry<Integer,ArrayList<ArrayList<Expression>>> tmp=iter.next();
            if(tmp.getValue().get(0).get(0).EXPTYPE==ExpressionType.PRIMITIVE)
            {
                if(tmp.getValue().get(0).get(0).equals(Expression.makePrimitiveValue(true)))
                {
                    skpoints.add(tmp.getKey());
                }
                iter.remove();
            }
        }
    }


    public static void main(String[] args) throws Exception
    {
        String FilePath= GlobalVariables.FILEPATH;
        String dUrl=GlobalVariables.INSTURL;
        int dimension=GlobalVariables.DIMENSION;
        int numEachRound=GlobalVariables.QNUMPERROUND;
        int MaxQNum=GlobalVariables.MAXQNUM;

        int round=0;
        String XmlFile;

        skylineQuery sq=new skylineQuery();
        sq.LoadDataFromFile(FilePath,dimension);
        sq.BasicSkylineQuery();
        BPlusTree[] bptrees=sq.BuildBplusTrees();

//        for(int i=0;i<dimension;i++)
//        {
//            System.out.println("==============B+Tree INDEX for DIM = "+i+"===============");
//            System.out.println(sq.BPTrees[i].print());
//        }
        System.out.println("================CTABLE CONDITONS=================\n");

        /*********************************************************************************/
//        int i=0;
//        Expression e0=Expression.makeVar(1,1);
//        Expression e1=Expression.makeVar(4,1);
//        Expression e2=Expression.makeVar(2,2);
//        Expression e3=Expression.makeVar(4,2);
//        Expression e4=Expression.makeVar(4,3);
//        Float[] d0=new Float[10];
//        Float[] d1=new Float[10];
//        Float[] d2=new Float[8];
//        Float[] d3=new Float[8];
//        Float[] d4=new Float[6];
//        for(i=0;i<d0.length;i++) d0[i]=0.1f;
//        for(i=0;i<d1.length;i++) d1[i]=0.1f;
//        for(i=0;i<d2.length;i++) d2[i]=0.125f;
//        for(i=0;i<d3.length;i++) d3[i]=0.125f;
//        d4[0]=0.1f;
//        d4[1]=0.1f;
//        d4[2]=0.2f;
//        d4[3]=0.2f;
//        d4[4]=0.3f;
//        d4[5]=0.1f;
//
//        HashMap<Expression,Float[]> distribution=new HashMap<Expression,Float[]>();
//        distribution.put(e0,d0);
//        distribution.put(e1,d1);
//        distribution.put(e2,d2);
//        distribution.put(e3,d3);
//        distribution.put(e4,d4);
//
        int i=0;
        HashMap<Integer, ArrayList<ArrayList<Expression>>> conds=new HashMap<>();
        Expression trueexp=Expression.makePrimitiveValue(true);
        int CntForDominatedPoints=0;
        //BufferedWriter tmpfilebfw=new BufferedWriter(new FileWriter(new File(".//tmpfile.txt")));
        for(i=0;i<sq.AllData.TotalData.length;i++)
        {
            ArrayList<Integer> doms=sq.FindAllPossibleObjects(i,bptrees);
            if(doms.size()==0)//case: dom集合为空，不被任何点支配，故一定是skyline点
            {
                sq.skpoints.add(i);
                continue;
            }
            ArrayList<ArrayList<Expression>> exps=sq.GenerateConditionsForObject(i,doms);
            //System.out.println("i="+i+", exps="+exps);
            //tmpfilebfw.write("i="+i+"\tDom="+doms.size()+"\n");
            System.out.print(i+"\t");
//            if(i==164)
//            {
//                System.out.println("Stop");
//            }

//            if(i%100==0)
//                System.out.print("\n");
            if(exps.get(0).get(0).toString().toLowerCase().equals("false"))
            {
                //能够在这一步明确判断出被支配，该点数据必然是完整的，且存在另一个完整数据点支配该点
                CntForDominatedPoints++;
            }
            else
            {
                conds.put(i,exps);
//                Options op=new Options(exps,sq.AllData.varsDistribution);
//                for(Map.Entry<Expression, Float> e:op.CPIs)
//                {
//                    System.out.println("CPI="+e.getKey()+"\tEXP="+e.getValue());
//                }

            }

        }
//        tmpfilebfw.flush();
//        tmpfilebfw.close();
        System.out.println("Dominated Points Number: "+CntForDominatedPoints);
        System.out.println("Maxium Expression Number: "+CntForDominatedPoints);
        //*************** crowdsourcing begin *****************//
        sq.conditionlist=conds;
        sq.hitutils=new HITUtils(HITUtils.getSandboxClient());

        if(hittypeid.equals(""))
        {
            hittypeid=sq.hitutils.CreateHITTYPE();
        }
        sq.hitutils.SetHITTypeId(hittypeid);
        //利用决策树选择问题，生成XML文件
        //************** iterating **************//
        while(!(sq.conditionlist.size()==0||hitnum>=MaxQNum))
        {
            System.out.println("Round"+round+"\n");
            //sq.PrintConditionList();
            Conditions options=new Conditions(conds,sq.AllData);
            String hitid="";
            XmlFile=FilePath+String.valueOf(round)+".xml";
            //dUrl="https://s3-us-west-2.amazonaws.com/zju-crowdsourcing-instructions/NBAInstruction0.png";
            //numEachRound=3;
            int qnum = options.GenerateXMLFile(numEachRound,XmlFile,dUrl);//该轮中真正发布的问题个数
            //发布HIT,更新静态变量的hit个数
            boolean flag=false;
            while(!flag)//HIT发布成功方跳出循环
            {
                try
                {
                    hitid = sq.hitutils.CreateHIT(XmlFile,qnum,hitnum);
                    File file = new File(skylineQuery.HITFile);
                    BufferedWriter bfw = new BufferedWriter(new FileWriter(file,true));
                    bfw.write("Round="+round+"\tHITID="+ hitid+"\tQNUM="+qnum+"\tSTART="+hitnum+"\tXMLFile="+XmlFile+"\n");
                    bfw.flush();
                    bfw.close();
                    hitnum+=qnum;
                    flag=true;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("Catch exception while creating HITs, try again after 5 seconds.");
                    Thread.sleep((long) 5000);
                }
            }
            sq.tasklist.putAll(options.GetTasks());//记录已经发布在AMT上的task
            //接收答案
//        int qnum=3;
//        sq.hitnum=3;
//        hitid="3T2HW4QDUWADX09PZWJCJ25RP7L9C2";
            while(!sq.hitutils.IsHITReviewable(hitid))//当HIT成为reviewable状态方跳出循环
            {
                Thread.sleep((long)1000*60);//sleep not until hit becomes reviewable
            }
            HashMap<String,Boolean> tskResult = sq.hitutils.CollectAnswer(hitid,qnum);
            sq.SetTaskResults(tskResult);
            //更新（先更新概率分布，再计算概率，根据概率为0或1的表达式更新整个ctable，更新完毕后，如有必要更新skyline结果集合，再进入下一轮）
            sq.ComputeProbability();
            sq.WriteTasksToFile(round);
            System.out.println("Skyline points:");
            System.out.println(sq.skpoints);
            round++;
            //TotalQNum+=qnum;
        }
        System.out.println("Completed!");
        System.out.println("Total question number: "+hitnum);
    }
}
