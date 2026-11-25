package forex.util;
import java.nio.charset.StandardCharsets; import java.security.*; import java.util.HexFormat;
public class PasswordUtil {
 public static String hash(String password){try{byte[] b=MessageDigest.getInstance("SHA-256").digest(password.getBytes(StandardCharsets.UTF_8));return HexFormat.of().formatHex(b);}catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
 public static boolean verify(String password,String hash){return MessageDigest.isEqual(hash(password).getBytes(StandardCharsets.UTF_8),hash.getBytes(StandardCharsets.UTF_8));}
}
