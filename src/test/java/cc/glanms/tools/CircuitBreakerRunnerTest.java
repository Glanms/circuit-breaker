package cc.glanms.tools;

import org.junit.Test;

/**
 * Created by glanms on 2020/9/20.
 */
public class CircuitBreakerRunnerTest {


    @Test
    public void runTest() throws InterruptedException {
        final long sleepMills = 10 * 1000;  //1 mins sleep
        UserService userService = new UserService();
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failedThreshold(10)
                .totalThreshold(30)
                .sleepMills(sleepMills)
                .build();

        CircuitBreaker<String> circuitBreaker =
                new CircuitBreakerImpl<>("userService", userService::getUserNameCallback, config);

        for (int i = 0; i < 100; i++) {
            Thread.sleep(500);
            String userName = CircuitBreakerRunner.run(circuitBreaker, userService::getUserName);
            System.out.printf("[ RES-%d ] user: %s \n", i, userName);
        }
    }

}
