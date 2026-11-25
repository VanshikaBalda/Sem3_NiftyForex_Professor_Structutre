package forex.main;
import forex.thread.BackgroundMonitor;
public class Main { public static void main(String[] args){try(BackgroundMonitor monitor=new BackgroundMonitor()){monitor.start();new MenuRouter(new ConsoleUI()).start();}catch(Exception e){System.out.println("Application stopped: "+e.getMessage());}} }
