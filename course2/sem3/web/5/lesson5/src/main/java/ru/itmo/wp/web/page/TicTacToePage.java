package ru.itmo.wp.web.page;

import ru.itmo.wp.model.State;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.Map;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class TicTacToePage {
    private static final String PAGE = "/ticTacToe";
    private static final String STATE = "state";

    private void action(HttpServletRequest request, Map<String, Object> view) {
        view.put(STATE, getState(request));
    }

    private void onMove(HttpServletRequest request, Map<String, Object> view) {
        Enumeration<String> params = request.getParameterNames();
        State state = getState(request);

        while (!state.isEnd() && params.hasMoreElements()) {
            String param = params.nextElement();
            if (param.matches("cell_[0-9]{2}")) {
                try {
                    int row = param.charAt(param.length() - 2) - '0';
                    int col = param.charAt(param.length() - 1) - '0';
                    state.makeMove(row, col);
                } catch (IllegalArgumentException ignored) {
                    // No operations.
                }
            }
        }
        view.put(STATE, state);
        throw new RedirectException(PAGE);
    }

    private void newGame(HttpServletRequest request, Map<String, Object> view) {
        request.getSession().setAttribute(STATE, new State(3));
        view.put(STATE, getState(request));
        throw new RedirectException(PAGE);
    }

    private State getState(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute(STATE) == null) {
            session.setAttribute(STATE, new State(3));
        }
        return (State) session.getAttribute(STATE);
    }
}
