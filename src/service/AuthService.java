package forex.service;
import forex.dao.UserDAO;
public class AuthService {
 public record Session(int id,String name,String email,String role){}
 private final RegistrationService registration=new RegistrationService(); private final LoginService login=new LoginService();
 public void register(String name,String email,String password){registration.register(name,email,password);}
 public Session login(String email,String password){UserDAO.UserRecord u=login.login(email,password);return u==null?null:new Session(u.id(),u.name(),u.email(),u.role());}
}
