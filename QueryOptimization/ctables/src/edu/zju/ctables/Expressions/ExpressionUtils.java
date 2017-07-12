package edu.zju.ctables.Expressions;

import edu.zju.ctables.ArithUtil;
import edu.zju.ctables.DataSet;

import java.util.*;

/**
 * Created by TLD7040A on 2017/6/7.
 */

class Decoder
{
    //num表示为1 的位数，left表示左移位数
    public static int GenTmp(int num,int left)
    {
        int rslt=(int)Math.pow(2.0,(double) num)-1;
        rslt=rslt<<left;
        //System.out.println(Integer.toBinaryString(rslt));
        return rslt;
    }
    public static int[] Decode(int num,int[] cnt)
    {
        int[] rslt=new int[cnt.length];
        int i=0,j=0;
        for(i=0;i<cnt.length;i++)
        {
            int tmp=GenTmp(cnt[i],j);
            rslt[i]=(tmp&num)>>j;
            j+=cnt[i];
        }
        return rslt;
    }

    public static void main(String[] args)
    {
        int[] cnt=new int[2];
        cnt[0]=2;
        cnt[1]=3;
//        int[] rslt = Decode(10,cnt);
        for(int i=0;i<32;i++)
        {
            int[] rslt=Decode(i,cnt);
            for(int j=0;j<rslt.length;j++)
            {
                System.out.print(rslt[j]+"\t");
            }
            System.out.println();
        }
    }
}

public class ExpressionUtils
{

//    Expression makeAND(Expression a, Expression b)
//    {
//        Expression trueExp=new PrimitiveValue(true);
//        Expression falseExp=new PrimitiveValue(false);
//
//        if(a.equals(falseExp) || b.equals(falseExp))
//        {
//            return falseExp;
//        }
//        else  if(a.equals(trueExp))
//        {
//            return b;
//        }
//        else if(b.equals(trueExp))
//        {
//            return a;
//        }
//        else
//        {
//            return Expression.makeArithmetic(a,b,Arith.AND);
////            return new arithmetic(a,b, Arith.AND);
//        }
//    }
//
//    Expression makeOR(Expression a,Expression b)
//    {
//        Expression trueExp=new PrimitiveValue(true);
//        Expression falseExp=new PrimitiveValue(false);
//        if(a.equals(trueExp) || b.equals(trueExp))
//        {
//            return trueExp;
//        }
//        else  if(a.equals(falseExp))
//        {
//            return b;
//        }
//        else if(b.equals(falseExp))
//        {
//            return a;
//        }
//        else
//        {
//            return Expression.makeArithmetic(a,b,Arith.OR);
////            return new arithmetic(a,b,Arith.OR);
//        }
//    }

    public static Expression CmpNegate(Expression e)
    {
        return new Comparison(e.lop,e.rop, Cmp.negate(((Comparison)e).op));
    }
    //如果exp的左右操作数均为变量，则num1,num2分别为分配给左右操作数的值；左操作数必定为变量，若右操作数不为变量，则函数输入中num2<0
    public static boolean isTrue(Comparison exp,int num1,int num2)
    {
        if(num2<0)
            num2=Integer.parseInt(exp.rop.toString());
        switch(exp.op)
        {
            case EQ:
                return num2 == num1;
            case NEQ:
                return num2 != num1;
            case GT:
                return num1 > num2;
            case LT:
                return num1 < num2;
            case GTE:
                return num1 >= num2;
            default ://LTE
                return num1 <= num2;
        }
    }
    //针对常量表达式，例如2<3，不存在变量，左右操作数均为常量
    public static boolean isTrue(Comparison exp)
    {
        if(exp.lop.getClass()!=PrimitiveValue.class || exp.rop.getClass()!=PrimitiveValue.class)
        {
            System.out.println("Both operands should be PrimitiveValues.");
            return false;
        }
        int num1=Integer.parseInt(exp.lop.toString());
        int num2=Integer.parseInt(exp.rop.toString());

        switch(exp.op)
        {
            case EQ:
                return num2 == num1;
            case NEQ:
                return num2 != num1;
            case GT:
                return num1 > num2;
            case LT:
                return num1 < num2;
            case GTE:
                return num1 >= num2;
            default ://LTE
                return num1 <= num2;
        }
    }
//    public static float computeProbForCmpExpressionInSky(Comparison exp,Float[] distribution)
//    {
//        float prob=0;
//        int i=0,num;
//        num=Integer.parseInt( exp.rop.toString());
//        switch (exp.op)
//        {
//            case EQ:
//                prob=distribution[num];
//                return prob;
//            case NEQ:
//                prob=1-distribution[num];
//                return prob;
//            case GT:
//                for(i=num+1;i<distribution.length;i++)
//                {
//                    prob+=distribution[i];
//                };
//                return prob;
//            case LT:
//                for(i=0;i<num;i++)
//                {
//                    prob+=distribution[i];
//                };
//                return prob;
//            case GTE:
//                for(i=num;i<distribution.length;i++)
//                {
//                    prob += distribution[i];
//                };
//                return prob;
//            default :
//                for(i=0;i<=num;i++)
//                {
//                    prob+=distribution[i];
//                };
//                return prob;
//        }
//    }

