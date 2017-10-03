package core;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;

public class Security {

    private static final Random RANDOM = new SecureRandom();

    public Password generatePassword(String password) {
        try {
            byte[] salt = nextSalt();

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
        return generatePassword(userPassword).hash.equals(dbPassword.hash);
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
