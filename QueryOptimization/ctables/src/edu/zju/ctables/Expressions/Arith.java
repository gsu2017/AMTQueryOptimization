package edu.zju.ctables.Expressions;

/**
 * Created by TLD7040A on 2017/6/8.
 */
public enum Arith
{
    ADD,SUB,MULT,DIV;
    //ADD,SUB,MULT,DIV,AND,OR;

    public static Arith fromString(String a) throws Exception
    {
        switch (a)
        {
            case "+":return ADD;
            case "-":return SUB;
            case "*":return MULT;
            case "/":return DIV;
//            case "&":return AND;
//            case "|":return OR;
            default:throw new Exception("Invalid Operator '"+a+"'");
        }
    }

    public static String opString(Arith a)
    {
        switch(a)
        {
            case ADD    :   return "+";
            case SUB    :   return "-";
            case MULT   :   return "*";
            default     :   return "/";
//            case AND    :   return "&";
//            default     :   return "|"; //OR
        }
    }

//    public static boolean isBool(Arith a)
//    {
//        switch (a)
//        {
//            case AND:return true;
//            case OR:return true;
//            default:return false;
//        }
//    }
//
//    public static boolean isNumeric(Arith a)
//    {
//        return !isBool(a);
//    }
}
