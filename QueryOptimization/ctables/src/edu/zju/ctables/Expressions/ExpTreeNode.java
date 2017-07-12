package edu.zju.ctables.Expressions;

import edu.zju.ctables.ArithUtil;

import java.util.*;

import static edu.zju.ctables.Expressions.ExpressionUtils.computeProbsForAllChildrenInSky;
import static edu.zju.ctables.Expressions.ExpressionUtils.updateProbDistributionForVarInSky;

/**
 * Created by TLD7040A on 2017/6/8.
 */


//构造函数中输入的expression表示该树节点的父节点表示的表达式，利用输入的condition生成该树节点表示的表达式，以及概率关系
public class ExpTreeNode
{
    //public Expression exp;
    public Expression cond;//指该节点在父节点时选择了cond，cond=A 或 !A
    public int childrenNum;//并非指孩子节点个数，而是指原子表达式个数
    public boolean isLeaf=false;
    public float condProb;
    public float entropy;
    public float[] Expectations;
    public float[] CPIs;
    public ArrayList<ExpTreeNode> ptrs=new ArrayList<ExpTreeNode>();//pointers to the children
    public Map<Expression,Float> expProbPairs;//<comparison, probability>
    public HashMap<Expression,Float[]> varDistributions=new HashMap<Expression,Float[]>();//<Var, probability distribution>
    public ArrayList<ArrayList<Expression>> expLists;//表达式表示成析取范式形式

    public ExpTreeNode(Expression a,HashMap<Expression,Float[]> vdist) throws Exception
    {
        cond=new PrimitiveValue(true);
        condProb=1;
        Expression tmp=a.toDNF();
        expLists=ExpressionUtils.GetLists(tmp);
        HashSet<Expression> children=GetAllExps();
        Expectations=new float[childrenNum];
        CPIs=new float[childrenNum];
        expProbPairs= computeProbsForAllChildrenInSky(children,vdist);//可以放在GetAllExps中

        for(Map.Entry<Expression,Float> entry:expProbPairs.entrySet())
        {
            //varDistributions.put(entry.getKey(), vdist.get(entry.getKey()));
            if(entry.getKey().lop.getClass()==Var.class)
                varDistributions.put(entry.getKey().lop,vdist.get(entry.getKey().lop));
            if(entry.getKey().rop.getClass()==Var.class)
                varDistributions.put(entry.getKey().rop,vdist.get(entry.getKey().rop));
        }

//        for(ArrayList<Expression> l:expLists)
//        {
//            for(Expression e:l)
//            {
//                if(e.lop.getClass()==Var.class)
//                    varDistributions.put(e.lop,vdist.get(e.lop));
//                if(e.rop.getClass()==Var.class)
//                    varDistributions.put(e.rop,vdist.get(e.rop));
//            }
//        }

        ComputeEntropy();
        GenerateChildrenPtrs();
//        for(Map.Entry<Expression,Float> entry:expProbPairs.entrySet())
//        {
//            //varDistributions.put(entry.getKey(),vdist.get(entry.getKey()));
//            ExpTreeNode tmpTreeNode=new ExpTreeNode(expLists,entry.getKey(),entry.getValue(),varDistributions);
//            ptrs.add(tmpTreeNode);
//            tmpTreeNode=new ExpTreeNode(expLists,new LogicFormula(entry.getKey(),Conj.NEGATE).DeMorgan(),1-entry.getValue(),varDistributions);
//            ptrs.add(tmpTreeNode);
//        }
    }

