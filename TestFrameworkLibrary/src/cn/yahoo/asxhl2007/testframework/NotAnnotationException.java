package cn.yahoo.asxhl2007.testframework;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * 缺少注释时抛出此异常
 * 
 * @author yangcheng
 * 
 */
public class NotAnnotationException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String message = "缺少必要的注解\n";

    public NotAnnotationException() {

    }

    public NotAnnotationException(String message) {
        this.message = message + "\n";
    }

    @Override
    public String getLocalizedMessage() {
        return message + super.getLocalizedMessage();
    }

    @Override
    public String getMessage() {
        return message + super.getMessage();
    }

    @Override
    public void printStackTrace(PrintStream ps) {
        ps.append(message);
        super.printStackTrace(ps);
    }

    @Override
    public void printStackTrace(PrintWriter pw) {
        pw.append(message);
        super.printStackTrace(pw);
    }

    @Override
    public String toString() {
        return message + super.toString();
    }

}
