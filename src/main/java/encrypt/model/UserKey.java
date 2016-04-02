package encrypt.model;

import it.unisa.dia.gas.jpbc.Element;

/**
 * @Author joway
 * @Email joway.w@gmail.com
 * @Date 16/3/31.
 */
public class UserKey {
    // 以下为用户私有信息
    private Element Kp; // 主解密私钥
    private Element Ke; // 托管私钥,交给服务器
    private Element Ppub; // 公钥


    public UserKey() {
    }

    public UserKey(Element kp, Element ke, Element ppub) {
        Kp = kp;
        Ke = ke;
        Ppub = ppub;
    }

    public Element getKp() {
        return Kp;
    }

    public void setKp(Element kp) {
        Kp = kp;
    }

    public Element getKe() {
        return Ke;
    }

    public void setKe(Element ke) {
        Ke = ke;
    }

    public Element getPpub() {
        return Ppub;
    }

    public void setPpub(Element ppub) {
        Ppub = ppub;
    }
}
