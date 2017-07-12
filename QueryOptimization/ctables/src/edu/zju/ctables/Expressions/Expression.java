package edu.zju.ctables.Expressions;

import java.util.HashSet;

import static edu.zju.ctables.Expressions.Conj.AND;
import static edu.zju.ctables.Expressions.Conj.NEGATE;

/**
 * Created by TLD7040A on 2017/6/2.
 */

//primitive value type enumeration
enum PrimitiveType
{
    PINT,PFLOAT,PSTRING,PBOOLEAN;
}


class PrimitiveValue extends Expression
{
    public PrimitiveType type;
//    public Integer asInt;
//    public Float asFloat;
    public String asString;
    public Boolean asBool=false;//used to indicate always true condition

    public String toString()
    {
        return asString;
    }

    public PrimitiveValue(Integer a)
    {
        type=PrimitiveType.PINT;
//        asInt=a;
//        asFloat=Float.parseFloat(a.toString());
        asString=a.toString();
        EXPTYPE=ExpressionType.PRIMITIVE;
//        isPrimitive=true;

    }

    public PrimitiveValue(Float a)
    {
        type=PrimitiveType.PFLOAT;
//        asInt=Integer.parseInt(a.toString());
//        asFloat=a;
        asString=a.toString();
        EXPTYPE=ExpressionType.PRIMITIVE;
//        isPrimitive=true;
    }

    public PrimitiveValue(String a)
    {
        type=PrimitiveType.PSTRING;
//        asInt=Integer.parseInt(a);
//        asFloat=Float.parseFloat(a);
        asString=a;
        EXPTYPE=ExpressionType.PRIMITIVE;
//        isPrimitive=true;
    }

    public  PrimitiveValue(Boolean a)
    {
        type=PrimitiveType.PBOOLEAN;
//        asInt=0;
//        asFloat=Float.parseFloat(asInt.toString());
        asString=a.toString();
        asBool=a;
        EXPTYPE=ExpressionType.PRIMITIVE;
//        isPrimitive=true;
    }


    @Override
    public String GetOperator()
    {
        return null;
    }
}
class Arithmetic extends Expression
{
    public Arith op;

    public Arithmetic(Expression left,Expression right,Arith a)
    {
        lop=left;
        rop=right;
        op=a;
        EXPTYPE=ExpressionType.ARITHMETIC;
    }
    public String toString()
    {
        return "("+lop.toString()+Arith.opString(op)+rop.toString()+")";
    }

    @Override
    public String GetOperator()
    {
        return op.toString();
    }
//    public Arith getOP()
//    {
//        return op;
//    }
}

class LogicFormula extends Expression
{
    public Conj op;

    public LogicFormula(Expression left,Expression right,Conj a)
    {
        lop=left;
        rop=right;
        op=a;
        EXPTYPE=ExpressionType.LOGICAL;
    }
    public String toString()
    {
        String s="("+lop.toString()+Conj.opString(op)+rop.toString()+")";
        if(op== NEGATE)
        {
            s="("+Conj.opString(op)+rop.toString()+")";
        }
        else
        {
            s="("+lop.toString()+Conj.opString(op)+rop.toString()+")";
        }
        return s;
    }
    public LogicFormula(Expression exp,Conj a)
    {
        if(a!= NEGATE)
        {
            System.out.println("Invalid parameter when you construct LOGICFORMULA.");
        }
        else
        {
            lop=exp;
            rop=exp;
            op=a;
            EXPTYPE=ExpressionType.LOGICAL;
        }
    }
//  TO DO: recursive check
//    public boolean check()
//    {
//
//    }

    //暂时简单判断若左右式子都不是逻辑式，则该式子是原子公式
    public boolean isAtom()
    {
        return lop.getClass() != LogicFormula.class && rop.getClass() != LogicFormula.class;
    }

    @Override
    public String GetOperator()
    {
        return op.toString();
    }

}
public abstract class Expression implements Cloneable
{
    public Expression lop;//left operand
    public Expression rop;//right operand
    //public boolean isPrimitive=false;
    public ExpressionType EXPTYPE;

    abstract public String GetOperator();//抽象方法，返回操作符，若是Var或者PrimitiveValue，返回null

    //比较左右操作数是否相同，暂时用于comparison类表达式
    public boolean HasSameOperands(Expression other)
    {
        if(this.getClass()==other.getClass())
        {
            if((this.lop.equals(other.lop) && this.rop.equals(other.rop)) ||(this.lop.equals(other.rop) && this.rop.equals(other.lop)))
                return true;
        }
        return false;
    }

