package edu.zju.ctables.Expressions;

/**
 * Created by TLD7040A on 2017/6/9.
 */
//否定，合取，析取，条件，双条件
public enum Conj
{
    AND,OR,NEGATE;

    public static Conj fromString(String a) throws Exception
    {
        switch (a)
        {
            case "&":return AND;
            case "|":return OR;
            case "~":return NEGATE;
            default:throw new Exception("Invalid Operator '"+a+"'");
        }
    }

    public static String opString(Conj a)
    {
        switch(a)
        {
            case AND    :   return "&";
            case NEGATE :   return "~";
            default     :   return "|"; //OR
        }
    }

}
