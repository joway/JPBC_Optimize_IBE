package encrypt.model;

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
public class PlainText implements CommonText {
    private ArrayList<byte[]> message;

    public PlainText(byte[] message) {
        this.message = Utils.slice(message);
    }

    public PlainText(ArrayList<byte[]> arrayList) {
        this.message = arrayList;
    }

    public PlainText(String stringMessage) {
        this.message = Utils.slice(stringMessage.getBytes());
    }

    public ArrayList<byte[]> getMessage() {
        return message;
    }

    public byte[] toBytes() {
        return Utils.splice(message);
    }

    public void setMessage(ArrayList<byte[]> message) {
        this.message = message;
    }


    @Override
    public String toString() {
        return Utils.toString(message);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(message);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        message = (ArrayList<byte[]>) in.readObject();
    }
}
