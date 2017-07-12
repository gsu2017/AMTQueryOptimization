package edu.zju.ctables;

import edu.zju.amt.Task;
import edu.zju.ctables.Expressions.Expression;
import edu.zju.ctables.Expressions.ExpressionType;
import edu.zju.ctables.Expressions.Options;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by TLD7040A on 2017/6/30.
 */

class CItem
{
    public int oid;//记录所表示对象的id
    public float CPI;//记录该对象的condition中CPI最大值
    public Expression crowdexp;//记录用于提问的问题
    public ArrayList<ArrayList<Expression>> lists;//完整的condition
}

public class Conditions
{
    public ArrayList<CItem> condlists=new ArrayList<>();
    public DataSet ds;
    public HashMap<Expression,Set<Integer>> varInConditions=new HashMap<>();//记录各个变量分别出现在了哪些condition之中
    public HashMap<String,Task> tasks;

    public HashMap<String,Task> GetTasks()
    {
        return tasks;
    }
    public Conditions(HashMap<Integer, ArrayList<ArrayList<Expression>>> conds, DataSet dataset) throws Exception
    {
        ds=dataset;
        for(HashMap.Entry<Integer,ArrayList<ArrayList<Expression>>> entry:conds.entrySet())
        {
            CItem item=new CItem();
            item.oid=entry.getKey();
            item.lists=entry.getValue();
            Options op=new Options(entry.getValue(),ds.varsDistribution);
            item.CPI=op.CPIs.get(0).getValue();//记录CPI值最高的表达式
            item.crowdexp=op.CPIs.get(0).getKey();
            condlists.add(item);
            RecordVarsInCondition(item.oid,item.lists);
        }
        //按照CPI值排序
        Collections.sort(condlists, new Comparator<CItem>()
        {
            @Override
            public int compare(CItem o1, CItem o2)
            {
                return Float.compare(o2.CPI,o1.CPI);
            }
        });
    }

    //选择k个表达式，生成Question Form
    //返回questionForm中包含的问题个数
    public int GenerateXMLFile(int k,String xmlfile,String url) throws Exception
    {
        //String filename="";
        String prefix="<QuestionForm xmlns=\"http://mechanicalturk.amazonaws.com/AWSMechanicalTurkDataSchemas/2005-10-01/QuestionForm.xsd\">\n" +
                "<Overview>\n" +
                "<Title>Investigation on basketball players</Title>\n" +
                "<Text>\n" +
                "Make sure you read the following instructions carefully before starting this task. \n" +
                "If you can't see the picture below, you can open a new window and visit the URL( "+url+" ) to read the instructions.\n" +
                "</Text>\n" +
                "<Binary>\n" +
                "<MimeType>\n" +
                "<Type>image</Type>\n" +
                "</MimeType>\n" +
                "<DataURL>"+url+"</DataURL>\n" +//https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png
                "<AltText>Instructions</AltText>\n" +
                "</Binary>\n" +
                "</Overview>";
        String suffix="</QuestionForm>\n";
        StringBuilder content=new StringBuilder("");
        HashMap<String,Task> tasks=new HashMap<>();
        int i=0;
        for(CItem citem:condlists)
        {
            boolean flag=false;
            if(i==k) break;
            // TODO: 2017/7/5 需要保证task的非冗余性，例如如果存在x<y问题，则x>y可以不问
            for(Map.Entry<String,Task> entry:tasks.entrySet())
            {
                Expression exp=entry.getValue().question;
                if(exp.HasSameOperands(citem.crowdexp))
                {
                    flag=true;
                    break;
                }
            }
            if(flag)//当前待提问表达式和已有的问题拥有相同的左右操作数，因此无需再问，直接跳过
                continue;
            int questionID=skylineQuery.hitnum+i;
            Task tsk=new Task(citem.oid,questionID,citem.crowdexp);//注意HIT成功生成之后才会更新hitnum值
            if(citem.crowdexp.lop.EXPTYPE==ExpressionType.VARIABLE)
            {
                DataItem ditem = ds.TotalData[Integer.parseInt( citem.crowdexp.lop.lop.toString())];
                tsk.data.add(ditem);
                //tsk.var.add(citem.crowdexp.lop);
            }
            if(citem.crowdexp.rop.EXPTYPE==ExpressionType.VARIABLE)
            {
                DataItem ditem = ds.TotalData[Integer.parseInt( citem.crowdexp.rop.lop.toString())];
                tsk.data.add(ditem);
                //tsk.var.add(citem.crowdexp.rop);
            }
            i++;
            tasks.put("question"+String.valueOf(questionID),tsk);
            content.append(tsk.XMLQuestion());
        }
        BufferedWriter bw= new BufferedWriter(new FileWriter(xmlfile));
        bw.write(prefix);
        bw.write(content.toString());
        bw.write(suffix);
        bw.flush();
        bw.close();
        this.tasks=tasks;
        return i;
    }

    public void RecordVarsInCondition(int oid, ArrayList<ArrayList<Expression>> cond)
    {
        for(ArrayList<Expression> l:cond)
        {
            for(Expression e:l)
            {
                if(e.lop.EXPTYPE== ExpressionType.VARIABLE)
                {
                    Set<Integer> value=varInConditions.get(e.lop);
                    if(value==null)
                    {
                        value=new HashSet<Integer>();
                        value.add(oid);
                        varInConditions.put(e.lop,value);
                    }
                    else
                    {
                        value.add(oid);
                    }
                }

                if(e.rop.EXPTYPE== ExpressionType.VARIABLE)
                {
                    Set<Integer> value=varInConditions.get(e.rop);
                    if(value==null)
                    {
                        value=new HashSet<Integer>();
                        value.add(oid);
                        varInConditions.put(e.rop,value);
                    }
                    else
                    {
                        value.add(oid);
                    }
                }
            }
        }
    }



}