    public static float computeProbForCmpExpressionInSky(Comparison exp,Float[] leftdistribution,Float[] rightdistribution)
    {
        float prob=0;
        int i=0,j=0;
        for(i=0;i<leftdistribution.length;i++)
        {
            for(j=0;j<rightdistribution.length;j++)
            {
                if(isTrue(exp,i,j))
                {
                    //prob+=leftdistribution[i]*rightdistribution[j];
                    prob=ArithUtil.add(prob,ArithUtil.mul(leftdistribution[i],rightdistribution[j]));
                }
            }
        }
        return prob;
//        switch (exp.op)
//        {
//            case EQ:
//                while(i<leftdistribution.length && i<rightdistribution.length)
//                {
//                    prob+=leftdistribution[i]*rightdistribution[i];
//                    i++;
//                }
//                return prob;
//            case NEQ:
//                while(i<leftdistribution.length && i<rightdistribution.length)
//                {
//                    prob+=leftdistribution[i]*rightdistribution[i];
//                    i++;
//                }
//                prob=1-prob;
//                return prob;
//            case GT://left>right
//                for(i=0;i<rightdistribution.length;i++)
//                {
//                    for(j=i+1;j<leftdistribution.length;j++)
//                    {
//                        prob+=rightdistribution[i]*leftdistribution[j];
//                    }
//                }
//                return prob;
//            case LT:
//                for(i=1;i<rightdistribution.length;i++)
//                {
//                    for(j=i-1;j>=0;j--)
//                    {
//                        prob+=rightdistribution[i]*leftdistribution[j];
//                    }
//                }
//                return prob;
//            case GTE:
//                for(i=0;i<rightdistribution.length;i++)
//                {
//                    for(j=i;j<leftdistribution.length;j++)
//                    {
//                        prob+=rightdistribution[i]*leftdistribution[j];
//                    }
//                }
//                return prob;
//            default ://left<=right
//                for(i=0;i<rightdistribution.length;i++)
//                {
//                    for(j=i;j>=0;j--)
//                    {
//                        prob+=rightdistribution[i]*leftdistribution[j];
//                    }
//                }
//                return prob;
//        }
    }

//    public static Map<Expression,Float> computeProbsForAllChildrenInSky(Expression a, Map<Expression,Float[]> VarsDistritbution) throws Exception
//    {
//        Map<Expression,Float> rslt=new HashMap<Expression,Float>();
//        HashSet<Expression> children=a.toDNF().Children();
//        float prob=0;
//        for(Expression e:children)
//        {
//            System.out.println(e);
//            Float[] leftProbDistribution=VarsDistritbution.get(e.lop);
//            Float[] rightProbDistribution;
//            if(leftProbDistribution==null)
//            {
//                throw new Exception("Can't get the correct distribution for Expression(left) "+e.lop);
//            }
//            if(e.rop.getClass()==PrimitiveValue.class)
//            {
//                int tmp=Integer.parseInt(e.rop.toString());
//                rightProbDistribution=new Float[tmp+1];
//                for(int i=0;i<tmp;i++) rightProbDistribution[i]=Float.valueOf(0);
//                rightProbDistribution[tmp]=Float.valueOf(1);
//            }
//            else    //another variables
//            {
//                rightProbDistribution=VarsDistritbution.get(e.rop);
//                if(leftProbDistribution==null)
//                {
//                    throw new Exception("Can't get the correct distribution for Expression(right) "+e.rop);
//                }
//            }
//            prob=computeProbForCmpExpressionInSky((Comparison) e,leftProbDistribution,rightProbDistribution);
//            rslt.put(e,prob);
//        }
//        return rslt;
//    }