    public ExpTreeNode(ArrayList<ArrayList<Expression>> parentExpLists,Expression condition,float probability,HashMap<Expression,Float[]>vdist) throws Exception
    {
        cond=condition;
        condProb=probability;
        expLists=new ArrayList<ArrayList<Expression>>(parentExpLists.size()); //TODO 值传递OR克隆
        //ArrayList< ArrayList<Expression>> tmpexplist=new ArrayList<ArrayList<Expression>>( expLists.size());
        //Deep Copy of expLists
        for(ArrayList<Expression> l:parentExpLists)
        {
            ArrayList<Expression> tmplist=new ArrayList<Expression>(l.size());
            Iterator<Expression> tmpiter=l.iterator();
            while(tmpiter.hasNext())
            {
                tmplist.add(tmpiter.next().clone());
            }
            expLists.add(tmplist);
        }

        //Deep Copy of varDistribution
        Iterator<Map.Entry<Expression, Float[]>> mapiter=vdist.entrySet().iterator();
        while(mapiter.hasNext())
        {
            Map.Entry<Expression,Float[]> mapitem=mapiter.next();
            varDistributions.put(mapitem.getKey().clone(),mapitem.getValue().clone());
        }
        //首先将condition的条件满足
        RemoveConstExp(condition);
        if(expLists.size()==1 && expLists.get(0).size()==1)
        {
            isLeaf=true;
            if(expLists.get(0).get(0).getClass()==PrimitiveValue.class)
            {
                //叶子节点，此时条件为真或假
                entropy=0;
            }
            else
            {
                //叶子结点，此时条件为原子表达式
                ComputeEntropy();
                //entropy=-(condProb*(float)Logarithm.log(condProb,2)+(1-condProb)*(float)Logarithm.log(1-condProb,2));
            }
        }
//        if(expLists.get(0).get(0).getClass()==PrimitiveValue.class)
//        {
//            //若此时条件为真或假
//            isLeaf=true;
//            entropy=0;
//            //entropy=-(condProb*(float)Logarithm.log(condProb,2)+(1-condProb)*(float)Logarithm.log(1-condProb,2));
//        }
        else
        {
            //然后，更新与condition中的变量相关的表达式的概率分布
            if(condition.getClass()==LogicFormula.class)//!(x<y) => (x>=y) 符号取反
            {
                condition=new Comparison(condition.lop.lop,condition.lop.rop,Cmp.negate(((Comparison)(condition.lop)).op));
                //((Comparison) condition).op=Cmp.negate(((Comparison) condition).op);
            }
            if(condition.lop.getClass()==Var.class && condition.rop.getClass()==Var.class)
            {
                Float[] var1new= updateProbDistributionForVarInSky((Var) condition.lop,(Comparison) condition,vdist.get(condition.lop),vdist.get(condition.rop));
                Float[] var2new= updateProbDistributionForVarInSky((Var) condition.rop,(Comparison) condition,vdist.get(condition.rop),vdist.get(condition.lop));
                varDistributions.put(condition.lop,var1new);
                varDistributions.put(condition.rop,var2new);
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
                Float[] varNewDist = ExpressionUtils.updateProbDistributionForVarInSky(tmpVar,(Comparison) condition,vdist.get(tmpVar),tmpNumDist);
                varDistributions.put(tmpVar,varNewDist);
            }

            //varDistributions=vdist;
            HashSet<Expression> children=GetAllExps();
            Expectations=new float[childrenNum];
            CPIs=new float[childrenNum];
            expProbPairs = computeProbsForAllChildrenInSky(children,varDistributions);
            ComputeEntropy();
            //更新表达式expLists，condition满足后存在永真或永假的原子表达式
            for(Map.Entry<Expression,Float> entry:expProbPairs.entrySet())
            {
                if(entry.getValue()==1)
                    RemoveConstExp(entry.getKey());
                else if(entry.getValue()==0)
                    RemoveConstExp(new LogicFormula(entry.getKey(), Conj.NEGATE).DeMorgan());
            }
            if(expLists.get(0).get(0).getClass()==PrimitiveValue.class)
            {
                isLeaf=true;
                entropy=0;
            }
            else
            {
                GenerateChildrenPtrs();
                //ComputeCPIs();
            }

        }
//        for(Map.Entry<Expression,Float> entry:expProbPairs.entrySet())
//        {
//            //varDistributions.put(entry.getKey(),vdist.get(entry.getKey()));
//            if(entry.getValue()==1 || entry.getValue()==0)
//            {
//                continue;
//            }
//            ExpTreeNode tmpTreeNode=new ExpTreeNode(expLists,entry.getKey(),entry.getValue(),varDistributions);
//            ptrs.add(tmpTreeNode);
//            tmpTreeNode=new ExpTreeNode(expLists,new LogicFormula(entry.getKey(),Conj.NEGATE).DeMorgan(),1-entry.getValue(),varDistributions);
//            ptrs.add(tmpTreeNode);
//        }
    }

    //public void
    public void PrintTreeNode(int level)
    {
        if(isLeaf)
        {
            System.out.println("[LEVEL "+level+"] [Leaf Node] ["+cond.toString()+"] ["+expLists+"]");
            System.out.println("[LEVEL "+level+" END]");
        }
        else
        {
            System.out.println("[LEVEL "+level+"] [Non-Leaf Node] ["+cond.toString()+"] ["+expLists+"]");
            System.out.println("[LEVEL "+level+" END]");

            for(ExpTreeNode n:ptrs)
            {
                level++;
                n.PrintTreeNode(level);
            }
        }
    }

