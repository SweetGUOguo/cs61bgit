package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
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
}