# Lambda Invoker Benchmark

This project contains test scripts to benchmark performance of the AWS Lambda invocation API using[version 2.0
of the AWS Java SDK](https://aws.amazon.com/blogs/developer/aws-sdk-for-java-2-0-developer-preview/) which is presently
available as a preview release.

## Building

Use Maven to build.

```
mvn package
```

## Running

Make sure your AWS environment is configured
```
aws configure
```
Follow the prompts to configure your local aws client

*Optional: If using a non-default profile set the environment variable, e.g.:*
```
export AWS_DEFAULT_PROFILE=rise
```

*Note that we have hardcoded the region to us-west-2. If you plan to run in a
different region please edit the Java source code (class LambdaInvokeAsync).*

Run the benchmark:
```
java -jar target/java-lambda-invoker-1.0-SNAPSHOT.jar [Function Name] [Sleep Argument] [Num Invocations] [Experiment Id]
```

Arguments are:
- *Function Name* - name of the AWS Lambda function to execute
- *Sleep Argument* - We pass a parameter "Sleep" = *Sleep Argument* to the function during invocation.
  The function may read this parameter and sleep in response.
- *Num Invocations* - How many times to call the function
- *Experiment Id* - We pass a parameter "ExperimentId" = *Experiment Id* to the function during invocation.
  The function may read this parameter and use it, e.g., in logs or other output.

For example:
```
java -jar target/java-lambda-invoker-1.0-SNAPSHOT.jar HelloPython .010 2 e200
```

This program will save the return values of function invocations in the file lambda_async_log.json.