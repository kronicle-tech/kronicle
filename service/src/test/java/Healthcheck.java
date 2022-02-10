import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.util.Optional.ofNullable;

public class Healthcheck {
    public static void main(String[] args) {
        var host = ofNullable(System.getenv("HOST"))
                .orElse("localhost");
        var port = ofNullable(System.getenv("PORT"))
                .map(Integer::parseInt).orElse(80);
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + host + ":" + port + "/health"))
                .header("accept", "*/*")
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            System.err.println("Healthcheck failed with exception: " + ex);
            System.exit(1);
            return;
        }
        if (response.statusCode() != 200) {
            System.err.println("Healthcheck failed with status code: " + response.statusCode());
            System.exit(1);
        }
    }
}
