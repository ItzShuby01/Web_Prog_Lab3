package web.mbean;

public interface PointsCounterMBean {
    int getTotalPoints();
    int getPointsInsideArea();
    void reset();
}