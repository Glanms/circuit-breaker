package cc.glanms.tools;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Unit test for simple App.
 */
public class UserService {

    private static AtomicLong counter = new AtomicLong(0);

    public String getUserName() {
        if (counter.addAndGet(1) % 3 == 0) {
            throw new IllegalStateException("user status error....");
        }
        return "Jimmy";
    }


    public String getUserNameCallback() {
        return "Unknown";
    }

}
