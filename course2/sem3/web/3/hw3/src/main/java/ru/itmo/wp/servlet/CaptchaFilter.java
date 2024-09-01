package ru.itmo.wp.servlet;

import ru.itmo.wp.util.ImageUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

public class CaptchaFilter extends HttpFilter {
    private static final String CAPTCHA = "captcha";
    private static final String PASS = "pass";

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request.getMethod().equals("GET")) {
            HttpSession session = request.getSession();

            String sessionCaptcha = (String) session.getAttribute(CAPTCHA);
            if (sessionCaptcha == null) {
                doCaptcha(request, response);
            } else {
                String requestCaptcha = request.getParameter(CAPTCHA);
                if (sessionCaptcha.equals(PASS) || request.getRequestURI().equals("/favicon.ico")) {
                    chain.doFilter(request, response);
                } else if (sessionCaptcha.equals(requestCaptcha)) {
                    session.setAttribute(CAPTCHA, PASS);
                    chain.doFilter(request, response);
                } else {
                    doCaptcha(request, response);
                }
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    private void doCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Random random = new Random();
        String randNum = Integer.toString(random.nextInt(900) + 100);
        request.getSession().setAttribute(CAPTCHA, randNum);
        byte[] img = ImageUtils.toPng(randNum);
        String imgString = Base64.getEncoder().encodeToString(img);
        response.getWriter().print("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Captcha</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <img src=\"data:image/png;base64," + imgString + "\"/>\n" +
                "    <form method=\"get\" action=\"" + request.getRequestURI() + "\">\n" +
                "        <label for=\"captcha\">Enter number:</label>\n" +
                "        <input name=\"captcha\" id=\"captcha\">\n" +
                "    </form>\n" +
                "</body>\n" +
                "</html>");
        response.getWriter().flush();
    }
}