    //已知表达式以及表达式内变量概率分布，计算概率
    //para -lists:合取表达式
    public static float ComputeProbability(ArrayList<ArrayList<Expression>> lists, HashMap<Expression,Float[]> distribution) throws Exception
    {
        if(lists.get(0).get(0).getClass()==PrimitiveValue.class)//// TODO: 2017/6/28 暂定，当条件为真或假原子表达式时，返回概率值均为1，和Options类中构造函数中要保持一致，目前不一致
        {
            return 1f;
        }
        float prob=0;
        int i=0,j=0,total=0,bits=0;
        int cnt1=0,cnt2=0;
        int[] bitRecord=new int[distribution.size()];

        for(Map.Entry<Expression,Float[]> entry: distribution.entrySet())
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
            Iterator iter=distribution.keySet().iterator();
            while(iter.hasNext())//计算特定assignment的概率，tmp存储了assignment的信息，tmpprob存储该assignment的概率值，若flag=true，则继续判断该assignment是否使得lists为真
            {
                Var tmpvar=(Var) iter.next();
                if(tmp[j]<distribution.get(tmpvar).length && distribution.get(tmpvar)[tmp[j]]!=0)
                {
                    tmpmap.put(tmpvar,tmp[j]);
                    //tmpprob*=distribution.get(tmpvar)[tmp[j]];
                    tmpprob=ArithUtil.mul(tmpprob,distribution.get(tmpvar)[tmp[j]]);
                    j++;
                }
                else
                {
                    flag=false;
                    break;
                }
            }
            if(flag)//判断assignment是否满足为真
            {
                cnt1++;
                ArrayList< ArrayList<Expression>> tmpexplist=new ArrayList<ArrayList<Expression>>( lists.size());
                //Deep Copy of expLists
                for(ArrayList<Expression> l:lists)
                {
                    ArrayList<Expression> tmplist=new ArrayList<Expression>(l.size());
                    for (Expression aL : l)
                    {
                        tmplist.add(aL.clone());
                    }
                    tmpexplist.add(tmplist);
                }

                for(ArrayList<Expression> l:tmpexplist)
                {
                    boolean tmpflag=false;
                    for(Expression e:l)
                    {
                        if(e.lop.getClass()==Var.class)
                            e.lop=new PrimitiveValue(tmpmap.get(e.lop));
                        if(e.rop.getClass()==Var.class)
                            e.rop=new PrimitiveValue(tmpmap.get(e.rop));
                        if(ExpressionUtils.isTrue((Comparison)e))
                        {
                            tmpflag=true;
                            break;
                        }
                    }
                    if(!tmpflag)//l中没有任何e为真，因此，l为假，该assignment为假，lists为假
                    {
                        cnt2++;
                        //prob+=tmpprob;
                        prob=ArithUtil.add(prob,tmpprob);
                        break;
                    }
                }
            }
        }
//        for(i=0;i< total;i++)
//        {
//            j=0;
//            boolean flag=true;
//            float tmpprob=1;
//            int[] tmp=Decoder.Decode(i,bitRecord);
//            HashMap<Var,Integer> tmpmap=new HashMap<Var,Integer>();
//            Iterator iter=distribution.keySet().iterator();
//            while(iter.hasNext())
//            {
//                Var tmpvar=(Var) iter.next();
//                if(tmp[j]<distribution.get(tmpvar).length && distribution.get(tmpvar)[tmp[j]]!=0)
//                {
//                    tmpmap.put(tmpvar,tmp[j]);
//                    //tmpprob*=distribution.get(tmpvar)[tmp[j]];
//                    tmpprob=ArithUtil.mul(tmpprob,distribution.get(tmpvar)[tmp[j]]);
//                    j++;
//                }
//                else
//                {
//                    flag=false;
//                    break;
//                }
//            }
//            if(flag)
//            {
//                cnt1++;
//                ArrayList< ArrayList<Expression>> tmpexplist=new ArrayList<ArrayList<Expression>>( lists.size());
//                //Deep Copy of expLists
//                for(ArrayList<Expression> l:lists)
//                {
//                    ArrayList<Expression> tmplist=new ArrayList<Expression>(l.size());
//                    Iterator<Expression> tmpiter=l.iterator();
//                    while(tmpiter.hasNext())
//                    {
//                        tmplist.add(tmpiter.next().clone());
//                    }
//                    tmpexplist.add(tmplist);
//                }
//
//                for(ArrayList<Expression> l:tmpexplist)
//                {
//                    boolean tmpflag=true;
//                    for(Expression e:l)
//                    {
//                        if(e.lop.getClass()==Var.class)
//                            e.lop=new PrimitiveValue(tmpmap.get(e.lop));
//                        if(e.rop.getClass()==Var.class)
//                            e.rop=new PrimitiveValue(tmpmap.get(e.rop));
//                        if(!ExpressionUtils.isTrue((Comparison)e))
//                        {
//                            tmpflag=false;
//                            break;
//                        }
//                    }
//                    if(tmpflag)
//                    {
//                        cnt2++;
//                        //prob+=tmpprob;
//                        prob=ArithUtil.add(prob,tmpprob);
//                        break;
//                    }
////                    else
////                    {
////                        for(int tmpnum=0;tmpnum<tmp.length;tmpnum++)
////                        {
////                            System.out.print(tmp[tmpnum]+"\t");
////                        }
////                        System.out.println();
////                    }
//                }
//            }
//        }
        //System.out.println("cnt1="+cnt1+", cnt2="+cnt2);
        return ArithUtil.sub(1f,prob);
    }

