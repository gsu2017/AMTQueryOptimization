package edu.zju.amt;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.AmazonMTurkClientBuilder;
import com.amazonaws.services.mturk.model.*;
import edu.zju.ctables.skylineQuery;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TLD7040A on 2017/7/4.
 */
public class HITUtils
{
    private static final String SANDBOX_ENDPOINT = "mturk-requester-sandbox.us-east-1.amazonaws.com";
    private static final String SIGNING_REGION = "us-east-1";
    private final AmazonMTurk client;
    private String hittypeid="";

    public HITUtils(AmazonMTurk client)
    {
        this.client=client;
    }
    public HITUtils(AmazonMTurk client,String HITTypeId)
    {
        this.client=client;
        this.hittypeid=HITTypeId;
    }

    public void SetHITTypeId(String id)
    {
        hittypeid=id;
    }
    public static AmazonMTurk getSandboxClient() {
        AmazonMTurkClientBuilder builder = AmazonMTurkClientBuilder.standard();
        builder.setEndpointConfiguration(new EndpointConfiguration(SANDBOX_ENDPOINT, SIGNING_REGION));
        return builder.build();
    }

    public String CreateHITTYPE()
    {
        /*
            "Title": String,
            "Description": String,
            "Reward": String,
            "AssignmentDurationInSeconds": Integer,
            "Keywords": String,
            "AutoApprovalDelayInSeconds": Integer,
            "QualificationRequirements": QualificationRequirementList (p. 138)
        */
        CreateHITTypeRequest request=new CreateHITTypeRequest();
        request.setReward("0.00");
        request.setTitle("Best Basketball Player");
        request.setKeywords("basketball, question, binary-choice, research");
        request.setDescription("Answer a simple question");
        request.setAutoApprovalDelayInSeconds(864000L);
        request.setAssignmentDurationInSeconds(3600L);

        CreateHITTypeResult result = client.createHITType(request);
        hittypeid = result.getHITTypeId();
        return hittypeid;
    }
    //返回hitid
    //questionNum用于设置reviewpolicy参数,startId表示起始问题的id编号
    public String CreateHIT(String xmlFile,int questionNum,int startId) throws Exception
    {
        // Read the question XML into a String
        String question = new String(Files.readAllBytes(Paths.get(xmlFile)));

        CreateHITWithHITTypeRequest request=new CreateHITWithHITTypeRequest();

        ArrayList<PolicyParameter> policyparams=new ArrayList<>();
        PolicyParameter pp1=new PolicyParameter();
        ArrayList<String> qids=new ArrayList<>();
        for(int i=0;i<questionNum;i++)
        {
            String str="question"+String.valueOf(i+startId);
            qids.add(str);
        }
        pp1.setKey("QuestionIds");
        pp1.setValues(qids);
        PolicyParameter pp2=new PolicyParameter();
        pp2.setKey("QuestionAgreementThreshold");
        pp2.setValues(new ArrayList<String>(){{add(String.valueOf(50));}});
        PolicyParameter pp3=new PolicyParameter();
        pp3.setKey("DisregardAssignmentIfRejected");
        pp3.setValues(new ArrayList<String>(){{add(String.valueOf(true));}});
        PolicyParameter pp4=new PolicyParameter();
        pp4.setKey("RejectIfWorkerAgreementScoreIsLessThan");
        pp4.setValues(new ArrayList<String>(){{add(String.valueOf((int)Math.floor((double) 100/(double) questionNum)));}});//没有任何答案是agreed answer，则reject
        PolicyParameter pp5=new PolicyParameter();
        pp5.setKey("RejectReason");
        pp5.setValues(new ArrayList<String>(){{add("Sorry, none of your answer is correct. Maybe you should check the instructions again. Thanks anyway.");}});
        policyparams.add(pp1);
        policyparams.add(pp2);
        policyparams.add(pp3);
        policyparams.add(pp4);
        policyparams.add(pp5);

        ReviewPolicy reviewpolicy=new ReviewPolicy();
        reviewpolicy.setPolicyName("SimplePlurality/2011-09-01");
        reviewpolicy.setParameters(policyparams);

        request.setHITTypeId(hittypeid);
        request.setHITReviewPolicy(reviewpolicy);
        request.setMaxAssignments(3);
        request.setLifetimeInSeconds(3600L);
        request.setQuestion(question);

        CreateHITWithHITTypeResult result = client.createHITWithHITType(request);
        //System.out.println(result.getHIT());
        System.out.println("Your HIT has been created. You can see it at this link:");
        System.out.println("https://workersandbox.mturk.com/mturk/preview?groupId=" + result.getHIT().getHITTypeId());
        System.out.println("Your HIT ID is: " + result.getHIT().getHITId());
        return result.getHIT().getHITId();
    }

