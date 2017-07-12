package edu.zju.ctables.Expressions;

/**
 * Created by TLD7040A on 2017/7/6.
 */
public class Var extends Expression
{
    public Var(int rowid,int attrid)
    {
        lop=new PrimitiveValue(rowid);
        rop=new PrimitiveValue(attrid);
        EXPTYPE=ExpressionType.VARIABLE;
//        isPrimitive=true;
    }

    public String toString()
    {
        return "V_"+lop.toString()+"_"+rop.toString();
    }
    @Override
    public String GetOperator()
    {
        return null;
    }
}