    public void GenerateChildrenPtrs() throws Exception
    {
        for(Map.Entry<Expression,Float> entry:expProbPairs.entrySet())
        {
            if(entry.getValue()==1 || entry.getValue()==0)
            {
                continue;
            }
            ExpTreeNode tmpTreeNode=new ExpTreeNode(expLists,entry.getKey(),entry.getValue(),varDistributions);
            ptrs.add(tmpTreeNode);
            tmpTreeNode=new ExpTreeNode(expLists,new LogicFormula(entry.getKey(),Conj.NEGATE).DeMorgan(),1-entry.getValue(),varDistributions);
            ptrs.add(tmpTreeNode);

        }
        ComputeCPIs();
    }
    public void RemoveConstExp(Expression condition)
    {
        boolean flag=true;
        Expression trueexp=Expression.makePrimitiveValue(true);
        Expression falseexp=Expression.makePrimitiveValue(false);
        if(condition.getClass()==LogicFormula.class)//有取反符号
        {
            flag=false;
            condition=condition.lop;
        }
        Iterator<ArrayList<Expression>> iter1=expLists.iterator();
        boolean flag2=false;
        while(iter1.hasNext())
        {
            ArrayList<Expression> l=iter1.next();
            Iterator<Expression> iter2=l.iterator();
            while(iter2.hasNext())
            {
                Expression e=iter2.next();
                if(e.equals(condition)&& flag)
                {
                    //l.remove(e);
                    iter2.remove();
                    if(l.size()==0)
                    {
                        //l.add(trueexp);
                        flag2=true;
                        break;
                    }
                }
                else if(e.equals(condition)&& !flag)
                {
                    l.clear();
                    l.add(falseexp);
                    break;
                }
            }


            if(flag2)
            {
                expLists.clear();
                l.add(trueexp);
                expLists.add(l);
                break;
            }
            else if(l.get(0).equals(falseexp))
            {
                //expLists.remove(l);
                iter1.remove();
                if(expLists.size()==0)
                {
                    ArrayList<Expression> ltmp=new ArrayList<Expression>();
                    ltmp.add(falseexp);
                    expLists.add(ltmp);
                    break;
                }
            }
        }
        HashSet<ArrayList<Expression>> tmphashset=new HashSet<ArrayList<Expression>>(expLists);
        expLists=new ArrayList<ArrayList<Expression>>(tmphashset);
//        for(ArrayList<Expression> l :expLists)
//        {
//            for(Expression e :l)
//            {
//                if(e.equals(condition)&&flag==true)
//                {
//                    l.remove(e);
//                    if(l.size()==0)
//                    {
//                        l.add(trueexp);
//                    }
//                }
//                else if(e.equals(condition)&&flag==false)
//                {
//                    l.clear();
//                    l.add(falseexp);
//                    break;
//                }
//
//            }
//            if(l.get(0).equals(trueexp))
//            {
//                expLists.clear();
//                expLists.add(l);
//            }
//            else if(l.get(0).equals(falseexp))
//            {
//                expLists.remove(l);
//                if(expLists.size()==0)
//                {
//                    ArrayList<Expression> ltmp=new ArrayList<Expression>();
//                    ltmp.add(falseexp);
//                    expLists.add(ltmp);
//                }
//            }
//        }
    }

    public HashSet<Expression> GetAllExps()
    {
        HashSet<Expression> rslt=new HashSet<Expression>();
        for(ArrayList<Expression> l :expLists)
        {
            for(Expression e :l)
            {
                rslt.add(e);
            }
        }
        childrenNum=rslt.size();
        return rslt;
    }

    public void ComputeEntropy() throws Exception
    {
        //float prob=GetProbabity(this.exp);
        float prob=GetProbability();
        entropy=-(prob*(float) ArithUtil.log(prob,2)+(1-prob)*(float)ArithUtil.log(1-prob,2));
        System.out.println("Probability: "+prob+", Entropy: "+entropy);
    }

    public HashSet<Var> GetAllVars()
    {
        HashSet<Var> rslt = new HashSet<Var>();
        for(ArrayList<Expression> l:expLists)
        {
            for(Expression e:l)
            {
                if(e.lop.getClass()==Var.class)
                    rslt.add((Var)e.lop);
                if(e.rop.getClass()==Var.class)
                    rslt.add((Var)e.rop);
            }
        }
        return rslt;
    }

    //list all possibilities

