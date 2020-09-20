package cc.glanms.tools;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Created by glanms on 2020/9/20.
 */
public class CircuitBreakerImpl<R> implements CircuitBreaker {

    // cur status
    private static AtomicReference<Status> status = new AtomicReference<>(Status.CLOSED);
    // private static ConcurrentHashMap<String, CircuitBreaker> holder = new ConcurrentHashMap<>(6);
    // open time
    private final AtomicLong circuitOpened = new AtomicLong(-1);
    private final AtomicLong successCnt = new AtomicLong(0);
    private final AtomicLong failedCnt = new AtomicLong(0);
    private Supplier<R> callback;
    private String name; //method flag

    private CircuitBreakerConfig config;

    public CircuitBreakerImpl(String name, Supplier<R> callback) {
        this(name, callback, CircuitBreakerConfig.ofDefaults());
    }

    public CircuitBreakerImpl(String name, Supplier<R> callback, CircuitBreakerConfig config) {
        this.name = name;
        this.config = config;
        this.callback = callback;
    }

    @Override
    public boolean allowRequest() {
        if (circuitOpened.get() == -1) {
            return true;
        } else {
            if (status.get().equals(Status.HALF_OPEN)) {
                return false;
            } else {
                return isAfterSleepWindow();
            }
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isOpen() {
        return circuitOpened.get() >= 0;
    }

    @Override
    public void markSuccess() {
        successCnt.getAndIncrement();
        if (status.compareAndSet(Status.HALF_OPEN, Status.CLOSED)) {
            circuitOpened.set(-1L);
            successCnt.set(0);
            failedCnt.set(0);
            System.out.printf("switch: half_open -> closed | %s - %s  \n", successCnt.get(), failedCnt.get());
        }
    }

    @Override
    public void markFailed() {
        // add cnt
        failedCnt.getAndIncrement();
        if (overThreshold() && status.compareAndSet(Status.CLOSED, Status.OPEN)) {
            // update open time
            circuitOpened.set(System.currentTimeMillis());
            System.out.printf("switch: closed -> open | %s - %s  \n", successCnt.get(), failedCnt.get());
        }
    }

    @Override
    public void markNonSuccess() {
        if (status.compareAndSet(Status.HALF_OPEN, Status.OPEN)) {
            //re-open & update time
            circuitOpened.set(System.currentTimeMillis());
            System.out.printf("switch: half_open -> open | %s - %s  \n", successCnt.get(), failedCnt.get());
        }
    }

    @Override
    public boolean attemptExecution() {
        if (circuitOpened.get() == -1) {
            return true;
        } else {
            if (isAfterSleepWindow()) {
                if (status.compareAndSet(Status.OPEN, Status.HALF_OPEN)) {
                    //only the first request after sleep window should execute
                    System.out.printf("switch: open -> half_open | %s - %s  \n", successCnt.get(), failedCnt.get());
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    @Override
    public Object callbackMethod() {
        return callback.get();
    }

    private boolean overThreshold() {
        long totalCnt = successCnt.get() + failedCnt.get();
        long failed = failedCnt.get();
        System.out.println("total:" + totalCnt);
        if (failed >= config.getFailedThreshold()) {
            double rate = failed / totalCnt - config.getFailedThreshold() / config.getTotalThreshold();
            System.out.println("rate:" + rate);
            return rate >= 0;
        }
        return false;
    }

    private boolean isAfterSleepWindow() {
        final long circuitOpenTime = circuitOpened.get();
        final long currentTime = System.currentTimeMillis();
        return currentTime > circuitOpenTime + config.getSleepMills();
    }

}
