package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.*;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;
import static gitlet.Repository.*;

import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
// TODO: You'll likely use this in this class

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author gg
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
    private String parentCommit;
    private TreeMap<String, String> trackTree = new TreeMap<>();
    private String commitID;

    public Commit(String message) {
        this.message = message;
        this.parentCommit = null;
        this.timestamp = timestamp();
    }

    public  Commit(String SHA,String message){
        Commit tmp = readObject(getObjectfileById(SHA), Commit.class);
        this.trackTree.putAll(tmp.trackTree);
        this.message = message;
        this.timestamp = timestamp();
        this.parentCommit = SHA;
    }

    public static Commit readCommit(String SHA){
        return readObject(getObjectfileById(SHA), Commit.class);
    }

    public static void printLog(String CommitID){
        Commit logCommit = readCommit(CommitID);
        System.out.println("===");
        System.out.println("commit "+logCommit.commitID);
        System.out.println("Date: "+logCommit.timestamp);
        System.out.println(logCommit.message+'\n');
        if(logCommit.parentCommit!=null){
            printLog(logCommit.parentCommit);
        }
    }

    public String getMessage() {
        return this.message;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public TreeMap<String, String> getTrackTree() {
        return this.trackTree;
    }

    public String getParent() {
        return this.parentCommit;
    }

    /*Add to Commit*/
    public void addToTrack(TreeMap<String, String> indexAdd){
        trackTree.putAll(indexAdd);
    }

    public void rmToTrack(Set<String> indexRm){
        for(String rm:indexRm){
            trackTree.remove(rm);
        }
    }

    public String toByteArray() {
        String commitstring = message + timestamp + parentCommit;
        return commitstring;
    }

    /*return timestamp in second format*/
    private String timestamp() {
        Date date = new Date();
        Formatter formatter = new Formatter();
        String timestamp = formatter.format(Locale.ENGLISH,"%1$ta %1$tb %1$td %1$tT %1$tY %1$tz", date).toString();
        formatter.close();
        return timestamp;
    }

    public void setCommitID() {
        commitID = sha1(toByteArray());
    }

    public void setTree(TreeMap<String,String> nowTree){
        trackTree.putAll(nowTree);
    }

    public void saveCommit() {
        setCommitID();
        saveObjectFile(commitID, this);
        saveHEAD();
    }

    @Override
    public void dump() {
    }

    public void saveHEAD(){
        writeContents(HEAD, commitID);
    }

}