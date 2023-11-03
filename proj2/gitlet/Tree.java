package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;
import static gitlet.Repository.*;
import java.util.TreeMap;
public class Tree implements Serializable{
    /*TreeMap<filename, blobSHA1>*/
    private transient TreeMap<String, String> parentTree;

    /*TreeMap<filename, blobSHA1>*/
    private TreeMap<String, String> treenodes;

    public Tree(String filename, Blob addblob){
        treenodes.put(filename,addblob.getRefs());
    }

    /*Firstly, copy Tree from parenttree
    * in order to have all the blobs used to have*/
    public Tree(Tree parenttree){
        parentTree = parenttree.parentTree;
        treenodes = parenttree.treenodes;
    }

    public void addTree(String filename, Blob addblob){
        treenodes.put(filename,addblob.getRefs());
    }

    /*store the tree which have add new file to the staging area*/
    public void saveTree(){
        writeObject(Repository.index, this);
    }
    /*set the last tracked files by commit*/
    public void setParentTree(TreeMap<String, String> commitTrack){
        treenodes = commitTrack;
    }
}
