package gitlet;

import java.io.File;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File Objects_DIR = join(GITLET_DIR,"objects");
    public static final File Refs_DIR = join(GITLET_DIR,"refs");
    public static final File HEAD_DIR = join(GITLET_DIR,"HEAD");
    public static final File Staging_DIR = join(GITLET_DIR,"staging");
    public static final File RemovedStage_DIR = join(GITLET_DIR,"removedStage");
    /*Make a file named index*/
    public static final File index = join(GITLET_DIR,"index");
    /* TODO: fill in the rest of this class. */
    /* init */
    public static void initRepository(){
        /*Make Big Dir*/
        GITLET_DIR.mkdir();
        Objects_DIR.mkdir();
        Refs_DIR.mkdir();
        HEAD_DIR.mkdir();
        Staging_DIR.mkdir();
        RemovedStage_DIR.mkdir();
        /*Set init commit object*/
        Commit initcommit = new Commit("initial commit",null);

        initcommit.saveCommit();
    }

    /*produce a tree*/
    public static void buildTree(File filename, Blob addblob){
        String nameString = filename.getPath();
        Tree newtree = new Tree(nameString, addblob);
        newtree.saveTree();
    }

}
