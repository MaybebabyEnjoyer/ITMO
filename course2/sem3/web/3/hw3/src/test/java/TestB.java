import com.github.mjeanroy.junit.servers.annotations.TestServer;
import com.github.mjeanroy.junit.servers.junit4.JunitServerRunner;
import com.github.mjeanroy.junit.servers.tomcat.EmbeddedTomcat;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(JunitServerRunner.class)
public class TestB {
    @SuppressWarnings("unused")
    @TestServer
    private static EmbeddedTomcat tomcat;

    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    private CloseableHttpResponse doGet(String url) throws IOException {
        HttpGet request = new HttpGet(tomcat.getUrl() + url);
        return httpClient.execute(request);
    }

    @Test
    public void testRGB() throws IOException {
        for (String url : new String[]{"/css/r.css+css/g.css+css/b.css",
                "/css/r.css+css/g.css+/css/b.css",
                "/css/r.css+/css/g.css+/css/b.css"}) {
            CloseableHttpResponse response = doGet(url);
            assertEquals(200, response.getStatusLine().getStatusCode());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            response.getEntity().writeTo(byteArrayOutputStream);
            TestUtils.assertNearlyEquals(357, byteArrayOutputStream.toByteArray().length);
            assertEquals("text/css", response.getEntity().getContentType().getValue());
            response.close();
        }
    }

    @Test
    public void test200() throws IOException {
        for (String url : new String[]{"index.html+/css/r.css+img/itmo_ru.png"}) {
            CloseableHttpResponse response = doGet(url);
            assertEquals(200, response.getStatusLine().getStatusCode());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            response.getEntity().writeTo(byteArrayOutputStream);
            TestUtils.assertNearlyEquals(40487, byteArrayOutputStream.toByteArray().length);
            assertEquals("text/html", response.getEntity().getContentType().getValue());
            response.close();
        }
    }

    @Test
    public void testManyTimesR() throws IOException {
        StringBuilder url = new StringBuilder("/css/r.css");
        for (int i = 0; i + 1 < 10; i++) {
            url.append("+css/r.css");
        }
        CloseableHttpResponse response = doGet(url.toString());
        assertEquals(200, response.getStatusLine().getStatusCode());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        response.getEntity().writeTo(byteArrayOutputStream);
        TestUtils.assertNearlyEquals(1170, byteArrayOutputStream.toByteArray().length);
        response.close();
    }

    @Test
    public void test404() throws IOException {
        for (String url : new String[]{"/css/r.css+css/g.css+b.css", "/css/r.css+css/index.html", "/css/r.css+index.htm"}) {
            CloseableHttpResponse response = doGet(url);
            assertEquals(404, response.getStatusLine().getStatusCode());
            response.close();
        }
    }

}
