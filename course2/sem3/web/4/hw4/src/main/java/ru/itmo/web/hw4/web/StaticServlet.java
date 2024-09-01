package ru.itmo.web.hw4.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class StaticServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        File file = new File("/Users/lollysens/WebHW/FourthHw/hw4/src/main/webapp" + request.getRequestURI());
        if (!file.isFile()) {
            file = new File(getServletContext().getRealPath(request.getRequestURI()));
        }
        if (file.isFile()) {
            response.setContentType(getServletContext().getMimeType(file.getName()));
            Files.copy(file.toPath(), response.getOutputStream());
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
