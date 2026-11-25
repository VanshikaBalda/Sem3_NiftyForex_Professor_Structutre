package forex.service;
import forex.dao.UserDAO;
import forex.util.PasswordUtil;
public class LoginService {
 private final UserDAO users=new UserDAO();
 public UserDAO.UserRecord login(String email,String password){UserDAO.UserRecord u=users.findByEmail(email);if(u==null||!PasswordUtil.verify(password,u.passwordHash()))return null;return u;}
}
