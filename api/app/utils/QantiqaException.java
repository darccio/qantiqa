package utils;

public class QantiqaException extends Exception {

    public QantiqaException(String message) {
        super(message);
    }

    public QantiqaException(Exception e) {
        super(e);
    }
}
