package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;
import static gitlet.Repository.*;

import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * Represents a gitlet commit object.
 * does at a high level.
 *
 * @author gg
 */
public class Commit implements Serializable, Dumpable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    private String message;
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

    public Commit(String sha, String message) {
        Commit tmp = readObject(getObjectfileById(sha), Commit.class);
        this.trackTree.putAll(tmp.trackTree);
        this.message = message;
        this.timestamp = timestamp();
        this.parentCommit = sha;
    }

    public static Commit readCommit(String sha) {
        if (ifObjectisCommit(getObjectfileById(sha))) {
            return readObject(getObjectfileById(sha), Commit.class);
        }
        return null;
    }

    public static void printLog(String commitID) {
        Commit logCommit = readCommit(commitID);
        System.out.println("===");
        System.out.println("commit " + logCommit.commitID);
        System.out.println("Date: " + logCommit.timestamp);
        System.out.println(logCommit.message + '\n');
        if (logCommit.parentCommit != null) {
            printLog(logCommit.parentCommit);
        }
    }

    public static void printGloballog() {
//        StringBuilder commitlog = new StringBuilder();
        List<String> filenames = traverseFolder(OBJECTS_DIR);
        for (String filename : filenames) {
            File file = new File(filename);
            if (MyUtils.ifObjectisCommit(file)) {
                Commit ifCommit = readObject(file, Commit.class);
                if (ifCommit.commitID != null) {
//                    Commit logCommit = readCommit(ifCommit.commitID);
                    System.out.println("===");
                    System.out.println("commit " + ifCommit.commitID);
                    System.out.println("Date: " + ifCommit.timestamp);
                    System.out.println(ifCommit.message + '\n');
                }
            }
        }
    }

    public static StringBuilder findCommitbyMessage(String message) {
        StringBuilder commitIds = new StringBuilder();
        int num = 0;
        List<String> filenames = traverseFolder(OBJECTS_DIR);
        for (String filename : filenames) {
            File file = new File(filename);
            if (MyUtils.ifObjectisCommit(file)) {
                Commit ifCommit = readObject(file, Commit.class);
                if (ifCommit.commitID != null) {
                    if (ifCommit.getMessage().equals(message)) {
                        num = num + 1;
                        commitIds.append(ifCommit.commitID);
                        commitIds.append('\n');
                    }
                }
            }
        }
        if (num == 0) {
            commitIds = null;
        }
        return commitIds;
    }

    public static boolean checkAllTracked(String workingId) {
        List<String> workFiles = plainFilenamesIn(CWD);
        Commit workingCommit = readCommit(workingId);
        TreeMap<String, String> fileTrackTree = workingCommit.getTrackTree();

        for (String workFile : workFiles) {
            File file = join(CWD, workFile);
            if (isTxtFile(file)) {
                String filewithPath = file.getPath();
                if (!fileTrackTree.containsKey(filewithPath)) {
                    return false;
                }
            }
        }
        return true;
    }

    /*checkout(copy) all the files in the branchCommit
     * to the working DIR*/
    public static void checkoutAll(String checkoutId) {
        Commit checkoutCommit = readCommit(checkoutId);
        TreeMap<String, String> fileTrackTree = checkoutCommit.getTrackTree();
        for (String filename : fileTrackTree.keySet()) {
            File file = new File(filename);
            String blobSHA = fileTrackTree.get(filename);
            Blob checkoutBlob = Blob.readBlob(blobSHA);
            byte[] content = checkoutBlob.getContent();
            writeContents(file, content);
        }
    }

    /*delete all the files in working DIR
     * which are not in the branchCommit
     * but are in the currentCommit*/
    public static void deleteDiftxt(String workingId, String checkoutId) {
        Commit workingCommit = readCommit(workingId);
        Commit checkoutCommit = readCommit(checkoutId);
        TreeMap<String, String> workingTrackTree = workingCommit.getTrackTree();
        TreeMap<String, String> checkoutTrackTree = checkoutCommit.getTrackTree();
        for (String filename : workingTrackTree.keySet()) {
            if (!checkoutTrackTree.containsKey(filename)) {
                deletetxtFile(filename);
            }
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
    public void addToTrack(TreeMap<String, String> indexAdd) {
        trackTree.putAll(indexAdd);
    }

    public void rmToTrack(Set<String> indexRm) {
        for (String rm : indexRm) {
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
        String mytimestamp = formatter.format(Locale.ENGLISH,
                "%1$ta %1$tb %1$td %1$tT %1$tY %1$tz", date).toString();
        formatter.close();
        return mytimestamp;
    }

    public void setCommitID() {
        commitID = sha1(toByteArray());
    }

    public void setTree(TreeMap<String, String> nowTree) {
        trackTree.putAll(nowTree);
    }

    public void saveCommit(File nowbranch) {
        setCommitID();
        saveObjectFile(commitID, this);
        saveHEAD(nowbranch);
    }

    @Override
    public void dump() {
    }

    public void saveHEAD(File nowbranch) {
        saveBranch(nowbranch);
        writeContents(HEAD, nowbranch.getName());
    }

    public void saveBranch(File nowbranch) {
        writeContents(nowbranch, commitID);
    }

}
