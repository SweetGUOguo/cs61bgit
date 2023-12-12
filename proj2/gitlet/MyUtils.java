package gitlet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class MyUtils {
    public static <T> Lazy<T> lazy(Supplier<T> delegate) {
        return new Lazy<>(delegate);
    }

    public static File getObjectfileById(String id) {
        File objectFile = join(OBJECTS_DIR, makeSha2DIR(id), makeShaFile(id));
        return objectFile;
    }

    public static void saveObjectFile(String id, Serializable classobject) {
        File outDIR = join(OBJECTS_DIR, makeSha2DIR(id));
        outDIR.mkdir();
        File outFile = join(outDIR, makeShaFile(id));
        writeObject(outFile, classobject);
    }

    private static String makeSha2DIR(String sha) {
        String shaDirname = sha.substring(0, 2);
        return shaDirname;
    }

    private static String makeShaFile(String sha) {
        String shaFilename = sha.substring(2);
        return shaFilename;
    }

    /**
     * get all the files in a dir
     * all the dir in dir
     */
    public static List<String> traverseFolder(File folder) {
        List<String> fileList = new ArrayList<>();

        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        fileList.addAll(traverseFolder(file));
                    } else {
                        fileList.add(file.getPath());
                    }
                }
            }
        }
        return fileList;
    }

    /**
     * Check if the file contain the Commit.class
     */
    public static boolean ifObjectisCommit(File file) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = in.readObject();
            return obj instanceof Commit;
        } catch (IOException | ClassNotFoundException excp) {
            return false;
        }
    }
}