    public float GetProbability() throws Exception
    {
        float prob=0;
//        HashMap<HashMap<Var,Integer>,Float> possibs=new HashMap<HashMap<Var,Integer>,Float>();//记录所有可能取值

        int i=0,j=0,total=0,bits=0;
        int cnt1=0,cnt2=0;
        int[] bitRecord=new int[varDistributions.size()];

        for(Map.Entry<Expression,Float[]> entry: varDistributions.entrySet())
        {
            bitRecord[i]= (int) Math.ceil( ArithUtil.log(entry.getValue().length,2));
            bits+=bitRecord[i];
            i++;
        }
        total=(int)Math.pow(2,bits);
        for(i=0;i< total;i++)
        {
            j=0;
            boolean flag=true;
            float tmpprob=1;
            int[] tmp=Decoder.Decode(i,bitRecord);
            HashMap<Var,Integer> tmpmap=new HashMap<Var,Integer>();
            Iterator iter=varDistributions.keySet().iterator();
            while(iter.hasNext())
            {
                Var tmpvar=(Var) iter.next();
                if(tmp[j]<varDistributions.get(tmpvar).length && varDistributions.get(tmpvar)[tmp[j]]!=0)
                {
                    tmpmap.put(tmpvar,tmp[j]);
                    tmpprob*=varDistributions.get(tmpvar)[tmp[j]];
                    j++;
                }
                else
                {
                    flag=false;
                    break;
                }
            }
            if(flag)
            {
                //System.out.println("i="+i+", tmpmap="+tmpmap);
                cnt1++;
//                possibs.put(tmpmap,tmpprob);
                ArrayList< ArrayList<Expression>> tmpexplist=new ArrayList<ArrayList<Expression>>( expLists.size());
                //Deep Copy of expLists
                for(ArrayList<Expression> l:expLists)
                {
                    ArrayList<Expression> tmplist=new ArrayList<Expression>(l.size());
                    Iterator<Expression> tmpiter=l.iterator();
                    while(tmpiter.hasNext())
                    {
                        tmplist.add(tmpiter.next().clone());
                    }
                    tmpexplist.add(tmplist);
                }

                for(ArrayList<Expression> l:tmpexplist)
                {
                    boolean tmpflag=true;
                    for(Expression e:l)
                    {
                        if(e.lop.getClass()==Var.class)
                            e.lop=new PrimitiveValue(tmpmap.get(e.lop));
                        if(e.rop.getClass()==Var.class)
                            e.rop=new PrimitiveValue(tmpmap.get(e.rop));
                        if(!ExpressionUtils.isTrue((Comparison)e))
                        {
                            tmpflag=false;
                            break;
                        }
                    }
                    if(tmpflag)
                    {
                        cnt2++;
                        prob+=tmpprob;
                        //System.out.println("cnt2="+cnt2+", tmpmap="+tmpmap+", tmpprob="+tmpprob+", prob="+prob);
                        break;
                    }
                }
            }
        }
//        for(Map.Entry< HashMap<Var,Integer>,Float> item:possibs.entrySet())
//        {
//
//        }
        System.out.println("cnt1="+cnt1+", cnt2="+cnt2);
        //System.out.println(expLists);
        return prob;
    }

//    public float GetProbability()
//    {
//        float prob1,prob2;
//        prob1=0;
//        for(ArrayList<Expression> l:expLists)
//        {
//            prob2=1;
//            for(Expression e:l)
//            {
//                prob2*=expProbPairs.get(e);
//            }
//            prob1=1-(1-prob1)*(1-prob2);
//        }
//        return prob1;
//    }
//    public float GetProbabity(Expression a)
//    {
//        float rslt=0;
//        if(a.getClass()==Comparison.class)
//        {
//            return expProbPairs.get(a);
//        }
//        else
//        {
//            switch (((LogicFormula) a).op)
//            {
//                case NEGATE:
//                    rslt=GetProbabity(a.lop);
//                    break;
//                case AND:
//                    rslt=GetProbabity(a.lop)*GetProbabity(a.rop);
//                    break;
//                default:
//                    rslt=1-GetProbabity(a.lop)*GetProbabity(a.rop);
//            }
//        }
//        return rslt;
//    }

    public void ComputeCPIs() throws Exception
    {
        int i=0;
        for(i=0;i<childrenNum;i++)
        {
            //compute CPI for Option i
            Expectations[i]=ptrs.get(2*i).condProb*ptrs.get(2*i).entropy+ptrs.get(2*i+1).condProb*ptrs.get(2*i+1).entropy;
            CPIs[i]=entropy-Expectations[i];
            if(CPIs[i]<0)
            {
                throw new Exception("Invalid CPI!");
            }
        }
    }
}