    @Override
    protected Expression clone() throws CloneNotSupportedException
    {
        Expression clone=null;
        try{
            clone = (Expression) super.clone();
        }catch(CloneNotSupportedException e){
            throw new RuntimeException(e); // won't happen
        }
        return clone;
    }
    //若是覆写该函数，则public boolean equals(Object obj)
    @Override
    public boolean equals(Object a)
    {
        if(this==a)
            return true;
        if(this.EXPTYPE!=((Expression)a).EXPTYPE)
            return false;
        if(this.EXPTYPE==ExpressionType.PRIMITIVE)
        {
            if (this.toString().equals(((Expression)a).toString()))
                return true;
        }
        else
        {
            if(this.lop.equals(((Expression)a).lop)&&this.rop.equals(((Expression)a).rop))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return this.toString().hashCode();
    }
//    public boolean equals(String s)
//    {
//        //System.out.println("this:"+this.toString()+", s:"+s);
//        if(this.toString().equals(s))
//        {
//            return true;
//        }
//        return false;
//    }

    //Constructors
    public static PrimitiveValue makePrimitiveValue(Integer a) { return new PrimitiveValue(a);}
    public static PrimitiveValue makePrimitiveValue(Float a) {return new PrimitiveValue(a);}
    public static PrimitiveValue makePrimitiveValue(Boolean a) {return new PrimitiveValue(a);}
    public static PrimitiveValue makePrimitiveValue(String a) {return new PrimitiveValue(a);}
    public static Var makeVar(int rowid,int attrid) {return new Var(rowid,attrid);}
    public static LogicFormula makeLogicFormula(Expression left,Expression right,Conj a) {return new LogicFormula(left,right,a);}
    public static LogicFormula makeLogicFormula(Expression left,Conj a) {return new LogicFormula(left,a);}
    public static Comparison makeComparison(Expression left,Expression right,Cmp a) {return new Comparison(left,right,a);}
    public static Arithmetic makeArithmetic(Expression left,Expression right,Arith a) {return new Arithmetic(left,right,a);}


    public HashSet<Expression> Children()
    {
        HashSet<Expression> rslt=new HashSet<Expression>();
        //System.out.println(this+"\t"+this.getClass().getName());
        if(this.getClass()!= LogicFormula.class)
        {
            rslt.add(this);
            return rslt;
        }
//
//        if(this.EXPTYPE==ExpressionType.VARIABLE||this.EXPTYPE==ExpressionType.PRIMITIVE)
//        {
//            return rslt;
//        }
//        if((this.lop.EXPTYPE==ExpressionType.VARIABLE || this.lop.EXPTYPE==ExpressionType.PRIMITIVE) && (this.rop.EXPTYPE==ExpressionType.VARIABLE || this.rop.EXPTYPE==ExpressionType.PRIMITIVE))
//        {
//            rslt.add(this);
//            return rslt;
//        }
        rslt.addAll(this.lop.Children());
        rslt.addAll(this.rop.Children());
        return rslt;
    }

    //德摩根定律
    public Expression DeMorgan()
    {
        LogicFormula rslt;
        Expression l,r;
        if(this.getClass()!=LogicFormula.class)
        {
            return this;
        }
        else if(this.lop.getClass()==LogicFormula.class && ((LogicFormula)this).op== Conj.NEGATE)
        {
            LogicFormula tmpExp=(LogicFormula) (this.lop);
            rslt=tmpExp;
            if(tmpExp.op==NEGATE)
            {
                return tmpExp.lop.DeMorgan();
            }
            else if(tmpExp.op==AND)
            {
                l=new LogicFormula(tmpExp.lop, NEGATE);
                r=new LogicFormula(tmpExp.rop, NEGATE);

                rslt.lop=l.DeMorgan();
                rslt.rop=r.DeMorgan();
                rslt.op=Conj.OR;
            }
            else
            {
                l=new LogicFormula(tmpExp.lop, NEGATE);
                r=new LogicFormula(tmpExp.rop, NEGATE);

                rslt.lop=l.DeMorgan();
                rslt.rop=r.DeMorgan();
                rslt.op=Conj.AND;
            }
            return rslt;
        }
        else
        {
            lop=lop.DeMorgan();
            rop=rop.DeMorgan();
            return this;
        }
    }

    //转化为析取范式，利用分配律
    //convert to Disjunctive Normal Formula
    public Expression toDNF()
    {
        Expression exp = this.DeMorgan();
        Expression l,r;
        LogicFormula rslt;
        if(exp.getClass()!=LogicFormula.class)
        {
            return exp;
        }
        else if(((LogicFormula)exp).op==NEGATE)
        {
            return exp;
        }
        else if(((LogicFormula)exp).op==AND)
        {
            rslt=(LogicFormula)exp;
            exp.lop=exp.lop.toDNF();
            exp.rop=exp.rop.toDNF();
            if(exp.lop.getClass()==LogicFormula.class && ((LogicFormula)exp.lop).op==Conj.OR)
            {
                LogicFormula tmp=(LogicFormula)rslt.lop;
                l=new LogicFormula(tmp.lop,exp.rop, Conj.AND);
                r=new LogicFormula(tmp.rop,exp.rop, Conj.AND);
                rslt.lop=l.toDNF();
                rslt.rop=r.toDNF();
                rslt.op= Conj.OR;
                return rslt;
            }
            else if(exp.rop.getClass()==LogicFormula.class && ((LogicFormula)exp.rop).op==Conj.OR)
            {
                LogicFormula tmp=(LogicFormula)rslt.rop;
                l=new LogicFormula(exp.lop,tmp.lop, Conj.AND);
                r=new LogicFormula(exp.lop,tmp.rop, Conj.AND);
                rslt.lop=l.toDNF();
                rslt.rop=r.toDNF();
                rslt.op= Conj.OR;
                return rslt;
            }
            else
            {
                return exp;
            }
        }
        else
        {
            rslt=(LogicFormula)exp;
            rslt.lop=exp.lop.toDNF();
            rslt.rop=exp.rop.toDNF();
            return rslt;
        }
    }

//    public ArrayList<Expression> toDNFList()
//    {
//        Expression exp=this.toDNF();
//
//    }

    public static void main(String[] args)
    {
        Expression exp1=new Var(1,3);
        Expression exp2=new PrimitiveValue(10);
        Expression exp3=new PrimitiveValue(18);
        Expression exp4=new Comparison(exp1,exp2, Cmp.GT);
        Expression exp5=new Arithmetic(exp2,exp3,Arith.ADD);
        Expression exp6=new PrimitiveValue(true);
        Expression exp7=new Comparison(exp1,exp3, Cmp.LT);
        Expression exp8=new LogicFormula(exp4,exp7,Conj.AND);
        Expression exp9=new LogicFormula(exp8,exp4, Conj.OR);
        Expression exp10=new Comparison(exp1,new PrimitiveValue(20), Cmp.EQ);
        Expression exp11=new LogicFormula(exp10, NEGATE);
        Expression exp12=new LogicFormula(exp9,exp11, Conj.AND);
        Expression exp13=new LogicFormula(new LogicFormula(new LogicFormula(exp4, NEGATE), NEGATE),NEGATE) ;
        Expression exp14=new LogicFormula(new LogicFormula(new LogicFormula(new LogicFormula(exp4,exp7,Conj.AND), NEGATE),new LogicFormula(exp4,new LogicFormula(exp7, NEGATE), Conj.OR),Conj.OR), NEGATE);
        Expression exp15=new LogicFormula(new LogicFormula(new LogicFormula(exp4,exp7,Conj.AND), NEGATE),new LogicFormula(exp4,new LogicFormula(exp7, NEGATE), Conj.OR),Conj.OR);
//        System.out.println(exp1.getClass()==Var.class);
//        System.out.println(exp2.getClass().getName());
//        System.out.println(exp3);
//        System.out.println(exp4);
//        System.out.println(exp5);
//        System.out.println(exp6);
//        System.out.println(exp7);
//        System.out.println(exp8);
//        System.out.println(((LogicFormula)exp8).isAtom());
//        System.out.println(exp9);
//        System.out.println(((LogicFormula)exp9).isAtom());
//        System.out.println(exp10);
//        System.out.println(exp11);
//        System.out.println(exp12);
//        System.out.println(((LogicFormula)exp11).isAtom());
//        System.out.println(((LogicFormula)exp12).isAtom());

//        System.out.println(exp16);
//        System.out.println(exp16.DeMorgan());
//        System.out.println(exp17);
//        System.out.println(exp17.DeMorgan());
//        System.out.println(((LogicFormula)exp13).isAtom());
//        System.out.println(exp13.DeMorgan());
//        System.out.println(exp17);
//        System.out.println(exp17.DeMorgan());
//        System.out.println(exp14);
//        System.out.println(exp14.DeMorgan());
//        System.out.println(exp15);
//        System.out.println(exp15.DeMorgan().getClass().getName());
        Expression exp16=new LogicFormula(new LogicFormula(exp4,exp7, Conj.AND), Conj.NEGATE);
        Expression exp17=new LogicFormula(new LogicFormula(new LogicFormula(exp4,exp7, Conj.OR), Conj.NEGATE),NEGATE) ;
        Expression exp=exp15.DeMorgan();
        //System.out.println(exp.getClass().getName());
        Expression exp18=new LogicFormula(exp4,new LogicFormula(exp4,exp7, Conj.OR), Conj.AND);
        Expression exp19=new LogicFormula(exp4,new LogicFormula(exp4,exp7, Conj.AND), Conj.OR);
        Expression exp20=new LogicFormula(exp18,exp4, Conj.AND);
        Expression exp21=new LogicFormula(exp20,new LogicFormula(exp4,exp7, Conj.OR), Conj.AND);
        System.out.println(exp18+"\t"+exp18.toDNF());
        System.out.println(exp19+"\t"+exp19.toDNF());
        System.out.println(exp20+"\t"+exp20.toDNF());
        System.out.println(exp21+"\t"+exp21.toDNF());

        for(Expression e:exp21.toDNF().Children())
        {
            System.out.println(e);
        }
    }

}
