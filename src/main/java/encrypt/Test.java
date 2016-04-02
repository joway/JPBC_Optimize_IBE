package encrypt;

import encrypt.core.BaseIdentSystem;
import encrypt.core.Ident;
import encrypt.model.CipherText;
import encrypt.model.PlainText;
import encrypt.model.UserKey;

import java.io.IOException;
import java.lang.reflect.Proxy;

/**
 * @Author joway
 * @Email joway.w@gmail.com
 * @Date 16/3/31.
 */
public class Test {

    public static PlainText plainText;
    public static CipherText cipherText;
    public static UserKey userKey;

    public static void init() {
        String temp = "";
        for (int i = 0; i < 100; ++i) {
            temp += "我爱北京天安门";
        }
        plainText = new PlainText(temp);
    }


    public static void test() throws IOException, ClassNotFoundException {

        BaseIdentSystem baseIdentSystem = new BaseIdentSystem();

        // 动态代理，统计各个方法耗时
        Ident identProxy = (Ident) Proxy.newProxyInstance(
                BaseIdentSystem.class.getClassLoader(),
                new Class[]{Ident.class}, new TimeCountProxyHandle(baseIdentSystem));

        String plainFile = "./23M.NEF";
        String encFile = "./23MENC.jpg";
        String plainNewFile = "./23MNEW.jpg";
        String plainNewFile2 = "./23MNEW2.jpg";


        userKey = identProxy.privateKeyGen();

//        CipherText temp = identProxy.encrypt(plainFile, userKey);
        init();
        CipherText temp = identProxy.encrypt(plainText, userKey);

//        Utils.logBegTime();
//        FileUtils.writeObject(temp, encFile);
//        Utils.logEndTime("写入密文");

//        Utils.logBegTime();
        identProxy.userDecrypt(temp,userKey);
        identProxy.escrowDecrypt(temp, userKey);

//        FileUtils.byteArrayToFile(
//                plainNewFile, identProxy.userDecrypt(encFile, userKey).toBytes());
//        Utils.logEndTime("解密密文并保存到文件中");

//        FileUtils.byteArrayToFile(
//                plainNewFile2, identProxy.escrowDecrypt(encFile, userKey).toBytes());
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        test();
    }

}
