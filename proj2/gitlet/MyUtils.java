package gitlet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class MyUtils {
    //    public static <T extends Serializable> void getObjectById(String id, Class<T> classobject){
//        File aimDIR = join(Objects_DIR, makeSha2DIR(id));
//        aimDIR.mkdir();
//        File aimFile = join(aimDIR, makeShaFile(id));
//        readObject(aimFile,classobject);
//    }

    public static <T> Lazy<T> lazy(Supplier<T> delegate){
        return new Lazy<>(delegate);
    }
    public static File getObjectfileById(String id) {
        File Objectfile = join(Objects_DIR, makeSha2DIR(id), makeShaFile(id));
        return Objectfile;
    }

    public static void saveObjectFile(String id, Serializable classobject) {
        File outDIR = join(Objects_DIR, makeSha2DIR(id));
        outDIR.mkdir();
        File outFile = join(outDIR, makeShaFile(id));
        writeObject(outFile, classobject);
    }

    private static String makeSha2DIR(String SHA) {
        String shaDirname = SHA.substring(0, 2);
        return shaDirname;
    }

    private static String makeShaFile(String SHA) {
        String shaFilename = SHA.substring(2);
        return shaFilename;
    }

    /**
     * get all the files in a dir
     * all the dir in dir*/
    public static List<String> traverseFolder(File folder){
        List<String> fileList = new ArrayList<>();

        if(folder.isDirectory()){
            File[] files = folder.listFiles();
            if(files!=null){
                for(File file:files){
                    if(file.isDirectory()){
                        fileList.addAll(traverseFolder(file));
                    }else{
                        fileList.add(file.getPath());
                    }
                }
            }
        }
        return fileList;
    }
    /**
     * Check if the file contain the Commit.class
     * */
    public static boolean ifObjectisCommit(File file){
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            Object obj = in.readObject();
            in.close();

            if (obj instanceof Commit) {
                return true;
            } else if (obj instanceof Blob) {
                return false;
            } else {
                return false;
            }
        } catch (IOException | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }
}