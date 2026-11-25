package forex.service;
import forex.dao.PortfolioDAO;
import java.util.*;
public class PortfolioService {private final PortfolioDAO dao=new PortfolioDAO();public List<String> list(int uid){return dao.list(uid);}}
