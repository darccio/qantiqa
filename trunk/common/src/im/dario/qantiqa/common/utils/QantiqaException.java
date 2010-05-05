package im.dario.qantiqa.common.utils;

public class QantiqaException extends Exception {

    private static final long serialVersionUID = 5186024946937923090L;

    public QantiqaException(String message) {
        super(message);
    }

    public QantiqaException(Exception e) {
        super(e);
    }
}
