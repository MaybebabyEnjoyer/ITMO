package ru.itmo.wp.web;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class StaticFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String uri = request.getRequestURI();

        File file = new File(getServletContext().getRealPath(".") + "/../../src/main/webapp" + uri);
        if (!file.isFile()) {
            file = new File(getServletContext().getRealPath(uri));
        }

        if (file.isFile()) {
            response.setContentType(getServletContext().getMimeType(file.getCanonicalPath()));
            Files.copy(file.toPath(), response.getOutputStream());
        } else {
            super.doFilter(request, response, chain);
        }
    }
}
