package edu.zju.amt;

import edu.zju.GlobalVariables;
import edu.zju.ctables.DataItem;
import edu.zju.ctables.Expressions.Cmp;
import edu.zju.ctables.Expressions.Expression;
import edu.zju.ctables.Expressions.ExpressionType;

import java.util.ArrayList;
import java.util.Random;

;
public class Task
{
    public int tid;//记录是第几个问题
    public int oid;//记录该问题选自哪一个对象的condition
    public int aid;//记录相关属性，即使是变量之间比较，涉及的属性也只能是一个
    public ArrayList<DataItem> data=new ArrayList<>();
    public Expression question;
    public boolean isTrue;

    public Task()
    {

    }

    public Task(int objid,int taskid,int attrid, Expression exp)
    {
        oid=objid;
        tid=taskid;
        aid=attrid;
        question=exp;
    }

    public Task(int objid,int taskid, Expression exp) throws Exception
    {
        oid=objid;
        tid=taskid;
        if(exp.lop.EXPTYPE!= ExpressionType.VARIABLE)
            throw new Exception("Invalid EXP parameter when construct TASK class!");
        aid= Integer.parseInt(exp.lop.rop.toString());
        question=exp;
    }

    public StringBuilder XMLQuestion()
    {
        boolean hasX=false;
        StringBuilder sb=new StringBuilder("<Question>\n" +
                "<QuestionIdentifier>");
        sb.append("question").append(String.valueOf(tid));
        sb.append("</QuestionIdentifier>\n" +
                "<DisplayName>comparison between variable and constant</DisplayName>\n" +
                "<IsRequired>true</IsRequired>\n" +
                "<QuestionContent>\n" +
                "<Text>");
        for(DataItem ditem:data)
        {
            String basicinfo="player ["+ditem.keyPair.getKey()+"] in Season ["+ditem.keyPair.getValue()+"]\n";
            sb.append(basicinfo);
            for(int i=0;i<ditem.realData.length;i++)
            {
                String tmp= String.valueOf(ditem.realData[i]);
                if(ditem.missingValuesIndicator[i]<0)
                {
                    if(i==aid)//是缺失的变量，需要用x或y表示
                    {
                        tmp=hasX?"Y":"X";
                        hasX=true;
                    }
                    else {//显示为MISS
                        tmp="MISS";
                    }
                }
                sb.append(GlobalVariables.AttributeName.values()[i]+"["+tmp+"]\t");
            }
            sb.append("\n");
        }

        sb.append("\n");
        //问题部分
        if(data.size()==1)
        {
            sb.append("Question: Is Level(X) "+ Cmp.opString( question.GetOperator())+question.rop.toString()+" ?\n");
        }
        else if(data.size()==2)
        {
            sb.append("Question: Is Level(X) "+ Cmp.opString( question.GetOperator())+" Level(Y) ?\n");
        }

        sb.append("</Text>\n" +
                "</QuestionContent>\n" +
                "<AnswerSpecification>\n" +
                "<SelectionAnswer>\n" +
                "<StyleSuggestion>radiobutton</StyleSuggestion>\n" +
                "<Selections>\n" +
                "<Selection>\n" +
                "<SelectionIdentifier>True</SelectionIdentifier>\n" +
                "<Text>Yes</Text>\n" +
                "</Selection>\n" +
                "<Selection>\n" +
                "<SelectionIdentifier>False</SelectionIdentifier>\n" +
                "<Text>No</Text>\n" +
                "</Selection>\n" +
                "</Selections>\n" +
                "</SelectionAnswer>\n" +
                "</AnswerSpecification>\n" +
                "</Question>\n");
        return sb;
    }

    public static void main(String[] args)
    {
        DataItem d1=new DataItem(3,17,"name1",2002);//o3
        DataItem d2=new DataItem(7,17,"name2",2003);//o7
        Random rand=new Random();

        //d1.realData[0]="Name1";
        for(int i=0;i<17;i++)
        {
            d1.realData[i]=i*10+ rand.nextInt(10);
            d1.data[i]=i;
            d2.realData[i]=(i+1)*10+ rand.nextInt(10);
            d2.data[i]=i+1;
        }
        d1.missingValuesIndicator[0]=-1;
        d1.missingValuesIndicator[4]=-1;
        d1.missingValuesIndicator[9]=-1;
        d1.missingValuesIndicator[10]=-1;
        d1.missingValuesIndicator[15]=-1;

        d2.missingValuesIndicator[3]=-1;
        d2.missingValuesIndicator[7]=-1;
        d2.missingValuesIndicator[9]=-1;
        d2.missingValuesIndicator[11]=-1;
        d2.missingValuesIndicator[15]=-1;

        Expression single=Expression.makeComparison(Expression.makeVar(3,4),Expression.makePrimitiveValue(5),Cmp.LT);
        Expression binary=Expression.makeComparison(Expression.makeVar(3,9),Expression.makeVar(7,9), Cmp.GT);

        Task tsk1=new Task(0,0,4,single);
        tsk1.data.add(d1);
        Task tsk2=new Task(0,1,9,binary);
        tsk2.data.add(d1);
        tsk2.data.add(d2);

        System.out.println(tsk1.XMLQuestion());
        System.out.println(tsk2.XMLQuestion());

    }
}
