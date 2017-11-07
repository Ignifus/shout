package core;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;

@SessionScoped
public class Security implements Serializable {

    private static final Random RANDOM = new SecureRandom();

    public Password generatePassword(String password, byte[] dbSalt) {
        try {
            byte[] salt;

            if(dbSalt != null)
                salt = dbSalt;
            else
                salt = nextSalt();

            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = f.generateSecret(spec).getEncoded();

            return new Password(Base64.getEncoder().encodeToString(hash), Base64.getEncoder().encodeToString(salt));
        }
        catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed when creating password: " + e.getMessage());
        }
    }

    public boolean verifyPassword(String userPassword, Password dbPassword) {
        return generatePassword(userPassword, Base64.getDecoder().decode(dbPassword.salt)).hash.equals(dbPassword.hash);
    }

    private byte[] nextSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

    public static class Password {
        public String hash;
        public String salt;

        public Password(String hash, String salt) {
            this.hash = hash;
            this.salt = salt;
        }
    }
}
