package encrypt.core;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * @Author joway
 * @Email joway.w@gmail.com
 * @Date 16/3/31.
 */
public class FileUtils {

    /**
     * @param filename
     * @return
     * @throws IOException
     */
    public static byte[] fileToByteArray(String filename) throws IOException {
        Path path = Paths.get(filename);
        return Files.readAllBytes(path);
    }


    public static Path byteArrayToFile(String filename, byte[] bytes) throws IOException {
        Path path = Paths.get(filename);
        return Files.write(path, bytes);
    }


    public static void writeObject(Object object, String filename) throws IOException {
        FileOutputStream fileOut =
                new FileOutputStream(filename);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(object);
        out.close();
        fileOut.close();
    }

    public static Object readObject(String filename) throws IOException, ClassNotFoundException {


        FileInputStream fileIn = new FileInputStream(filename);
        ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(fileIn));

        // 性能的瓶颈在 读写文件的readObject这里
        Utils.logBegTime();
        Object object = in.readObject();
        Utils.logEndTime("readObject");

        in.close();
        fileIn.close();

        Utils.logEndTime("readObject");
        return object;
    }
}