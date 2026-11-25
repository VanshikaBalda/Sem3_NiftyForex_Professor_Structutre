package forex.service;
import java.util.*;
public class ReportService {private final HistoryService history=new HistoryService();private final PortfolioService portfolio=new PortfolioService();public void print(int uid){System.out.println("\n--- ACCOUNT REPORT ---");System.out.println("Portfolio: "+portfolio.list(uid));System.out.println("Recent transactions:");history.recent(uid).forEach(System.out::println);}}
