package forex.dao;
import forex.exception.DatabaseException;
import java.sql.*; import java.math.BigDecimal; import java.util.*;
public class AlertDAO {
 public void add(int uid,String cur,BigDecimal target,String direction){try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement("INSERT INTO price_alerts(user_id,currency,target_rate,direction) VALUES(?,?,?,?)")){p.setInt(1,uid);p.setString(2,cur.toUpperCase());p.setBigDecimal(3,target);p.setString(4,direction.toUpperCase());p.executeUpdate();}catch(SQLException e){throw new DatabaseException("Alert creation failed: "+e.getMessage(),e);}}
 public List<String> list(int uid){List<String>a=new ArrayList<>();try(Connection c=DatabaseConnection.getConnection();PreparedStatement p=c.prepareStatement("SELECT currency,direction,target_rate,active FROM price_alerts WHERE user_id=? ORDER BY id DESC")){p.setInt(1,uid);try(ResultSet r=p.executeQuery()){while(r.next())a.add(r.getString(1)+" "+r.getString(2)+" "+r.getBigDecimal(3)+" (active="+r.getBoolean(4)+")");}}catch(SQLException e){throw new DatabaseException("Alert load failed: "+e.getMessage(),e);}return a;}
}
