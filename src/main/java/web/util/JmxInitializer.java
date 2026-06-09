package web.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import web.mbean.PointsCounter;
import web.mbean.ClickTimer;

@WebListener
public class JmxInitializer implements ServletContextListener {

    // Static references so JSF beans can access them
    private static PointsCounter pointsCounter;
    private static ClickTimer clickTimer;

    private ObjectName counterName;
    private ObjectName timerName;

    public static PointsCounter getPointsCounter() {
        return pointsCounter;
    }

    public static ClickTimer getClickTimer() {
        return clickTimer;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

            pointsCounter = new PointsCounter();
            counterName = new ObjectName("web.mbean:type=PointsCounter");
            mbs.registerMBean(pointsCounter, counterName);

            clickTimer = new ClickTimer();
            timerName = new ObjectName("web.mbean:type=ClickTimer");
            mbs.registerMBean(clickTimer, timerName);

            System.out.println("--- JMX MBeans successfully registered! ---");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            if (counterName != null) mbs.unregisterMBean(counterName);
            if (timerName != null) mbs.unregisterMBean(timerName);
            System.out.println("--- JMX MBeans successfully unregistered. ---");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}