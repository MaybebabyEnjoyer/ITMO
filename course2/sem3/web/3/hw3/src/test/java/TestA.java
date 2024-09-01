import com.github.mjeanroy.junit.servers.annotations.TestServer;
import com.github.mjeanroy.junit.servers.annotations.TestServerConfiguration;
import com.github.mjeanroy.junit.servers.junit4.JunitServerRunner;
import com.github.mjeanroy.junit.servers.tomcat.EmbeddedTomcat;
import com.github.mjeanroy.junit.servers.tomcat.EmbeddedTomcatConfiguration;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JunitServerRunner.class)
public class TestA {
    @SuppressWarnings("unused")
    @TestServerConfiguration
    private final EmbeddedTomcatConfiguration configuration = EmbeddedTomcatConfiguration.builder()
            .withWebapp("target/hw3")
            .withClasspath("target/classes")
            .build();

    @SuppressWarnings("unused")
    @TestServer
    private static EmbeddedTomcat tomcat;

    private final CloseableHttpClient httpClient = HttpClients.createMinimal();

    private CloseableHttpResponse doGet(String url) throws IOException {
        HttpGet request = new HttpGet(tomcat.getUrl() + url);
        return httpClient.execute(request);
    }

    @Test
    public void testIndexHtml() throws IOException {
        for (String url: new String[] {"/index.html", "/css/normalize.css", "/img/logo.png"}) {
            CloseableHttpResponse response = doGet(url);
            assertEquals(200, response.getStatusLine().getStatusCode());
            response.close();
        }

        {
            HttpGet request = new HttpGet(tomcat.getUrl() + "/index.html");
            request.addHeader("Accept-Encoding", "gzip");
            CloseableHttpResponse response = httpClient.execute(request);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            System.out.println(response.getEntity().getContentEncoding());
            response.getEntity().writeTo(byteArrayOutputStream);
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            String content =  new java.util.Scanner(gzipInputStream).useDelimiter("\\A").next();
            assertTrue(content.contains("<li><a href=\"#\">"));
        }

    }

    @Test
    public void test404() throws IOException {
        for (String url: new String[] {"/index2.html", "/css", "/css/index.html", "/index"}) {
            CloseableHttpResponse response = doGet(url);
            assertEquals(404, response.getStatusLine().getStatusCode());
            response.close();
        }
    }

    @Test
    public void testReload() throws IOException {
        File cssDir = new File("./src/main/webapp/static/css");
        assertTrue(cssDir.isDirectory());

        //noinspection ConstantConditions
        for (File child : cssDir.listFiles()) {
            if (child.isFile() && child.getName().startsWith("test")) {
                assertTrue(child.delete());
            }
        }

        String fileName = "test" + System.nanoTime() + ".html";
        File file = new File(cssDir, fileName);
        {
            CloseableHttpResponse response = doGet("/css/" + fileName);
            assertEquals(404, response.getStatusLine().getStatusCode());
            response.close();
        }
        String content = "Hello, World!\n" + System.nanoTime();
        Files.write(file.toPath(), content.getBytes(), StandardOpenOption.CREATE_NEW);
        {
            CloseableHttpResponse response = doGet("/css/" + fileName);
            assertEquals(200, response.getStatusLine().getStatusCode());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            response.getEntity().writeTo(byteArrayOutputStream);
            assertEquals(content, byteArrayOutputStream.toString());

            response.close();
        }
        content = "Hello, World!\n" + System.nanoTime() + "!!!";
        Files.write(file.toPath(), content.getBytes(), StandardOpenOption.CREATE);
        {
            CloseableHttpResponse response = doGet("/css/" + fileName);
            assertEquals(200, response.getStatusLine().getStatusCode());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            response.getEntity().writeTo(byteArrayOutputStream);
            assertEquals(content, byteArrayOutputStream.toString());

            response.close();
        }

        //noinspection ConstantConditions
        for (File child : cssDir.listFiles()) {
            if (child.isFile() && child.getName().startsWith("test")) {
                assertTrue(child.delete());
            }
        }
        {
            CloseableHttpResponse response = doGet("/css/" + fileName);
            assertEquals(404, response.getStatusLine().getStatusCode());
            response.close();
        }
    }
}
