package ru.itmo.wp.servlet;

import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageServlet extends HttpServlet {
    private static final String USER = "user";
    private static final String TEXT = "text";
    private static final String AUTH = "/message/auth";
    private static final String FINDALL = "/message/findAll";
    private static final String ADD = "/message/add";
    private static final List<Message> messages = new ArrayList<>();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws  IOException {
        String uri = request.getRequestURI();

        switch (uri) {
            case AUTH -> auth(request, response);
            case FINDALL -> findAll(response);
            case ADD -> add(request, response);
            default -> {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    private void auth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String user = request.getParameter(USER);
        if (user == null || user.trim().isEmpty()) {
            user = (String) request.getSession().getAttribute(USER);
            if (user == null) {
                user = "";
            }
        } else {
            request.getSession().setAttribute(USER, user);
        }
        printJson(response, user);
    }

    private void findAll(HttpServletResponse response) throws IOException {
        printJson(response, messages);
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String text = request.getParameter("text").trim();
        if (!text.isEmpty()) {
            messages.add(new Message(request.getSession().getAttribute(USER), request.getParameter(TEXT)));
            printJson(response, "");
        }
    }

    private void printJson(HttpServletResponse response, Object obj) throws IOException {
        response.getWriter().print(new Gson().toJson(obj));
        response.getWriter().flush();
    }

    private record Message(Object user, String text){}
}
