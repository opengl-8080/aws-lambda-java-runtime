package lambda;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) throws Exception {
        final Function function = new Function();
        final String awsLambdaRuntimeApi = System.getenv("AWS_LAMBDA_RUNTIME_API");

        final String uriBase = "http://" + awsLambdaRuntimeApi + "/2018-06-01/runtime/invocation";

        final URI nextInvocationUri = URI.create(uriBase + "/next");
        final HttpRequest nextInvocationRequest = HttpRequest.newBuilder(nextInvocationUri).GET().build();
        final HttpResponse.BodyHandler<String> stringBodyHandler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

        final HttpClient client = HttpClient.newHttpClient();
        while (true) {
            final HttpResponse<String> event = client.send(nextInvocationRequest, stringBodyHandler);

            final String response = function.invoke(event.body());


            final HttpHeaders headers = event.headers();
            final String requestId = headers.firstValue("Lambda-Runtime-Aws-Request-Id").orElseThrow();
            System.out.println("requestId=" + requestId);

            final URI responseUri = URI.create(uriBase + "/" + requestId + "/response");
            final HttpRequest responseRequest = HttpRequest.newBuilder(responseUri).POST(HttpRequest.BodyPublishers.ofString(response)).build();
            client.send(responseRequest, HttpResponse.BodyHandlers.discarding());
        }
    }
}
