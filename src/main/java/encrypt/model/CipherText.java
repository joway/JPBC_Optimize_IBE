package encrypt.model;

import it.unisa.dia.gas.jpbc.Element;
import encrypt.core.SystemParameter;
import encrypt.core.Utils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

/**
 * @Author joway
 * @Email joway.w@gmail.com
 * @Date 16/3/31.
 */
public class CipherText implements CommonText {

    private Element U;

    private ArrayList<byte[]> V;



    public CipherText() {
        // 迫不得已的垃圾方法, 如果不初始化它的话, 后面的setFromByte方法就无法使用了
        U = SystemParameter.getPairing().getG1().newElement();
    }

    public CipherText(Element U, ArrayList<byte[]> v) {
        this.U = U;
        V = v;
    }

    public Element getU() {
        return U;
    }

    public void setU(Element u) {
        U = u;
    }


    public ArrayList<byte[]> getV() {
        return V;
    }

    public void setV(ArrayList<byte[]> v) {
        V = v;
    }


    @Override
    public String toString() {
        return "U: " + U.toString() + "\nV: " + Utils.toString(V);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(U.toBytes());
        out.writeObject(V);

    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        U.setFromBytes((byte[]) in.readObject());
        V = (ArrayList<byte[]>) in.readObject();
    }
}
