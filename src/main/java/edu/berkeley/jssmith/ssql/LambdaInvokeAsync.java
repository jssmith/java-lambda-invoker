package edu.berkeley.jssmith.ssql;

//import software.amazon.awssdk.auth.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.utils.FunctionalUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * Asynchronous
 */
public class LambdaInvokeAsync {

    static class Logger {
        BufferedWriter w;
        Logger(String filename) throws FileNotFoundException {
            this.w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, true)));
        }
        synchronized void log(String msg) {
            try {
                w.write(msg);
                w.write("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        synchronized void close() throws IOException {
            try {
                w.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws Exception {
        String functionName = args[0];
        double sleepDuration = Double.parseDouble(args[1]);
        int numInvocations = Integer.parseInt(args[2]);
        String experimentId = args[3];

        long startTime = System.currentTimeMillis();
        System.out.println("testing lambda invocation");
        String message = String.format("{\"Sleep\":%f,\"ExperimentId\":\"%s\"}", sleepDuration, experimentId);
        final CountDownLatch ct = new CountDownLatch(numInvocations);
        final ByteBuffer messageBuffer = ByteBuffer.wrap(message.getBytes());
        final Logger logger = new Logger("lambda_async_log.json");
        for (int i = 0; i < numInvocations; i++) {
            LambdaAsyncClient c = LambdaAsyncClient.builder()
                    .region(Region.US_WEST_2)
//                    .credentialsProvider(ProfileCredentialsProvider.builder()
//                            .profileName("rise").build())
                    .build();
            final CompletableFuture<InvokeResponse> f = c.invoke(InvokeRequest.builder().functionName(functionName)
                    .payload(messageBuffer).build());
            f.whenComplete((res, err) -> {
                try {
                    if (res != null) {
                        Charset cs = Charset.forName("UTF-8");
                        String res_str = cs.decode(res.payload()).toString();
                        System.out.println(res_str);
                        System.out.println("finished testing lambda invocation");
                        logger.log(res_str);
                    } else {
                        System.out.println("finished with error");
                        err.printStackTrace();
                    }
                } finally {
                    FunctionalUtils.invokeSafely(c::close);
                    ct.countDown();
                    System.out.printf("Still pending completion %d\n", ct.getCount());
                }
            });
        }
        ct.await();
        long elapsedTime = System.currentTimeMillis() - startTime;
        logger.close();
        System.out.println("Done");
        System.out.printf("Invoked %d requests in %d ms for %f requests/sec\n",
                numInvocations, elapsedTime, numInvocations * 1000. / elapsedTime);
    }
}