    public boolean IsHITReviewable(String hitid)
    {
        boolean rslt=false;
        GetHITRequest request=new GetHITRequest();
        request.setHITId(hitid);
        GetHITResult result=client.getHIT(request);
        System.out.println("HIT " + hitid + " status: " + result.getHIT().getHITStatus());
        if(result.getHIT().getHITStatus().equals("Reviewable"))
        {
            rslt=true;
        }
        return rslt;
    }

    public void WriteReviewResultsToFile(String filename,List<ReviewResultDetail> results) throws Exception
    {
        File file = new File(filename);
        BufferedWriter bfw = new BufferedWriter(new FileWriter(file,true));

        if (results == null)
        {
            System.out.println("No results for policy SimplePlurality/2011-09-01");
        } else
        {
            bfw.write("Results for policy SimplePlurality/2011-09-01:\n");
            for (ReviewResultDetail result : results)
            {
                String subjectType = result.getSubjectType();
                String subjectId = result.getSubjectId();
                String questionId = result.getQuestionId();
                String key = result.getKey();
                String value = result.getValue();
                if (questionId == null || questionId.equals(""))
                {
                    bfw.write("- " + subjectType + " " + subjectId + ": " + key + " is " + value+"\n");
                } else
                {
                    bfw.write("- " + subjectType + " " + subjectId + ": " + key + " for " + questionId + " is " + value+"\n");
                }
            }
        }
        bfw.write("\n\n");
        bfw.flush();
        bfw.close();
    }

    public HashMap<String,Boolean> CollectAnswer(String hitid,int qnum)throws Exception
    {
        int num=0;
        HashMap<String,Boolean> rslt=new HashMap<>();
        ListReviewPolicyResultsForHITRequest request = new ListReviewPolicyResultsForHITRequest();
        request.setHITId(hitid);//ReviewPolicyLevel.HIT
        request.setPolicyLevels(new ArrayList<String>()
        {{
            add("HIT");
        }});
        ListReviewPolicyResultsForHITResult reviewpolicyresult = client.listReviewPolicyResultsForHIT(request);
        ReviewReport report = reviewpolicyresult.getHITReviewReport();

        if (report == null)
        {
            System.out.println("No data for policy SimplePlurality/2011-09-01");
        } else
        {
            // Print review results
            List<ReviewResultDetail> results = report.getReviewResults();
            WriteReviewResultsToFile(skylineQuery.HITFile,results);//review policy结果写入文件中
            if (results == null)
            {
                System.out.println("No results for policy SimplePlurality/2011-09-01");
            } else
            {
                System.out.println("Results for policy SimplePlurality/2011-09-01:");
                for (ReviewResultDetail result : results)
                {
                    if(num==qnum)
                        break;
                    String questionId = result.getQuestionId();
                    String key = result.getKey();
                    String value = result.getValue();
                    if (questionId != null && !questionId.equals("") && key.equals("AgreedAnswer"))
                    {
                        rslt.put(questionId,Boolean.valueOf(value));
                        num++;
                    }
                }
            }
        }

        return rslt;
    }

}
