package cc.glanms.tools;

/**
 * Created by glanms on 2020/9/20.
 */
public interface CircuitBreaker<R> {

    String getName();

    boolean allowRequest();

    boolean isOpen();

    void markSuccess();

    void markNonSuccess();

    void markFailed();

    boolean attemptExecution();

    R callbackMethod();
}
