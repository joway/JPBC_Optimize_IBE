package encrypt.core;

import encrypt.model.CipherText;
import encrypt.model.PlainText;
import encrypt.model.UserKey;

import java.io.IOException;

/**
 * @Author joway
 * @Email joway.w@gmail.com
 * @Date 16/3/31.
 */
public interface Ident {

    void setUp();

    UserKey privateKeyGen();

    CipherText encrypt(PlainText plainText, UserKey userKey);
    CipherText encrypt(String filename, UserKey userKey) throws IOException;

    PlainText userDecrypt(CipherText cipherText, UserKey userKey);

    PlainText escrowDecrypt(CipherText cipherText, UserKey userKey);

    PlainText userDecrypt(String filename, UserKey userKey) throws IOException, ClassNotFoundException;

    PlainText escrowDecrypt(String filename, UserKey userKey) throws IOException, ClassNotFoundException;
}
