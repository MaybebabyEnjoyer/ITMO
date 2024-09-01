import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static org.junit.Assert.*;

public class Tests {
    private static final String ROOT_URL = "http://127.0.0.1:8088";

    @BeforeClass
    public static void before() {
        new Thread(() -> {
            try {
                HttpServer.main(new String[]{});
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }).start();
    }

    @Test
    public void testIndexHtml() throws Exception {
        Response response = doRequest("/index.html");
        assertEquals("325", response.getHeaders().get("Content-Length"));
        assertEquals(325, response.getBody().length);
        assertEquals("text/html", response.getHeaders().get("Content-Type"));
    }

    @Test
    public void testRoot() throws Exception {
        Response response = doRequest("");
        assertEquals("325", response.getHeaders().get("Content-Length"));
        assertEquals(325, response.getBody().length);
        assertEquals("text/html", response.getHeaders().get("Content-Type"));
    }

    @Test
    public void testImagesRoot() throws Exception {
        {
            Response response = doRequest("/images");
            assertEquals("12", response.getHeaders().get("Content-Length"));
            assertEquals(12, response.getBody().length);
            assertEquals("static_index", new String(response.getBody()));
            assertEquals("text/html", response.getHeaders().get("Content-Type"));
        }
        {
            Response response = doRequest("/images/");
            assertEquals("12", response.getHeaders().get("Content-Length"));
            assertEquals(12, response.getBody().length);
            assertEquals("static_index", new String(response.getBody()));
            assertEquals("text/html", response.getHeaders().get("Content-Type"));
        }
    }

    @Test
    public void testRootSlash() throws Exception {
        Response response = doRequest("/");
        assertEquals("325", response.getHeaders().get("Content-Length"));
        assertEquals(325, response.getBody().length);
        assertEquals("text/html", response.getHeaders().get("Content-Type"));
    }

    @Test
    public void testPng() throws Exception {
        Response response = doRequest("/images/t.png");
        assertEquals("115194", response.getHeaders().get("Content-Length"));
        assertEquals(115194, response.getBody().length);
        assertEquals("image/png", response.getHeaders().get("Content-Type"));
    }

    @Test
    public void testRussian() throws Exception {
        Files.copy(new File("static/_russian.html").toPath(),
                new File("static/русский.html").toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        {
            Response response = doRequest("/русский.html");
            assertEquals("92", response.getHeaders().get("Content-Length"));
            assertEquals(92, response.getBody().length);
            assertEquals("text/html", response.getHeaders().get("Content-Type"));
        }
        {
            Response response = doRequest("/%D1%80%D1%83%D1%81%D1%81%D0%BA%D0%B8%D0%B9.html");
            assertEquals("92", response.getHeaders().get("Content-Length"));
            assertEquals(92, response.getBody().length);
            assertEquals("text/html", response.getHeaders().get("Content-Type"));
        }
    }

    @Test
    public void testEtagUnchangedFile() throws Exception {
        Set<String> etags = new HashSet<>();
        int expectedSize = (int) new File("static/images/blog.png").length();
        for (int i = 0; i <  10; i++) {
            Response response = doRequest("/images/blog.png");
            assertEquals(Integer.toString(response.getBody().length), response.getHeaders().get("Content-Length"));
            assertEquals(expectedSize, response.getBody().length);
            assertEquals("image/png", response.getHeaders().get("Content-Type"));
            etags.add(response.getHeaders().get("ETag"));
        }
        assertEquals(1, etags.size());
        int length = etags.iterator().next().length();
        assertTrue(length >= 5);
        assertTrue(length <= 64);

        {
            Response response = doRequest("/images/blog.png", "If-None-Match", etags.iterator().next());
            assertEquals(304, response.getCode());
            assertEquals(0, response.getBody().length);
            assertEquals(etags.iterator().next(), response.getHeaders().get("ETag"));
        }
    }

    @Test
    public void testEtagChangedFile() throws Exception {
        Set<String> etags = new HashSet<>();
        int expectedSize = (int) new File("static/images/blog.png").length();
        for (int i = 0; i <  10; i++) {
            Response response = doRequest("/images/blog.png");
            assertEquals(Integer.toString(response.getBody().length), response.getHeaders().get("Content-Length"));
            assertEquals(expectedSize, response.getBody().length);
            assertEquals("image/png", response.getHeaders().get("Content-Type"));
            etags.add(response.getHeaders().get("ETag"));
        }
        assertEquals(1, etags.size());
        int etagLength = etags.iterator().next().length();
        assertTrue(etagLength >= 5);
        assertTrue(etagLength <= 64);

        {
            Response response = doRequest("/images/blog.png", "If-None-Match", etags.iterator().next());
            assertEquals(304, response.getCode());
            assertEquals(0, response.getBody().length);
            assertEquals(etags.iterator().next(), response.getHeaders().get("ETag"));
        }

        for (String file: new String[] {"1", "2"}) {
            File previousFile = new File("static/images/blog.png");
            File currentFile = new File("static/images/" + file + ".png");
            if (previousFile.length() != currentFile.length()) {
                //noinspection ResultOfMethodCallIgnored
                previousFile.delete();
                Files.copy(currentFile.toPath(), previousFile.toPath());
                break;
            }
        }

        String newEtag;
        {
            Response response = doRequest("/images/blog.png", "If-None-Match", etags.iterator().next());
            assertEquals(200, response.getCode());
            assertTrue(response.getBody().length > 0);
            assertNotEquals(etags.iterator().next(), response.getHeaders().get("ETag"));
            newEtag = response.getHeaders().get("ETag");
        }
        {
            Response response = doRequest("/images/blog.png", "If-None-Match", newEtag);
            assertEquals(304, response.getCode());
            assertEquals(0, response.getBody().length);
            assertEquals(newEtag, response.getHeaders().get("ETag"));
        }
    }

    private static Response doRequest(String url) throws Exception {
        return doRequest(url, null, null);
    }

    private static Response doRequest(String url, String headerName, String headerValue) throws Exception {
        Response response = new Response();

        if (!url.startsWith("http://")) {
            url = ROOT_URL + url;
        }

        URL urlObject = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        if (headerName != null && headerValue != null) {
            connection.setRequestProperty(headerName, headerValue);
        }
        response.setCode(connection.getResponseCode());
        if (response.getCode() == 200) {
            InputStream in = connection.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] b = new byte[65536];
            while (true) {
                int size = in.read(b);
                if (size < 0) {
                    break;
                }
                byteArrayOutputStream.write(b, 0, size);
            }
            in.close();
            response.setBody(byteArrayOutputStream.toByteArray());
        }

        for (Map.Entry<String, List<String>> e : connection.getHeaderFields().entrySet()) {
            if (!e.getValue().isEmpty()) {
                response.getHeaders().put(e.getKey(), e.getValue().get(0));
            }
        }

        return response;
    }

    private static final class Response {
        private int code;
        private Map<String, String> headers = new HashMap<>();
        private byte[] body = new byte[0];

        private int getCode() {
            return code;
        }

        private void setCode(int code) {
            this.code = code;
        }

        private Map<String, String> getHeaders() {
            return headers;
        }

        private void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        private byte[] getBody() {
            return body;
        }

        private void setBody(byte[] body) {
            this.body = body;
        }
    }
}
