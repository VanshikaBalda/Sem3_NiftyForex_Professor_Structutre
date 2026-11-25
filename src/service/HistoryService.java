package forex.service;
import forex.dao.TransactionDAO;
import java.util.*;
public class HistoryService {private final TransactionDAO dao=new TransactionDAO();public List<String> recent(int uid){return dao.recent(uid);}}
