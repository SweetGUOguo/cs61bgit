package gitlet;

import java.io.Serializable;
import java.io.File;

import static gitlet.Utils.*;
import static gitlet.Repository.*;
import static gitlet.MyUtils.*;

public class Blob implements Serializable {
    private String refs;
    private byte[] content;

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

    public byte[] getContent() {
        return content;
    }

    /*blob class get from file by sha1, then we can get the blob's content*/
//    public Blob(String SHA) {
//        readObject(getObjectfileById(SHA), Blob.class);
//    }

    public static Blob readBlob(String SHA){
        return readObject(getObjectfileById(SHA), Blob.class);
    }

    /*Produce a blob from a file.*/
    public void saveblob() {
        saveObjectFile(refs,this);
    }
}