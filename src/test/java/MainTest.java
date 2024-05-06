import org.bits.ss.Main;
import org.junit.Test;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.bits.ss.Main.APP_NAME;
import static org.junit.Assert.assertEquals;

public class MainTest {

    @Test
    public void testEndpoint() throws URISyntaxException, IOException, InterruptedException {
        int port = 9000;
        Main.startServer(port);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + port + "/" + APP_NAME + "/api/get"))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Main.stopServer();
    }

}
