package edu.zju.ctables.Expressions;

/**
 * Created by TLD7040A on 2017/7/6.
 */
public class Comparison extends Expression
{
    public Cmp op;

    public Comparison(Expression left,Expression right, Cmp a)
    {
        lop=left;
        rop=right;
        op=a;
        EXPTYPE=ExpressionType.COMPARISON;
    }
    public String toString()
    {
        String s="("+lop.toString()+Cmp.opString(op)+rop.toString()+")";
        return s;
    }

    @Override
    public String GetOperator()
    {
        return op.toString();
    }
}
