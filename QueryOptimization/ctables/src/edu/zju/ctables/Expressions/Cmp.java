package edu.zju.ctables.Expressions;

/**
 * Created by TLD7040A on 2017/6/8.
 */
public enum Cmp
{
    EQ,NEQ,GT,LT,GTE,LTE;
    public static Cmp negate(Cmp a)
    {
        switch(a)
        {
            case EQ:return NEQ;
            case NEQ:return EQ;
            case GT:return LTE;
            case LT:return GTE;
            case GTE:return LT;
            default :return GT;
        }
    }

    public static String opString(Cmp a)
    {
        switch(a)
        {
            case EQ:return "=";
            case NEQ:return "!=";
            case GT:return ">";
            case LT:return "<";
            case GTE:return ">=";
            default:return "<=";
        }
    }

    public static String opString(String a)
    {
        switch(a)
        {
            case "EQ":return "=";
            case "NEQ":return "!=";
            case "GT":return "&gt;";
            case "LT":return "&lt;";
            case "GTE":return "&gt;=";
            default:return "&lt;=";
        }
    }

    public static Cmp turnover(Cmp a)
    {
        switch(a)
        {
            case EQ:return EQ;
            case NEQ:return NEQ;
            case GT:return LT;
            case LT:return GT;
            case GTE:return LTE;
            default:return GTE;//LTE
        }
    }
}

