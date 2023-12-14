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

    public static Blob readBlob(String sha) {
        return readObject(getObjectfileById(sha), Blob.class);
    }

    public String getRefs() {
        return refs;
    }

    public byte[] getContent() {
        return content;
    }

    /*Produce a blob from a file.*/
    public void saveblob() {
        saveObjectFile(refs, this);
    }

}
