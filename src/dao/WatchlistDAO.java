package forex.dao;
import forex.exception.DatabaseException;
import java.sql.*; import java.util.*;
public class WatchlistDAO {
 public void add(int uid,String cur){try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement("INSERT IGNORE INTO watchlist(user_id,currency) VALUES(?,?)")){p.setInt(1,uid);p.setString(2,cur.toUpperCase());p.executeUpdate();}catch(SQLException e){throw new DatabaseException("Watchlist update failed: "+e.getMessage(),e);}}
 public List<String> list(int uid){List<String>a=new ArrayList<>();try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement("SELECT currency FROM watchlist WHERE user_id=? ORDER BY currency")){p.setInt(1,uid);try(ResultSet r=p.executeQuery()){while(r.next())a.add(r.getString(1));}}catch(SQLException e){throw new DatabaseException("Watchlist load failed: "+e.getMessage(),e);}return a;}
}
