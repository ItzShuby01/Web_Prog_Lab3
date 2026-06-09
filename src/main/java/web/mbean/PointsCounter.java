package web.mbean;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

public class PointsCounter extends NotificationBroadcasterSupport implements PointsCounterMBean {
    private int totalPoints = 0;
    private int pointsInsideArea = 0;
    private long sequenceNumber = 1;

    @Override
    public int getTotalPoints() {
        return totalPoints;
    }

    @Override
    public int getPointsInsideArea() {
        return pointsInsideArea;
    }

    @Override
    public synchronized void reset() {
        this.totalPoints = 0;
        this.pointsInsideArea = 0;
    }

    // Called whenever point is processed
    public synchronized void registerNewPoint(boolean isInside) {
        totalPoints++;
        if (isInside) {
            pointsInsideArea++;
        }

        // Trigger JMX notification if point count is a multiple of 15
        if (totalPoints > 0 && totalPoints % 15 == 0) {
            sendPointsNotification();
        }
    }

    private void sendPointsNotification() {
        Notification notification = new Notification(
                "web.mbean.points.multiple15",
                this,
                sequenceNumber++,
                System.currentTimeMillis(),
                "Total point milestone hit! Current count: " + totalPoints
        );
        sendNotification(notification);
    }
}