//    //从expLists中移除表达式condition，其中condition是一个已知为真的表达式，用于更新ctable中的条件
//    //利用deep copy，故而expLists不变，返回移除condition之后新的expLists
//    public static ArrayList<ArrayList<Expression>> RemoveConstExp(Expression condition,ArrayList<ArrayList<Expression>> expLists)
//    {
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
//    }

    //从expLists中移除表达式condition，其中condition是一个已知为真的表达式，用于更新ctable中的条件
    //不采用deep copy，直接改变参数expLists
    public static ArrayList<ArrayList<Expression>> RemoveConstExp(Expression condition,ArrayList<ArrayList<Expression>> expLists)
    {
        condition=condition.DeMorgan();

        boolean flag=true;
        Expression trueexp=Expression.makePrimitiveValue(true);
        Expression falseexp=Expression.makePrimitiveValue(false);
        if(condition.getClass()==LogicFormula.class)//有取反符号
        {
            flag=false;
            condition=condition.lop;
        }
        Iterator<ArrayList<Expression>> iter1= expLists.iterator();
        boolean flag2=true;
        while(iter1.hasNext())
        {
            ArrayList<Expression> l=iter1.next();

//            int idx=l.indexOf(condition);//可以直接替换下述update过程（from begin to end）
//            if(idx>=0)//
//            {
//                if(flag==true)
//                {
//                    iter1.remove();
//                }
//                else
//                {
//                    l.remove(idx);
//                    if(l.size()==0)
//                    {
//                        flag2= false;
//                        l.add(falseexp);
//                        rslt.clear();
//                        rslt.add(l);
//                        break;
//                    }
//                }
//            }
            /**************** update start ******************/
            if(l.contains(condition))//行中包含condition
            {
                if(flag)//整行移除
                {
                    iter1.remove();
                }
                else//将condition移除
                {
                    Iterator<Expression> iter2=l.iterator();
                    while(iter2.hasNext())
                    {
                        Expression e=iter2.next();
                        if(e.equals(condition))
                        {
                            iter2.remove();
                        }
                    }
                    if(l.size()==0)//移除condition之后，该行为空了，此时整行为false，因而整个表达式为false
                    {
                        flag2= false;
                        l.add(falseexp);
                        expLists.clear();
                        expLists.add(l);
                        break;
                    }
                }
            }
            /**************** update end ******************/

        }
        if(flag2&& expLists.size()==0)
        {
            ArrayList<Expression> l=new ArrayList<>();
            l.add(trueexp);
            expLists.add(l);
        }
        //去重复
        //不采用hashset的原因：考虑到expLists是值传递
        for (int i = 0; i < expLists.size() - 1 ; i ++ ) {
            for (int j = expLists.size() - 1; j > i; j -- ) {
                if (expLists.get(j).equals(expLists.get(i))) {
                    expLists.remove(j);
                }
            }
        }
        return expLists;
    }

    public static Map<Expression,Float> computeProbsForAllChildrenInSky(HashSet<Expression> children, HashMap<Expression,Float[]> VarsDistritbution) throws Exception
    {
        Map<Expression,Float> rslt=new HashMap<Expression,Float>();
        //HashSet<Expression> children=a.toDNF().Children();
        float prob=0;
        for(Expression e:children)
        {
            Float[] leftProbDistribution=VarsDistritbution.get(e.lop);
            Float[] rightProbDistribution;
            if(leftProbDistribution==null)
            {
                throw new Exception("Can't get the correct distribution for Expression(left) "+e.lop);
            }
            if(e.rop.getClass()==PrimitiveValue.class)
            {
                int tmp=Integer.parseInt(e.rop.toString());
                rightProbDistribution=new Float[tmp+1];
                for(int i=0;i<tmp;i++) rightProbDistribution[i]= 0f;
                rightProbDistribution[tmp]= 1f;
            }
            else    //another variables
            {
                rightProbDistribution=VarsDistritbution.get(e.rop);
                if(rightProbDistribution==null)
                {
                    throw new Exception("Can't get the correct distribution for Expression(right) "+e.rop);
                }
            }
            prob=computeProbForCmpExpressionInSky((Comparison) e,leftProbDistribution,rightProbDistribution);
            rslt.put(e,prob);
        }
        return rslt;
    }
    //注意a和cond涉及同一个变量var，更新概率分布，即计算var的条件概率分布P(a|cond)，cond为条件，形如x<2
