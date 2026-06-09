package web.mbean;

public class ClickTimer implements ClickTimerMBean {
    private long lastClickTime = 0;
    private long totalIntervalTimeMs = 0;
    private int clickCount = 0;

    @Override
    public synchronized double getAverageIntervalSeconds() {
        if (clickCount < 1) {
            return 0.0;
        }
        return (totalIntervalTimeMs / (double) clickCount) / 1000.0;
    }

    @Override
    public synchronized void reset() {
        this.lastClickTime = 0;
        this.totalIntervalTimeMs = 0;
        this.clickCount = 0;
    }

    // Called whenever a user submits coordinate
    public synchronized void registerClick() {
        long currentTime = System.currentTimeMillis();
        if (lastClickTime != 0) {
            long interval = currentTime - lastClickTime;
            totalIntervalTimeMs += interval;
            clickCount++;
        }
        lastClickTime = currentTime;
    }
}