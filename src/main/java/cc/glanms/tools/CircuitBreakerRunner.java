package cc.glanms.tools;

import java.util.function.Supplier;

/**
 * Created by glanms on 2020/9/20.
 */
public class CircuitBreakerRunner {

    /**
     * 执行特定的方法
     *
     * @param circuitBreaker
     * @param callMethod
     * @param <T>
     * @return
     */
    public static <T> T run(CircuitBreaker<T> circuitBreaker, Supplier<T> callMethod) {
        if (!circuitBreaker.isOpen()) {
            // close
            try {
                T resp = callMethod.get();
                circuitBreaker.markSuccess();
                return resp;
            } catch (Exception e) {
                // add err cnt
                e.printStackTrace();
                circuitBreaker.markFailed();
            }
        } else {
            if (circuitBreaker.attemptExecution()) {
                // allow try again
                try {
                    T resp = callMethod.get();
                    circuitBreaker.markSuccess();
                    return resp;
                } catch (Exception e) {
                    e.printStackTrace();
                    // re-open
                    circuitBreaker.markNonSuccess();
                }
            }
        }
        return circuitBreaker.callbackMethod();
    }

}