//    public Float[] updateProbDistributionForVarInSky(Comparison cond,Float[] aDist,float condProb)
//    {
//        int i=0,j=-1;
//        Float[] newDistribution=new Float[aDist.length];
//        for(i=0;i<aDist.length;i++)
//        {
//            newDistribution[i]=Float.valueOf(0);
//            if(isTrue(cond,i,j))
//            {
//                newDistribution[i]=aDist[i]/condProb;
//            }
//        }
//        return newDistribution;
//    }

    //cond中涉及两个变量,形如x<y，var1Dist指主变量var的概率分布，var2Dist指次变量的概率分布
    //其中此变量可以是一个常数，则概率分布表示为只有该常数的概率为1，其余均为0；
    //var指示主变量，cond中左操作数必为var，若非如此，则先将cond进行转换
    public static Float[] updateProbDistributionForVarInSky(Var var,Comparison cond,Float[] var1Dist,Float[] var2Dist)
    {
        int i=0,j=-1;
        Float[] newDistribution=new Float[var1Dist.length];
        if(!cond.rop.equals(var)&& !cond.lop.equals(var))   //变量var和cond无关，直接返回var的原始概率分布即可
        {
            return var1Dist;
        }
        if(cond.rop.equals(var))
        {
            Expression tmp=cond.lop;
            cond.op= Cmp.turnover(cond.op);
            cond.lop=cond.rop;
            cond.rop=tmp;
        }
        float condProb=computeProbForCmpExpressionInSky(cond,var1Dist,var2Dist);
        //System.out.println("p("+cond+")= "+ condProb);
        for(i=0;i<var1Dist.length;i++)
        {
            newDistribution[i]= 0f;
            for(j=0;j<var2Dist.length;j++)
            {
                if(isTrue(cond,i,j)) newDistribution[i]=ArithUtil.add(newDistribution[i],ArithUtil.mul(var1Dist[i],var2Dist[j]));
            }
            newDistribution[i]=ArithUtil.div(newDistribution[i],condProb);
//            newDistribution[i]/=condProb;
        }
        return newDistribution;
    }

    public static ArrayList<Expression> GetDisjuncts(Expression a)
    {
        ArrayList<Expression> rslt=new ArrayList<Expression>();
        if(a.getClass()==LogicFormula.class && ((LogicFormula)a).op== Conj.OR)
        {
            rslt.addAll(GetDisjuncts(a.lop));
            rslt.addAll(GetDisjuncts(a.rop));
        }
        else
        {
            rslt.add(a);
        }
        return rslt;
    }

    public static ArrayList<Expression> GetConjuncts(Expression a)
    {
        ArrayList<Expression> rslt=new ArrayList<Expression>();
        if(a.getClass()==LogicFormula.class && ((LogicFormula)a).op== Conj.AND)
        {
            rslt.addAll(GetConjuncts(a.lop));
            rslt.addAll(GetConjuncts(a.rop));
        }
        else
        {
            rslt.add(a);
        }
        return rslt;
    }
    //DNF表现形式，内层arraylist是合取的原子表达式，外层arraylist是析取的表达式
    public static ArrayList<ArrayList<Expression>> GetLists(Expression a)
    {
        ArrayList<ArrayList<Expression>> rslt=new ArrayList<ArrayList<Expression>>();
        ArrayList<Expression> disjuncts;
        disjuncts=GetDisjuncts(a);
        for(Expression e:disjuncts)
        {
            ArrayList<Expression> tmp=GetConjuncts(e);
            rslt.add(tmp);
        }
        return rslt;
    }
    public static ArrayList<Expression> GetAllExps(Expression a)
    {
        ArrayList<Expression> rslt=new ArrayList<>();
        if(a.getClass()!=LogicFormula.class)
        {
            rslt.add(a);
        }
        else
        {
            rslt.addAll(GetAllExps(a.lop));
            rslt.addAll(GetAllExps(a.rop));
        }
        return rslt;
    }
