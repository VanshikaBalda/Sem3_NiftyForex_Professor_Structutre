package forex.service;
import forex.dao.TransactionDAO;
import java.math.*;
public class TransactionService {
 private final TransactionDAO dao=new TransactionDAO();
 public void recordTrade(int uid,String side,String currency,BigDecimal qty){dao.add(uid,side,"USD",currency,qty,qty,"Simulated "+side.toLowerCase()+" order");}
}
