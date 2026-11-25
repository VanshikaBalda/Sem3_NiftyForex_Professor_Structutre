package forex.service;
import forex.dao.UserDAO;
import forex.dao.WalletDAO;
import forex.util.PasswordUtil;
public class RegistrationService {
 private final UserDAO users=new UserDAO(); private final WalletDAO wallets=new WalletDAO();
 public void register(String name,String email,String password){if(name.isBlank()||email.isBlank()||password.length()<6)throw new IllegalArgumentException("Enter name/email and a password of at least 6 characters.");if(!email.contains("@")||!email.contains("."))throw new IllegalArgumentException("Enter a valid email address.");if(users.findByEmail(email)!=null)throw new IllegalArgumentException("An account with this email already exists.");int id=users.create(name,email,PasswordUtil.hash(password),"USER");wallets.create(id);}
}
