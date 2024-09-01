package ru.itmo.wp.web.exception;

public class RedirectException extends RuntimeException {
    private final String target;

    public RedirectException(String target) {
        this.target = target;
    }

    public String getTarget() {
        return target;
    }
}
