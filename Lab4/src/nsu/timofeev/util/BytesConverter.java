package nsu.timofeev.util;

import java.io.*;

public class BytesConverter {
    public static byte[] getBytes(Object object) throws IOException {
        var bos = new ByteArrayOutputStream();

        try (var out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw e;
        }
    }

    public static Object getObject(byte[] bytes) throws IOException, ClassNotFoundException {
        var bin = new ByteArrayInputStream(bytes);
        try (var in = new ObjectInputStream(bin)) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw e;
        }
    }
}
