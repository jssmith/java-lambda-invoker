package edu.berkeley.jssmith.ssql;

//import software.amazon.awssdk.auth.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.nio.charset.Charset;

/**
 * The LambdaInvoke class demonstrates usage of the synchronous
 * AWS API. This is an example and is not used in benchmarking.
 */
public class LambdaInvoke {
    public static void main(String[] args) {
        System.out.println("testing lambda invocation");
        LambdaClient c = LambdaClient.builder()
                .region(Region.US_WEST_2)
//                .credentialsProvider(ProfileCredentialsProvider.builder()
//                        .profileName("default").build())
                .build();
        InvokeResponse res = c.invoke(InvokeRequest.builder().functionName("TimeTest").build());
        Charset cs = Charset.forName("UTF-8");
        System.out.println(cs.decode(res.payload()).toString());
        System.out.println("finished testing lambda invocation");
    }
}
