package encrypt.core;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import encrypt.model.CipherText;
import encrypt.model.PlainText;
import encrypt.model.UserKey;

import java.io.IOException;
import java.util.ArrayList;


/**
 * @Author joway
 * @Email joway.w@gmail.com
 * @Date 16/3/5.
 */
public class BaseIdentSystem implements Ident {

    private SystemParameter systemParameter;


    public BaseIdentSystem() {
        setUp();
    }

    /**
     * 判断配对是否为对称配对，不对称则输出错误信息
     *
     * @param pairing
     */
    private void checkSymmetric(Pairing pairing) {
        if (!pairing.isSymmetric()) {
            throw new RuntimeException("密钥不对称!");
        }
    }


    /*只需要启动一次
    系统初始化(Setup): 给定一个安全参数k,执行下面的步骤。
    1) 输出 2 个阶为素数 q 的循环群 G1 与 G2 、群G1的生成元P,以及双线性映射e:G1 X G1 ->G2
    2)计算g2 = e(P,P)。
    3)选择杂凑函数H:G2 -> {0,1}^n,其中n是整数。
    此方案的明文空间是 M = {0,1}^n ,密文空间是 C = G^* X {0,1}^n。
    系统公共参数 params 为 (q,G1,G2,e,n,P,g2,H)。
     */
    @Override
    public void setUp() {
        Utils.log("-------------------系统建立阶段----------------------");
        //双线性群的初始化
        Pairing pairing = PairingFactory.getPairing("jpbc.properties");
        PairingFactory.getInstance().setUsePBCWhenPossible(true);
        checkSymmetric(pairing);

        Field G1 = pairing.getG1(); //G1 == G2 对称

        Element P = G1.newRandomElement().getImmutable();// 生成G1的生成元P

        Element g2 = pairing.pairing(P, P).getImmutable();

        systemParameter = new SystemParameter(
                G1, pairing, P, g2, pairing.getZr()
        );
    }


    /*用户的(双)私钥生成算法:每个用户生成自己的公钥及其对应的 2 个 解密私钥。
    1. 随机选择一个随机数 x (- Zq ,并将其 设置为主解密私钥,即Kp = x。
    2. 将托管解密私钥设为Ke = x^-1 P
    3. 将公钥设为Ppub = xP (- G1 。
     */
    @Override
    public UserKey privateKeyGen() {
        Utils.log("-------------------密钥生成阶段----------------------");
        Element x = getRandInZr(); // 用户自己主私钥,用户自己设置

        Element Kp = x.getImmutable();
        Element Ke = systemParameter.getP().mulZn(x.invert()).getImmutable();
        Element Ppub = systemParameter.getP().mulZn(x).getImmutable();

        return new UserKey(Kp, Ke, Ppub);
    }

    /*
    拿Ppub对V加密
    密文 C = (U, V)
    1. 首先选择 r (- Zq
    2.
     */
    @Override
    public CipherText encrypt(PlainText plainText, UserKey userKey) {
        Utils.log("-------------------加密阶段----------------------");
        Element r = getRandInZr();
        Element U = userKey.getPpub().mulZn(r).getImmutable();//U

        Element g2_r = systemParameter.get_g2().powZn(r);
        byte[] sha256_g2_r = Utils.sha256(g2_r.toBytes());

        ArrayList<byte[]> cipherV = new ArrayList<>();
        int size = plainText.getMessage().size();
        ArrayList<byte[]> bytesBox = plainText.getMessage();

        for (int i = 0; i < size; ++i) {
            cipherV.add(Utils.xor(bytesBox.get(i), sha256_g2_r));
        }

        Utils.log("明文", plainText);
        Utils.log("密文", new CipherText(U, cipherV));
        return new CipherText(U, cipherV);
    }

    @Override
    public CipherText encrypt(String filename, UserKey userKey) throws IOException {
        return encrypt(new PlainText(FileUtils.fileToByteArray(filename)), userKey);
    }


    /*
    拿Ke对V解密
     */
    @Override
    public PlainText userDecrypt(CipherText cipherText, UserKey userKey) {
        Utils.log("-------------------主私钥解密阶段----------------------");

        Utils.logBegTime();
        Element eUKp_1_P = pairing(
                cipherText.getU().getImmutable(),
                systemParameter.getP().mulZn(userKey.getKp().invert())).getImmutable();
        Utils.logEndTime("配对结束");

        ArrayList<byte[]> plainBytes = new ArrayList<>();
        ArrayList<byte[]> cipherV = cipherText.getV();

        Utils.logBegTime();
        byte[] sha256_eUKp_1_P = Utils.sha256(eUKp_1_P.toBytes());
        Utils.logEndTime("哈希结束");

        Utils.logBegTime();
        for (byte[] aCipherV : cipherV) {
            plainBytes.add(Utils.xor(aCipherV,sha256_eUKp_1_P));
        }
        Utils.logEndTime("解密异或循环结束");


        Utils.log("主私钥解密后明文", new PlainText(plainBytes));
        return new PlainText(plainBytes);
    }

    @Override
    public PlainText userDecrypt(String filename, UserKey userKey)
            throws ClassNotFoundException, IOException {
        Utils.logBegTime();
        CipherText cipherText = (CipherText) FileUtils.readObject(filename);
        Utils.logEndTime("读取密文文件中的信息至内存");
        return userDecrypt(cipherText, userKey);
    }

    @Override
    public PlainText escrowDecrypt(CipherText cipherText, UserKey userKey) {
        Utils.log("-------------------托管私钥解密阶段----------------------");

        Element eUKe = pairing(
                cipherText.getU().getImmutable(),
                userKey.getKe().getImmutable()).getImmutable();

        byte[] sha256_eUKe = Utils.sha256(eUKe.toBytes());

        ArrayList<byte[]> plainBytes = new ArrayList<>();
        ArrayList<byte[]> cipherV = cipherText.getV();

        for (byte[] aCipherV : cipherV) {
            plainBytes.add(Utils.xor(aCipherV, sha256_eUKe));
        }

        Utils.log("托管解密后明文", new PlainText(plainBytes));
        return new PlainText(plainBytes);
    }

    // BLS sing:




    @Override
    public PlainText escrowDecrypt(String filename, UserKey userKey) throws IOException, ClassNotFoundException {
        CipherText cipherText = (CipherText) FileUtils.readObject(filename);
        return escrowDecrypt(cipherText, userKey);
    }

    private Element getRandInZr() {
        return systemParameter.getZr().newRandomElement().getImmutable();
    }

    private Element pairing(Element var1, Element var2) {
        return SystemParameter.getPairing().pairing(var1, var2).getImmutable();
    }

}