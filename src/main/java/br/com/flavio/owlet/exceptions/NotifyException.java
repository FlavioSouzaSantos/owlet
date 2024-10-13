package br.com.flavio.owlet.exceptions;

public class NotifyException extends RuntimeException {

    public NotifyException(String message) {
        super(message);
    }

    public NotifyException(String message, Throwable cause) {
        super(message, cause);
    }
}
