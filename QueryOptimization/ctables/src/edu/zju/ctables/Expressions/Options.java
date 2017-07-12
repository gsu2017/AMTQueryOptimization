package edu.zju.ctables.Expressions;

import edu.zju.ctables.ArithUtil;

import java.util.*;


/**
 * Created by TLD7040A on 2017/6/21.
 * 对于表达式(如A|(B&C))，Options表示所有可能的选择，记录CPIs
 */
public class Options
{
    public float entropy;
    public float prob;//Options 可能永真或永假，体现在概率上
    public HashMap<Expression,Float> Expectations=new HashMap<>();
    public List<Map.Entry<Expression,Float>> CPIs;//考虑到CPI需要进行排序，因此采用arraylist,按照CPI由大到小排序
    public Map<Expression,Float> expProbPairs=new HashMap<Expression,Float>();//<comparison, probability>
    public HashMap<Expression,Float[]> varDistributions=new HashMap<Expression,Float[]>();//<Var, probability distribution>
    public ArrayList<ArrayList<Expression>> expLists;//表达式表示成合取范式形式

//    public Options(Expression a,HashMap<Expression,Float[]> vdist) throws Exception
//    {
//        HashMap<Expression,Float> CPIsmap=new HashMap<>();
//        Expression tmp=a.toDNF();
//
//        expLists=ExpressionUtils.GetLists(tmp);
//        expProbPairs=ExpressionUtils.computeProbsForAllChildrenInSky(GetAllExps(),vdist);
//        SetVarDistribution(vdist);
//        prob=ComputeProbability();
//        entropy=ComputeEntropy(prob);
//
//        for(Map.Entry<Expression,Float> entry:expProbPairs.entrySet())
//        {
//            float prob1,prob2,entropy1,entropy2,expectation;
//
//            Float prob=entry.getValue();
//            Expression e=entry.getKey();
//
//            Expression note=new LogicFormula(e,Conj.NEGATE);//not e (!e)
//            HashMap<Expression,Float[]> tmpdist1=UpdateDistributionGivenCondition(e);
//            ArrayList<ArrayList<Expression>> tmplist1=RemoveConstExp(e);
//            prob1=ExpressionUtils.ComputeProbability(tmplist1,tmpdist1);
//            entropy1=ComputeEntropy(prob1);
//            if(e.equals(new Comparison(new Var(1,1),new Var(4,1), Cmp.LT)))
//            {
//                ArrayList<ArrayList<Expression>> testlist=new ArrayList<>();
//                HashMap<Expression,Float[]> testdist=new HashMap<>();
//                for(ArrayList<Expression> l:tmplist1)
//                {
//                    if(l.size()==1)
//                    {
//                        testlist.add(l);
//                        testdist.put(l.get(0).lop,tmpdist1.get(l.get(0).lop));
//                    }
//                }
//                float testprob=ExpressionUtils.ComputeProbability(testlist,testdist);
//                System.out.println("test for probability computation: "+testprob);
//            }
//            HashMap<Expression,Float[]> tmpdist2=UpdateDistributionGivenCondition(note);
//            ArrayList<ArrayList<Expression>> tmplist2=RemoveConstExp(note);
//            prob2=ExpressionUtils.ComputeProbability(tmplist2,tmpdist2);
//            entropy2=ComputeEntropy(prob2);
//
//            expectation=ArithUtil.add(ArithUtil.mul(entropy1,prob),ArithUtil.mul(entropy2,ArithUtil.sub(1,prob)));
////            expectation=entropy1*prob+entropy2*(1-prob);
//            Expectations.put(e,expectation);
////            CPIsmap.put(e,entropy-expectation);
//            CPIsmap.put(e,ArithUtil.sub(entropy,expectation));
//        }
//
//        CPIs=new ArrayList<>(CPIsmap.entrySet());
//        Collections.sort(CPIs, new Comparator<Map.Entry<Expression, Float>>()
//        {
//            @Override
//            public int compare(Map.Entry<Expression, Float> o1, Map.Entry<Expression, Float> o2)
//            {
//                return o2.getValue().compareTo(o1.getValue());
//            }
//        });
////        for(Map.Entry<Expression,Float> entry:CPIs)
////        {
////            System.out.println(entry.getKey()+"\t"+entry.getValue());
////        }
//    }

