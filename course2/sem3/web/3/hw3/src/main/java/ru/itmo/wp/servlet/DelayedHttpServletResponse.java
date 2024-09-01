package ru.itmo.wp.servlet;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class DelayedHttpServletResponse extends HttpServletResponseWrapper {
    private final ByteArrayOutputStream delayedOutputStream = new ByteArrayOutputStream();
    private ServletOutputStream servletOutputStream;
    private PrintWriter writer;

    DelayedHttpServletResponse(HttpServletResponse response) {
        super(response);
    }

    ByteArrayOutputStream getDelayedOutputStream() {
        return delayedOutputStream;
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (servletOutputStream == null) {
            servletOutputStream = new ServletOutputStreamWrapper(delayedOutputStream);
        }
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() {
        if (writer == null) {
            writer = new PrintWriter(getOutputStream());
        }
        return writer;
    }

    private static final class ServletOutputStreamWrapper extends ServletOutputStream {
        private final OutputStream outputStream;
        private WriteListener writeListener;

        private ServletOutputStreamWrapper(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            this.writeListener = writeListener;
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
            if (writeListener != null) {
                writeListener.notify();
            }
        }
    }

}
