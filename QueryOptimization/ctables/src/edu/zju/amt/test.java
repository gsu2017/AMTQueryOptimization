package edu.zju.amt;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.AmazonMTurkClientBuilder;
import com.amazonaws.services.mturk.model.CreateHITRequest;
import com.amazonaws.services.mturk.model.CreateHITResult;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by TLD7040A on 2017/6/26.
 * 形成一个可提交的众包问题
 * 需要众包问题的形式包括x<y(除需要众包的属性x之外还涉及另一属性y),x<3
 * 数据正则化过，所以形成问题需要恢复成原来数据
 * CASE x(<,>,=,!=,<=,>=)常数
 *
 *
 * CASE x(<,>,=,!=,<=,>=)y
 *
 *
 */
public class test
{
    private static final String SANDBOX_ENDPOINT = "mturk-requester-sandbox.us-east-1.amazonaws.com";
    private static final String SIGNING_REGION = "us-east-1";

    private static AmazonMTurk getSandboxClient() {
        AmazonMTurkClientBuilder builder = AmazonMTurkClientBuilder.standard();
        builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(SANDBOX_ENDPOINT, SIGNING_REGION));
        return builder.build();
    }

    public static void CreateFirstHit() throws Exception
    {

        String QUESTION_XML_FILE_NAME = "C:\\Users\\TLD7040A\\IdeaProjects\\QueryOptimization\\ctables\\src\\test0.xml";
        AmazonMTurk client= getSandboxClient();
        String questionSample=new String(Files.readAllBytes(Paths.get(QUESTION_XML_FILE_NAME)));
        CreateHITRequest request = new CreateHITRequest();
        request.setMaxAssignments(2);
        request.setLifetimeInSeconds(600L);
        request.setAssignmentDurationInSeconds(600L);
        // Reward is a USD dollar amount - USD$0.20 in the example below
        request.setReward("0.05");
        request.setTitle("Sample-best basketball player");
        request.setKeywords("question, answer, research, basketball");
        request.setDescription("Answer a simple question");
        request.setQuestion(questionSample);

        CreateHITResult result = client.createHIT(request);
        System.out.println(result.getHIT().getHITId());
    }

    public static int test()
    {
        int x=1;
        try
        {
            System.out.println("in try block");
            return x;
        }finally
        {
            System.out.println("in finally block");
            x=10;
            return x;
        }
    }


    public static void main(String[] args) throws Exception
    {
        test.CreateFirstHit();
        //System.out.println(test.test());
    }
}
