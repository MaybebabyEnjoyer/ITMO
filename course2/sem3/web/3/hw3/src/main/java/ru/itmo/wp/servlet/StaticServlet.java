package ru.itmo.wp.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class StaticServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        String[] files = uri.split("\\+");

        try (OutputStream outputStream = response.getOutputStream()) {
            for (String s : files) {
                File file = new File("/Users/lollysens/WebHW/ThirdHW/hw3/src/main/webapp/static", s);
                if (!file.isFile()) {
                    file = new File(getServletContext().getRealPath("/static/" + s));
                }
                if (file.isFile()) {
                    Files.copy(file.toPath(), outputStream);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            }
        }
        response.setContentType(getServletContext().getMimeType(files[0]));
    }
}
