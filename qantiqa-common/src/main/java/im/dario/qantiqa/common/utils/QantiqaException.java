package im.dario.qantiqa.common.utils;

public class QantiqaException extends Exception {

    private static final long serialVersionUID = 5186024946937923090L;

    private int status = 500;

    public QantiqaException(String message) {
        super(message);
    }

    public QantiqaException(Exception e) {
        super(e);
    }

    public QantiqaException status(int status) {
        this.status = status;

        return this;
    }

    public int getStatus() {
        return this.status;
    }
}
