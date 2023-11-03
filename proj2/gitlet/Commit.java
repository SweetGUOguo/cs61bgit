package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.*;
import  gitlet.MyUtils.*;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;

import java.util.Date;
// TODO: You'll likely use this in this class

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Commit implements Serializable, Dumpable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    private String message;

    /* TODO: fill in the rest of this class. */
    private String timestamp;
    //    Something that keeps track of what files
//    this commit is tracking.
    private List<String> parentCommit;
    private Map<String, String> trackTree = new TreeMap<>();
    private String tree = null;
    private transient String commitID;

    public Commit(String message, List<String> parent) {
        this.message = message;
        this.parentCommit = parent;
        this.timestamp = timestamp();
    }

    public static Commit fromFile(String SHA) {
        return readObject(getObjectfileById(SHA), Commit.class);
    }

    public String getMessage() {
        return this.message;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public List<String> getParent() {
        return this.parentCommit;
    }

    public Map<String, String> getTrackTree() {
        return this.trackTree;
    }

    public String toByteArray() {
        String commitstring = message + timestamp + parentCommit;
        return commitstring;
    }

    /*return timestamp in second format*/
    private String timestamp() {
        Date date = new Date();
        Formatter formatter = new Formatter();
        String timestamp = formatter.format("%ts", date).toString();
        formatter.close();
        return timestamp;
    }

    public void setCommitID() {
        commitID = sha1(toByteArray());
    }

    public void saveCommit() {
        setCommitID();
        saveObjectFile(commitID, this);
    }

    @Override
    public void dump() {
    }

}
