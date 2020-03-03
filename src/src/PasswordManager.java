import javax.xml.bind.DatatypeConverter;
import java.security.*;

public class PasswordManager {

    public static String generatePasswdHash(String passwd, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(DatatypeConverter.parseBase64Binary(salt));
        byte[] bytes = md.digest(passwd.getBytes());
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        String hashedPasswd = sb.toString();
        return hashedPasswd;
    }


    public static String getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return DatatypeConverter.printBase64Binary(salt);
    }
}
