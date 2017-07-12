package edu.zju.ctables;

import edu.zju.ctables.Expressions.Expression;
import edu.zju.ctables.Expressions.ExpressionType;
import edu.zju.ctables.Expressions.ExpressionUtils;

import java.util.*;

/**
 * Created by TLD7040A on 2017/6/21.
 */
public class BaseCount
{
    public HashMap<Integer, Expression> conditions;//condition中表达式为true的直接去掉了,<oid,condition of obj oid>
    public HashMap<Expression,ArrayList<Expression>> vars;

    public BaseCount(HashMap<Integer,Expression> conds)
    {
        vars=new HashMap<Expression,ArrayList<Expression>>();
        conditions=conds;

        //计算变量出现次数
        Iterator iter=conditions.values().iterator();
        while(iter.hasNext())
        {
            Expression tmpexp= (Expression) iter.next();
            ArrayList<Expression> tmplist = ExpressionUtils.GetAllExps(tmpexp);
            for(Expression e:tmplist)
            {
                //左操作数
                if(e.lop.EXPTYPE== ExpressionType.VARIABLE)
                {
                    ArrayList<Expression> tmp = vars.get(e.lop);
                    if(tmp==null) tmp=new ArrayList<Expression>();
                    tmp.add(e);
                    vars.put(e.lop,tmp);
//                    if(vars.containsKey(e.lop))
//                    {
//                        ArrayList<Expression> tmp=vars.get()
//                        int tmp=vars.get(e.lop);
//                        vars.put(e.lop,tmp+1);
//                    }
//                    else
//                    {
//                        vars.put(e.lop,1);
//                    }
                }
                //右操作数
                if(e.rop.EXPTYPE== ExpressionType.VARIABLE)
                {
                    ArrayList<Expression> tmp = vars.get(e.rop);
                    tmp.add(e);
                    vars.put(e.rop,tmp);
                }
            }
        }


    }

    public List<Map.Entry<Expression,ArrayList<Expression>>> TopkVar(int k)
    {
        int i=0;
        List<Map.Entry<Expression,ArrayList<Expression>>> rslt=new ArrayList<>();
        //变量按照出现次数从大到小排序
        List<Map.Entry<Expression,ArrayList<Expression>>> tmplist=new ArrayList<Map.Entry<Expression, ArrayList<Expression>>>(vars.entrySet());
        Collections.sort(tmplist, new Comparator<Map.Entry<Expression, ArrayList<Expression>>>()
        {
            @Override
            public int compare(Map.Entry<Expression, ArrayList<Expression>> o1, Map.Entry<Expression, ArrayList<Expression>> o2)
            {
                Integer len1=o1.getValue().size();
                Integer len2=o2.getValue().size();
                return len2.compareTo(len1);
            }
        });
        System.out.println("Check for correct ordering.");
        for(Map.Entry<Expression,ArrayList<Expression>> entry:tmplist)
        {
            System.out.println(entry.getKey()+"\t"+entry.getValue());
        }

        if(vars.size()<k)
        {
            return tmplist;
        }

        Iterator iter=tmplist.iterator();
        while(i++<k)
        {
            Map.Entry<Expression,ArrayList<Expression>> entry= (Map.Entry<Expression, ArrayList<Expression>>) iter.next();
            rslt.add(entry);
        }
        return rslt;
    }

}
