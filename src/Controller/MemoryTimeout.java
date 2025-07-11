// Controller/MemoryTimeout.java
package Controller;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

// issue: no rate limiting for login attempts
// solution: implement a memory-based timeout tracker for failed login attempts
public class MemoryTimeout {
    private static final MemoryTimeout INSTANCE = new MemoryTimeout();
    private ConcurrentHashMap<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_TIME = TimeUnit.MINUTES.toMillis(15);
    
    private MemoryTimeout() {}
    
    public static MemoryTimeout getInstance() {
        return INSTANCE;
    }
    
    public boolean isLocked(String username) {
        LoginAttempt attempt = loginAttempts.get(username);
        
        if (attempt != null) {
            long timeSinceLastAttempt = System.currentTimeMillis() - attempt.getLastAttempt();
            
            if (attempt.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
                if (timeSinceLastAttempt < LOCKOUT_TIME) {
                    return true;  // Still locked
                } else {
                    loginAttempts.remove(username);  // Lock expired
                }
            }
        }
        return false;
    }

    public void incrementFailedAttempts(String username) {
        loginAttempts.compute(username, (key, attempt) -> {
            if (attempt == null) {
                System.out.println("First failed attempt for: " + username);
                return new LoginAttempt(1, System.currentTimeMillis());
            }
            attempt.incrementFailedAttempts();
            attempt.setLastAttempt(System.currentTimeMillis());
            System.out.println("Failed attempts for " + username + ": " + attempt.getFailedAttempts());
            return attempt;
        });
    }

    public void resetFailedAttempts(String username) {
        loginAttempts.remove(username);
        System.out.println("Reset failed attempts for: " + username);
    }

    private static class LoginAttempt {
        private int failedAttempts;
        private long lastAttempt;

        public LoginAttempt(int failedAttempts, long lastAttempt) {
            this.failedAttempts = failedAttempts;
            this.lastAttempt = lastAttempt;
        }

        public int getFailedAttempts() {
            return failedAttempts;
        }

        public void incrementFailedAttempts() {
            this.failedAttempts++;
        }

        public long getLastAttempt() {
            return lastAttempt;
        }

        public void setLastAttempt(long lastAttempt) {
            this.lastAttempt = lastAttempt;
        }
    }
}
