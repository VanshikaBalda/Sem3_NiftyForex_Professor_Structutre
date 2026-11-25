package forex.dao;
import forex.exception.DatabaseException;
import java.sql.*; import java.math.BigDecimal; import java.util.*;
public class PortfolioDAO {
 public void add(int uid,String cur,BigDecimal qty){String s="INSERT INTO portfolio(user_id,currency,quantity) VALUES(?,?,?) ON DUPLICATE KEY UPDATE quantity=quantity+VALUES(quantity)";try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement(s)){p.setInt(1,uid);p.setString(2,cur.toUpperCase());p.setBigDecimal(3,qty);p.executeUpdate();}catch(SQLException e){throw new DatabaseException("Portfolio update failed: "+e.getMessage(),e);}}
 public List<String> list(int uid){List<String>a=new ArrayList<>();try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement("SELECT currency,quantity FROM portfolio WHERE user_id=? ORDER BY currency")){p.setInt(1,uid);try(ResultSet r=p.executeQuery()){while(r.next())a.add(r.getString(1)+": "+r.getBigDecimal(2));}}catch(SQLException e){throw new DatabaseException("Portfolio load failed: "+e.getMessage(),e);}return a;}
}