//    public static HashSet<Expression> GetExpsSize(ArrayList<ArrayList<Expression>> a)
//    {
//        int cnt=0;
//        for(ArrayList<Expression> l:a)
//        {
//            for(Expression e:l)
//            {
//                rslt.add(e);
//            }
//        }
//        return rslt;
//    }
    public static HashSet<Expression> GetAllExps(ArrayList<ArrayList<Expression>> a)
    {
        HashSet<Expression> rslt=new HashSet<>();
        for(ArrayList<Expression> l:a)
        {
            for(Expression e:l)
            {
                rslt.add(e);
            }
        }
        return rslt;
    }
    public static void main(String[] args) throws Exception
    {
        Expression a =new PrimitiveValue(true);
        Expression b=new PrimitiveValue(false);
        Expression c=new PrimitiveValue(1);
        Expression d=new PrimitiveValue(true);
        Expression e=new PrimitiveValue(false);
        ExpressionUtils eutils=new ExpressionUtils();

//        System.out.println(eutils.makeAND(a,b));
//        System.out.println(eutils.makeAND(a,a));
//        System.out.println(eutils.makeAND(b,e));
//        System.out.println(eutils.makeAND(a,d));
//        System.out.println(eutils.makeAND(a,c));
//        System.out.println(eutils.makeOR(a,b));

        Expression exp0=new Var(2,3);
        Expression exp1=new Var(1,3);
        Expression exp2=new PrimitiveValue(2);
        Expression exp3=new PrimitiveValue(7);
        Expression exp4=new Comparison(exp1,exp2, Cmp.GT);
        Expression exp5=new Comparison(exp0,exp3, Cmp.LT);
        DataSet ds=new DataSet();
        HashMap<Expression,Float[]> distribution=new HashMap<Expression,Float[]>();

        Float[] tmp1=new Float[10];
        for(int i=0;i<tmp1.length;i++)
        {
            tmp1[i]=(float)1/(float)tmp1.length;
        }
        Float[] tmp2=new Float[8];
        for(int i=0;i<tmp2.length;i++)
        {
            tmp2[i]=(float)1/(float)tmp2.length;
        }
        distribution.put(exp1,tmp1);
        distribution.put(exp0,tmp2);
        //ds.varsDistribution=distribution;
        Expression exp=new LogicFormula(new LogicFormula(exp4,exp5, Conj.AND),new Comparison(exp1,exp0, Cmp.GT),Conj.OR);
        HashSet<Expression> exps=exp.Children();
        System.out.println( computeProbsForAllChildrenInSky(exps,distribution));

//        int i=0;
//        Expression exp0=new Var(0,3);
//        Expression exp1=new Var(1,3);
//        Expression cond1=new Comparison(exp0,new PrimitiveValue(3),Cmp.LT);
//        Expression cond2=new Comparison(exp0,new PrimitiveValue(2),Cmp.LT);
//        Expression cond4=new Comparison(exp1,new PrimitiveValue(1),Cmp.GT);
//        Expression cond3=new Comparison(exp0,exp1,Cmp.LT);
//        Float[] xvar=new Float[5];
//        Float[] yvar=new Float[3];
//        xvar[0]=(float) 0.1;
//        xvar[1]=(float) 0.1;
//        xvar[2]=(float) 0.3;
//        xvar[3]=(float) 0.4;
//        xvar[4]=(float) 0.1;
//        yvar[0]=(float) 0.3;
//        yvar[1]=(float) 0.3;
//        yvar[2]=(float) 0.4;
//        Float[] constVar=new Float[4];
//        constVar[0]=(float) 0;
//        constVar[1]=(float) 0;
//        constVar[2]=(float) 0;
//        constVar[3]=(float) 1;
//        //float prob=ExpressionUtils.computeProbForCmpExpressionInSky((Comparison) cond1,xvar,constVar);
//        //Float[] newdist=ExpressionUtils.updateProbDistributionForVarInSky((Var) exp0,(Comparison) cond1,xvar,constVar,prob);
//        float prob=ExpressionUtils.computeProbForCmpExpressionInSky((Comparison) cond3,xvar,yvar);
//        Float[] newdist=ExpressionUtils.updateProbDistributionForVarInSky((Var) exp0,(Comparison) cond3,xvar,yvar);
//        float prob2=ExpressionUtils.computeProbForCmpExpressionInSky((Comparison) cond1,newdist,constVar);
//        Float[] newdist2=ExpressionUtils.updateProbDistributionForVarInSky((Var) exp0,(Comparison) cond1,newdist,constVar);
////        System.out.println(prob+"\n" );
////        for(i=0;i<newdist.length;i++)
////            System.out.println("i="+i+", prob="+newdist[i]);
////
////        System.out.println(prob2+"\n" );
////        for(i=0;i<newdist2.length;i++)
////            System.out.println("i="+i+", prob="+newdist2[i]);
//
//        Expression orExp=new LogicFormula(new LogicFormula(new LogicFormula(new LogicFormula(cond4,cond1,Conj.AND),cond3, Conj.OR),cond2, Conj.OR),cond1, Conj.OR);
//        Expression andExp=new LogicFormula(new LogicFormula(new LogicFormula(new LogicFormula(cond4,cond1,Conj.OR),cond3, Conj.AND),cond2, Conj.AND),cond1, Conj.AND);
//
//        System.out.println(ExpressionUtils.GetDisjuncts(orExp));
//        System.out.println(ExpressionUtils.GetConjuncts(andExp));
//        System.out.println(ExpressionUtils.GetLists(orExp));
//        System.out.println(ExpressionUtils.GetLists(andExp));
    }
}
