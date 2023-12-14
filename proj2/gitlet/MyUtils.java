package gitlet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import java.io.File;
import java.util.Collections;



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
     * get all the files in a dir
     * NO the dir in dir
     */
    public static String[] traverseFiles(File directory) {
        List<String> fileNames = new ArrayList<>();
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileNames.add(file.getName());
                    }
                }
            }
        }
        fileNames.remove("index");
        fileNames.remove("HEAD");
        Collections.sort(fileNames);
        return fileNames.toArray(new String[0]);
    }


    /**
     * Check if the file contain the Commit.class
     */
//    public static boolean ifObjectisCommit(File file) {
//        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
//            Object obj = in.readObject();
//            return obj instanceof Commit;
//        } catch (IOException | ClassNotFoundException excp) {
//            return false;
//        }
//    }

    public static boolean ifObjectisCommit(File file) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            Object obj = in.readObject();
            in.close();
            if (obj instanceof Commit) {
                return true;
            }
            return false;
        } catch (IOException | ClassNotFoundException excp) {
            return false;
        }
    }

    public static boolean isTxtFile(File file) {
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

        return fileExtension.equalsIgnoreCase("txt");
    }
}
