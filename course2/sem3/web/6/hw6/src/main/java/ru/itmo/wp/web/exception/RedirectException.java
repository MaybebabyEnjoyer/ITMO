package ru.itmo.wp.web.exception;

public class RedirectException extends RuntimeException {
    private final String location;

    public RedirectException(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}