    public Options(ArrayList<ArrayList<Expression>> explist,HashMap<Expression,Float[]> vdist) throws Exception
    {
        HashMap<Expression,Float> CPIsmap=new HashMap<>();
        expLists=new ArrayList<ArrayList<Expression>>(explist.size());
        //Deep Copy of expLists
        for(ArrayList<Expression> l:explist)
        {
            ArrayList<Expression> tmplist=new ArrayList<Expression>(l.size());
            Iterator<Expression> tmpiter=l.iterator();
            while(tmpiter.hasNext())
            {
                tmplist.add(tmpiter.next().clone());
            }
            expLists.add(tmplist);
        }

        if(explist.get(0).get(0).equals(new PrimitiveValue(true)))
        {
            prob=1;
            entropy=0;
        }
        else if(explist.get(0).get(0).equals(new PrimitiveValue(false)))
        {
            prob=0;
            entropy=0;
        }
        else
        {
            expProbPairs=ExpressionUtils.computeProbsForAllChildrenInSky(GetAllExps(),vdist);
            SetVarDistribution(vdist);
            prob=ComputeProbability();
            entropy=ComputeEntropy(prob);

            for(Map.Entry<Expression,Float> entry:expProbPairs.entrySet())
            {
                float prob1,prob2,entropy1,entropy2,expectation;

                Float prob=entry.getValue();
                Expression e=entry.getKey();

                Expression note=new LogicFormula(e,Conj.NEGATE);//not e (!e)
                HashMap<Expression,Float[]> tmpdist1=UpdateDistributionGivenCondition(e);
                ArrayList<ArrayList<Expression>> tmplist1=RemoveConstExp(e);
                prob1=ExpressionUtils.ComputeProbability(tmplist1,tmpdist1);
                entropy1=ComputeEntropy(prob1);

                HashMap<Expression,Float[]> tmpdist2=UpdateDistributionGivenCondition(note);
                ArrayList<ArrayList<Expression>> tmplist2=RemoveConstExp(note);
                prob2=ExpressionUtils.ComputeProbability(tmplist2,tmpdist2);
                entropy2=ComputeEntropy(prob2);

                expectation=entropy1*prob+entropy2*(1-prob);
                Expectations.put(e,expectation);
                CPIsmap.put(e,entropy-expectation);
            }

            CPIs=new ArrayList<>(CPIsmap.entrySet());
            Collections.sort(CPIs, new Comparator<Map.Entry<Expression, Float>>()
            {
                @Override
                public int compare(Map.Entry<Expression, Float> o1, Map.Entry<Expression, Float> o2)
                {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
//            for(Map.Entry<Expression,Float> entry:CPIs)
//            {
//                System.out.println(entry.getKey()+"\t"+entry.getValue());
//            }

        }

    }
    public void PrintExplists()
    {
        PrintExplists(expLists);
    }
    public void PrintExplists(ArrayList<ArrayList<Expression>> list)
    {
        Iterator iter1=list.iterator();
        while(iter1.hasNext())
        {
            ArrayList<Expression> l= (ArrayList<Expression>) iter1.next();
            Iterator iter2=l.iterator();
            System.out.print("(");
            while(iter2.hasNext())
            {
                Expression e=(Expression) iter2.next();
                if(iter2.hasNext())
                    System.out.print(e+" | ");
                else
                    System.out.print(e);
            }
            if(iter1.hasNext())
                System.out.print(") & ");
            else
                System.out.println(")");
        }
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
        return rslt;
    }
    //
    public ArrayList<ArrayList<Expression>> RemoveConstExp(Expression condition) throws Exception
    {
        //deep copy of explists
        ArrayList<ArrayList<Expression>> rslt=new ArrayList<ArrayList<Expression>>(expLists.size());
        //deep copy of expLists
        for(ArrayList<Expression> l:expLists)
        {
            ArrayList<Expression> tmplist=new ArrayList<Expression>(l.size());
            Iterator<Expression> tmpiter=l.iterator();
            while(tmpiter.hasNext())
            {
                tmplist.add(tmpiter.next().clone());
            }
            rslt.add(tmplist);
        }
        return ExpressionUtils.RemoveConstExp(condition,rslt);
//        ArrayList<ArrayList<Expression>> rslt=new ArrayList<ArrayList<Expression>>(expLists.size());
//        condition=condition.DeMorgan();
//        //deep copy of expLists
//        for(ArrayList<Expression> l:expLists)
//        {
//            ArrayList<Expression> tmplist=new ArrayList<Expression>(l.size());
//            Iterator<Expression> tmpiter=l.iterator();
//            while(tmpiter.hasNext())
//            {
//                tmplist.add(tmpiter.next().clone());
//            }
//            rslt.add(tmplist);
//        }
//
//        boolean flag=true;
//        Expression trueexp=Expression.makePrimitiveValue(true);
//        Expression falseexp=Expression.makePrimitiveValue(false);
//        if(condition.getClass()==LogicFormula.class)//有取反符号
//        {
//            flag=false;
//            condition=condition.lop;
//        }
//        Iterator<ArrayList<Expression>> iter1=rslt.iterator();
//        boolean flag2=true;
//        while(iter1.hasNext())
//        {
//            ArrayList<Expression> l=iter1.next();
//
////            int idx=l.indexOf(condition);//可以直接替换下述update过程（from begin to end）
////            if(idx>=0)//
////            {
////                if(flag==true)
////                {
////                    iter1.remove();
////                }
////                else
////                {
////                    l.remove(idx);
////                    if(l.size()==0)
////                    {
////                        flag2= false;
////                        l.add(falseexp);
////                        rslt.clear();
////                        rslt.add(l);
////                        break;
////                    }
////                }
////            }
//            /**************** update start ******************/
//            if(l.contains(condition))//行中包含condition
//            {
//                if(flag==true)//整行移除
//                {
//                    iter1.remove();
//                }
//                else//将condition移除
//                {
//                    Iterator<Expression> iter2=l.iterator();
//                    while(iter2.hasNext())
//                    {
//                        Expression e=iter2.next();
//                        if(e.equals(condition))
//                        {
//                            iter2.remove();
//                        }
//                    }
//                    if(l.size()==0)//移除condition之后，该行为空了，此时整行为false，因而整个表达式为false
//                    {
//                        flag2= false;
//                        l.add(falseexp);
//                        rslt.clear();
//                        rslt.add(l);
//                        break;
//                    }
//                }
//            }
//            /**************** update end ******************/
//
//        }
//        if(flag2&&rslt.size()==0)
//        {
//            ArrayList<Expression> l=new ArrayList<>();
//            l.add(trueexp);
//            rslt.add(l);
//        }
//        HashSet<ArrayList<Expression>> tmphashset=new HashSet<ArrayList<Expression>>(rslt);
//        //TODO rslt进一步化简
//        rslt=new ArrayList<ArrayList<Expression>>(tmphashset);
//
//        return rslt;
    }

    //选择表达式condition，得到其新的概率分布
    public HashMap<Expression,Float[]> UpdateDistributionGivenCondition(Expression condition) throws Exception
    {
        HashMap<Expression,Float[]> rslt=new HashMap<>(varDistributions.size());

        //deep copy of vardistribution
        Iterator<Map.Entry<Expression, Float[]>> mapiter=varDistributions.entrySet().iterator();
        while(mapiter.hasNext())
        {
            Map.Entry<Expression,Float[]> mapitem=mapiter.next();
            rslt.put(mapitem.getKey().clone(),mapitem.getValue().clone());
        }

        //若condition存在“！”,则对condition的符号取反，如!(x<y) => (x>=y)
        if(condition.getClass()==LogicFormula.class)
        {
            condition=new Comparison(condition.lop.lop,condition.lop.rop,Cmp.negate(((Comparison)(condition.lop)).op));
        }

        //更新condition中涉及变量的概率分布情况
        if(condition.lop.getClass()==Var.class && condition.rop.getClass()==Var.class)
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

        return rslt;
    }

    public float ComputeEntropy(float prob)
    {
        if(prob==1 || prob==0) return 0;
        //float rslt=-(prob*(float) ArithUtil.log(prob,2)+(1-prob)*(float)ArithUtil.log(1-prob,2));
        return ArithUtil.sub(0,ArithUtil.add(ArithUtil.mul(prob,ArithUtil.log(prob,2)),ArithUtil.mul(ArithUtil.sub(1,prob),ArithUtil.log(ArithUtil.sub(1,prob),2))));
    }
    public float ComputeProbability() throws Exception
    {
        return ExpressionUtils.ComputeProbability(expLists,varDistributions);
    }


    public void SetVarDistribution(HashMap<Expression,Float[]> vdist)
    {
        for(ArrayList<Expression> l :expLists)
        {
            for(Expression e :l)
            {
                if(e.lop.getClass()==Var.class && !varDistributions.containsKey(e.lop))
                {
                    varDistributions.put(e.lop,vdist.get(e.lop));
                }
                if(e.rop.getClass()==Var.class && !varDistributions.containsKey(e.rop))
                {
                    varDistributions.put(e.rop,vdist.get(e.rop));
                }
            }
        }
    }

    public List<Map.Entry<Expression,Float>> GetCPIs()
    {
        return CPIs;
    }

    public ArrayList<Expression> GetAllExpGivenVar(Expression var)
    {
        ArrayList<Expression> rslt=new ArrayList<>();
        for(ArrayList<Expression> l:expLists)
        {
            for(Expression e:l)
            {
                if(e.lop.equals(var)||e.rop.equals(var))
                {
                    rslt.add(e);
                }
            }
        }
        return rslt;
    }

    public static void main(String[] args) throws Exception
    {
        Expression a=new Comparison(new Var(1,1),new PrimitiveValue(1),Cmp.EQ);
        Expression b=new Comparison(new Var(1,2),new PrimitiveValue(2),Cmp.EQ);
        Expression c=new Comparison(new Var(1,3),new PrimitiveValue(3),Cmp.EQ);

        ArrayList<ArrayList<Expression>> exp=new ArrayList<>();
        ArrayList<Expression> tmp1=new ArrayList<>();
        ArrayList<Expression> tmp2=new ArrayList<>();
        tmp1.add(a);
        tmp1.add(b);
        exp.add(tmp1);
        tmp2.add(a);
        tmp2.add(c);
        exp.add(tmp2);
//        Expression exp=new LogicFormula(a,new LogicFormula(b,c,Conj.AND), Conj.OR);

        HashMap<Expression,Float[]> distribution=new HashMap<>();
        Float[] adist={(float) 0.2,(float)0.8};
        Float[] bdist={(float) 0.2,(float)0.3,(float)0.4,(float)0.1};
        Float[] cdist={(float) 0.2,(float)0.1,(float)0.1,(float)0.5,(float)0.1};
        distribution.put(new Var(1,1),adist);
        distribution.put(new Var(1,2),bdist);
        distribution.put(new Var(1,3),cdist);

        Options op=new Options(exp,distribution);
//        for(Map.Entry<Expression,Float> entry:op.CPIs)
//        {
//            System.out.println(entry.getKey()+"\t"+entry.getValue());
//        }
        Expression cond=op.CPIs.get(1).getKey();
        System.out.println(ExpressionUtils.ComputeProbability(op.RemoveConstExp(cond),op.UpdateDistributionGivenCondition(cond)));
        cond=new LogicFormula(cond,Conj.NEGATE);
        Options op2=new Options(op.RemoveConstExp(cond),op.UpdateDistributionGivenCondition(cond));
        op.PrintExplists();
        op2.PrintExplists();

    }
}
