package forex.dao;
import forex.util.DatabaseConfig;
import java.sql.*;
public class DatabaseConnection {
 private static final DatabaseConfig CONFIG=new DatabaseConfig();
 private DatabaseConnection(){}
 public static Connection getConnection() throws SQLException {
  try{Class.forName("com.mysql.cj.jdbc.Driver");}catch(ClassNotFoundException e){throw new SQLException("MySQL Connector/J is missing. Put mysql-connector-j.jar in lib/ and include it in the classpath.",e);}
  return DriverManager.getConnection(CONFIG.url(),CONFIG.user(),CONFIG.password());
 }
}
