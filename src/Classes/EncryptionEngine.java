package Classes;

import java.security.SecureRandom;

public class EncryptionEngine {

    SecureRandom randomNumber;
    String encryptionMethodName;

    public String getEncryptionMethodName() {
        return encryptionMethodName;
    }

    public void setEncryptionMethodName(String encryptionMethodName) {
        this.encryptionMethodName = encryptionMethodName;
    }

}
