package cc.glanms.tools;

/**
 * Created by glanms on 2020/9/20.
 */
public class CircuitBreakerConfig {

    private static final int DEFAULT_FAILED_THRESHOLD = 10;
    private static final int DEFAULT_TOTAL_THRESHOLD = 50;
    private static final int DEFAULT_RECOVER_SECONDS = 30;

    private int failedThreshold;
    private int totalThreshold;
    private long sleepMills;

    public static CircuitBreakerConfig ofDefaults() {
        return new Builder().build();
    }

    public int getFailedThreshold() {
        return failedThreshold;
    }

    public int getTotalThreshold() {
        return totalThreshold;
    }

    public long getSleepMills() {
        return sleepMills;
    }

    public static Builder custom() {
        return new Builder();
    }

    public static class Builder {

        private int failedThreshold = DEFAULT_FAILED_THRESHOLD;
        private int totalThreshold = DEFAULT_TOTAL_THRESHOLD;
        private long sleepMills = DEFAULT_RECOVER_SECONDS;

        private Builder() {
        }

        public Builder failedThreshold(int failedThreshold) {
            if (failedThreshold <= 0)
                throw new IllegalArgumentException("err: failedThreshold <= 0");
            this.failedThreshold = failedThreshold;
            return this;
        }

        public Builder totalThreshold(int totalThreshold) {
            if (totalThreshold <= 0)
                throw new IllegalArgumentException("err: totalThreshold <= 0");
            this.totalThreshold = totalThreshold;
            return this;
        }

        public Builder sleepMills(long sleepMills) {
            if (sleepMills <= 0)
                throw new IllegalArgumentException("err: sleepMills <= 0");
            this.sleepMills = sleepMills;
            return this;
        }

        public CircuitBreakerConfig build() {
            if (failedThreshold > totalThreshold)
                throw new IllegalArgumentException("err: failedThreshold  > totalThreshold");
            CircuitBreakerConfig config = new CircuitBreakerConfig();
            config.failedThreshold = failedThreshold;
            config.totalThreshold = totalThreshold;
            config.sleepMills = sleepMills;
            return config;
        }
    }

}
