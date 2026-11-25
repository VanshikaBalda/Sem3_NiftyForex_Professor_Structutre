package forex.service;
import forex.dao.WatchlistDAO;
import java.util.*;
public class WatchlistService {private final WatchlistDAO dao=new WatchlistDAO();public void add(int uid,String c){dao.add(uid,c);}public List<String> list(int uid){return dao.list(uid);}}
