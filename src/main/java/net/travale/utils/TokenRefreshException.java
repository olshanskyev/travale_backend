package net.travale.utils;

public class TokenRefreshException extends Exception {
    TokenRefreshException(String message) {
        super(message);
    }
    TokenRefreshException(String message, Throwable cause) {
        super(message, cause);
    }
}
