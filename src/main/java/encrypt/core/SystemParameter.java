package encrypt.core;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

/**
 * @Author joway
 * @Email joway.w@gmail.com
 * @Date 16/3/31.
 */
public class SystemParameter {



    // 系统公共参数:
    // 1. 素数阶q
    // 2. G1,G2, 其中G2 == G1
    private Field G1;

    // 3. 双线性映射关系 e ( 这里设成pairing
    /*
    pairing.pairing(x, y) 代表数学上 e（x,y）
    双线性映射
    */
    private static Pairing pairing;

    // 4. n : 二进制位数

    // 5. P : 群G1的生成元
    private Element P;// G1的生成元

    // 6. g2 : e(P,P) 值
    private Element g2;

    // 7. H : 杂凑函数(这里选择哈希函数 sh256)


    public static int SIZE = 32; // 256 bit

    public SystemParameter(Field g1, Pairing pairing, Element p, Element g2, Field zr) {
        G1 = g1;
        SystemParameter.pairing = pairing;
        P = p;
        this.g2 = g2;
        Zr = zr;

    }


    public Field getZr() {
        return Zr;
    }

    public void setZr(Field zr) {
        Zr = zr;
    }

    private Field Zr; // // {1,...,r} 整数集


    public SystemParameter(Field g1, Pairing pairing, Element p, Element g2) {
        G1 = g1;
        this.pairing = pairing;
        P = p;
        this.g2 = g2;
    }

    public SystemParameter() {
    }

    public Field getG1() {
        return G1;
    }

    public static Pairing getPairing() {
        return pairing;
    }


    public Element getP() {
        return P;
    }

    public void setP(Element p) {
        P = p;
    }

    public Element get_g2() {
        return g2;
    }

    public void set_g2(Element g2) {
        this.g2 = g2;
    }



}
