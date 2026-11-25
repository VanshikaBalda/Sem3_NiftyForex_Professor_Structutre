package forex.thread;
import java.util.concurrent.*;
public class BackgroundMonitor implements AutoCloseable {
 private final ScheduledExecutorService scheduler=Executors.newSingleThreadScheduledExecutor(r->{Thread t=new Thread(r,"forex-alert-monitor");t.setDaemon(true);return t;});
 public void start(){scheduler.scheduleAtFixedRate(()->System.out.println("[Monitor] Price alert monitor is running. Rates are simulated/static for this academic project."),30,60,TimeUnit.SECONDS);}
 public void close(){scheduler.shutdownNow();}
}
