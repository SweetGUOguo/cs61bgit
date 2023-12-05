package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;
//import static gitlet.Repository.*;

import java.util.Set;
import java.util.HashSet;
import java.util.TreeMap;

public class staging implements Serializable {
    /*TreeMap<filename, blobSHA1>*/
    private final TreeMap<String, String> add = new TreeMap<>();
    private final Set<String> remove = new HashSet<>();
    /*TreeMap<filename, blobSHA1>*/
    private transient TreeMap<String, String> trackedTree;
    /*Set the tree by copy from the tree before.*/
//    public staging(){
//        trackedTree.putAll(treeFromCommit());
//    }

    /*Recover message to be add or delete of the tree from Staging*/
    public static staging fromFile() {
        return readObject(Repository.index, staging.class);
    }

    public TreeMap<String, String> getAdd() {
        return add;
    }

    public Set<String> getRemove() {
        return remove;
    }

    /**
     * "gitlet add": call here
     * Attention: Once a file
     *
     * @param file
     * @return true if index change
     * false if not
     */
    public boolean addstage(File file) {
        Blob addblob = new Blob(file);
        String blobId = addblob.getRefs();

        String filename = file.getPath();
        String trackId = trackedTree.get(filename);
        /*Check if trackTree has the file*/
        if (trackId != null) {
            /*Check if the 2 blobs equals*/
            if (trackId.equals(blobId)) {
//                System.out.println("We have tracked the same file");
//                add.remove(filename);
                return false;
            }
        }

        String oldAddId = add.put(filename, blobId);
        /*Check if addTree has the same blob*/

        if (oldAddId != null && oldAddId.equals(blobId)) {
//            System.out.println("No change has been made to the add file.");
            add.remove(filename);
            return false;
        }

        addblob.saveblob();
        return true;
    }

    /**
     * "gitlet rm": call here
     * Attention: Once a file
     *
     * @param file
     * @return true if index change
     * false if not
     */
    public boolean rmstage(File file) {
        String filename = file.getPath();
        String oldAddId = add.remove(filename);
        if (oldAddId != null) {
            return true;
        }

        String TrackId = trackedTree.get(filename);
        if (TrackId != null) {
            if (file.exists()) {
                file.delete();
            }
            remove.add(filename);
            return true;
        } else {
            System.out.println("No reason to remove the file.");
        }
        return false;
    }

    public void clear() {
        add.clear();
        remove.clear();
    }

    public boolean isClear() {
        return add.isEmpty() && remove.isEmpty();
    }

    /*Add a node by filename and its corresponding addblob*/
    public void addNode(String filename, Blob addblob) {
        trackedTree.put(filename, addblob.getRefs());
    }

    /*Delete a node by filename.*/
    public void deleteNode(String filename) {
        trackedTree.remove(filename);
    }

    /*store the tree which have add/delete new file to the staging area*/
    public void save() {
        writeObject(Repository.index, this);
    }

    private TreeMap<String, String> treeFromCommit() {
        String lastCommitSha = readContentsAsString(new Repository().getNowbranch());
        Commit lastCommit = Commit.readCommit(lastCommitSha);
        return lastCommit.getTrackTree();
    }

    public void setTrackedTree() {
        trackedTree = treeFromCommit();
    }

}
