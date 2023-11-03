package gitlet;

import java.io.Serializable;
import java.io.File;

import static gitlet.Utils.*;
import static gitlet.Repository.*;
import static gitlet.MyUtils.*;

public class Blob implements Serializable {
    private static String refs;
    private static byte[] content;

    //   use a file to build a blob
    public Blob(File file) {
//        read content form file
        content = readContents(file);
//        get sha1
        refs = sha1(content);
    }
    public String getRefs(){
        return refs;
    }

    public static byte[] getContent() {
        return content;
    }

    /*produce a file from blob*/
    public static File fromBlob(String SHA, File aimfile) {
        String shaDirname = SHA.substring(0, 2);
        String Blobname = SHA.substring(2);
        File contentfile = join(Objects_DIR, shaDirname, Blobname);
        byte[] blobcontent = readContents(contentfile);

        /*form blob to file*/
        writeContents(aimfile, blobcontent);
        return aimfile;
    }

    /*Produce a blob from a file.*/
    public void saveblob() {
//        String shaDirname = refs.substring(0, 2);
//        /*Make small Dir*/
//        File blobDIR = join(Objects_DIR, shaDirname);
//        blobDIR.mkdir();
//        String Blobname = refs.substring(2);
//        File outFile = join(blobDIR, Blobname);
//        writeContents(outFile, content);

        saveObjectFile(refs,this);
    }
}
