package web.mbean;

public interface ClickTimerMBean {
    double getAverageIntervalSeconds();
    void reset();
}