import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.util.Optional.ofNullable;

public class HealthCheck {
    public static void main(String[] args) {
        try {
            var host = ofNullable(System.getenv("HOST"))
                    .orElse("0.0.0.0");
            var port = ofNullable(System.getenv("PORT"))
                    .map(Integer::parseInt).orElse(8090);
            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder()
                    .uri(URI.create("http://" + host + ":" + port + "/health"))
                    .header("accept", "*/*")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.err.println("Health check FAIL with status code: " + response.statusCode());
                System.exit(1);
            } else {
                System.err.println("Health check SUCCESS with status code: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Health check FAIL with exception: " + e);
            System.exit(1);
            return;
        }
    }
}
