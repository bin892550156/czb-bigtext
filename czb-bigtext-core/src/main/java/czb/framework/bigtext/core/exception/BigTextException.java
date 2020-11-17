package czb.framework.bigtext.core.exception;

/**
 * Czb大文本异常类
 *
 * @author chenzhuobin
 * @since 2020/11/13 0013
 */
public class BigTextException extends RuntimeException{

    public BigTextException(String message) {
        super(message);
    }

    public BigTextException(String message, Throwable cause) {
        super(message, cause);
    }
}
