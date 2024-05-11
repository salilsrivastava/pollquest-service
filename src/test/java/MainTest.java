import com.sun.net.httpserver.HttpExchange;
import org.bits.ss.Main;
import org.bits.ss.handlers.RestHandler;
import org.bits.ss.service.PollQuestionService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.bits.ss.Main.APP_NAME;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MainTest {
    @Mock
    private PollQuestionService pollQuestionService;
    @InjectMocks
    private RestHandler restHandler;
    public MainTest(){
        MockitoAnnotations.initMocks(this);
    }
    int port = 9000;

    @Before
    public void setup() throws IOException {
        Main.startServer(port, restHandler);
    }

    @After
    public void stop(){
        Main.stopServer();
    }

    @Test
    public void testEndpoint() throws URISyntaxException, IOException, InterruptedException {

        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestURI()).thenReturn(new URI("/pollquest-service/api/v1?id=12AETI"));
        when(exchange.getRequestMethod()).thenReturn("GET");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + port + "/" + APP_NAME + "/api/v1?id=12AETI"))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());

    }

    @Test
    public void testResponseForGetQuestionsById() throws URISyntaxException, IOException, InterruptedException {
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestURI()).thenReturn(new URI("/pollquest-service/api/v1/questions?id=12AETI"));
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(pollQuestionService.getQuestions(any())).thenReturn("{\"quest\": []}");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + port + "/" + APP_NAME + "/api/v1/questions?id=12AETI"))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("{\"quest\": []}",response.body());

    }

    @Test
    public void testResponseForPostQuestions() throws URISyntaxException, IOException, InterruptedException {
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestURI()).thenReturn(new URI("/pollquest-service/api/v1/questions?id=12AETI"));
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(pollQuestionService.generateCode(any())).thenReturn("3504D1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:" + port + "/" + APP_NAME + "/api/v1/generateCode"))
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {
                            "description" : "Testing"
                        }
                        """))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("3504D1",response.body());

    }

}
