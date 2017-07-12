package edu.zju.ctables.Expressions;

import edu.zju.ctables.DataSet;

import java.util.HashMap;

/**
 * Created by TLD7040A on 2017/6/8.
 */
public class ExpTree
{
    public ExpTreeNode root;
    public DataSet ds;
    //public Map<Expression,Float> expProbPairs;
    //public ExpressionUtils expUtil=new ExpressionUtils();
   // public//

    public ExpTree(Expression a, HashMap<Expression,Float[]> ds) throws Exception
    {
        //expProbPairs=expUtil.computeProbsForAllChildrenInSky(a,ds);
        root=new ExpTreeNode(a,ds);
    }

    public void PrintExpTree()
    {
        root.PrintTreeNode(0);
    }

    public static void main(String[] args) throws Exception
    {
        Expression a=new Comparison(new Var(1,1),new PrimitiveValue(1),Cmp.EQ);
        Expression b=new Comparison(new Var(1,2),new PrimitiveValue(2),Cmp.EQ);
        Expression c=new Comparison(new Var(1,3),new PrimitiveValue(3),Cmp.EQ);

        Expression exp=new LogicFormula(a,new LogicFormula(b,c,Conj.AND), Conj.OR);

        HashMap<Expression,Float[]> distribution=new HashMap<>();
        Float[] adist={(float) 0.2,(float)0.8};
        Float[] bdist={(float) 0.2,(float)0.3,(float)0.4,(float)0.1};
        Float[] cdist={(float) 0.2,(float)0.1,(float)0.1,(float)0.5,(float)0.1};
        distribution.put(new Var(1,1),adist);
        distribution.put(new Var(1,2),bdist);
        distribution.put(new Var(1,3),cdist);

        ExpTree tree=new ExpTree(exp,distribution);
        tree.PrintExpTree();
    }

}
