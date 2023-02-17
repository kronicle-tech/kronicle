import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.util.Optional.ofNullable;

public class HealthCheck {
    public static void main(String[] args) {
        var host = ofNullable(System.getenv("HOST"))
                .orElse("0.0.0.0");
        var port = ofNullable(System.getenv("PORT"))
                .map(Integer::parseInt).orElse(8090);
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + host + ":" + port + "/health"))
                .header("accept", "*/*")
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Health check failed with exception: " + e);
            System.exit(1);
            return;
        }
        if (response.statusCode() != 200) {
            System.err.println("Health check failed with status code: " + response.statusCode());
            System.exit(1);
        }
    }
}
