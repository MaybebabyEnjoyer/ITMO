package ru.itmo.wp.servlet;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GzipFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String acceptEncodingHeaderValue = request.getHeader("Accept-Encoding");
        if (acceptEncodingHeaderValue != null
                && acceptEncodingHeaderValue.toLowerCase().contains("gzip")) {
            DelayedHttpServletResponse delayedResponse = new DelayedHttpServletResponse(response);
            response.setHeader("Content-Encoding", "gzip");
            chain.doFilter(request, delayedResponse);
            ServletOutputStream outputStream = response.getOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
            gzipOutputStream.write(delayedResponse.getDelayedOutputStream().toByteArray());
            gzipOutputStream.close();
        } else {
            chain.doFilter(request, response);
        }
    }
}
