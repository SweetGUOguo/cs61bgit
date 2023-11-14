package gitlet;

import java.io.File;
import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

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
    public static final File HEAD = join(GITLET_DIR,"HEAD");
    /*Make a file named index*/
    public static final File index = join(GITLET_DIR,"index");
    /* TODO: fill in the rest of this class. */
    /* init */


    private final Lazy<staging> stagingArea = lazy(() -> {
        /*check if index exist*/
        staging s = index.exists()
                ? staging.fromFile()
                : new staging();
        s.setTrackedTree();
        return s;
    });

    public static void initRepository(){
        if(!GITLET_DIR.exists()){
            /*Make Big Dir*/
            GITLET_DIR.mkdir();
            Objects_DIR.mkdir();
            Refs_DIR.mkdir();
//        HEAD_DIR.mkdir();
            /*Set init commit object*/
            Commit initcommit = new Commit("initial commit");
            initcommit.saveCommit();
        }else{
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
    }

    public static boolean checkGit(){
        if(GITLET_DIR.exists()){
            return true;
        }
        System.out.println("Not init yet.");
        return false;
    }

    public void add(File addFile){
        if(addFile.exists()){
            if(stagingArea.get().addstage(addFile)){
                stagingArea.get().save();
            }
        }else{
            System.out.println("File does not exist.");
        }
    }

    public void commit(String message){
        if(!stagingArea.get().isClear()){
            String lastSha = readContentsAsString(HEAD);
            Commit nowCommit = new Commit(lastSha, message);
            nowCommit.addToTrack(stagingArea.get().getAdd());
            nowCommit.rmToTrack(stagingArea.get().getRemove());
//        /* TODO: SET TREE */
            stagingArea.get().clear();
            stagingArea.get().save();
            nowCommit.saveCommit();
        }else {
            System.out.println("No changes added to the commit.");
        }
    }


    public static void checkout(File file){
        String fileName = file.getPath();
        String lastCommitSha = readContentsAsString(HEAD);
        Commit checkoutCommit = Commit.readCommit(lastCommitSha);
        String blobSHA = checkoutCommit.getTrackTree().get(fileName);
//        Blob checkoutBlob = new Blob(blobSHA);
//        Blob checkoutBlob = readObject(getObjectfileById(blobSHA), Blob.class);
        Blob checkoutBlob = Blob.readBlob(blobSHA);
        byte[] content = checkoutBlob.getContent();
        writeContents(file, content);
    }
    public static void checkout(String SHA, File file){
        String fileName = file.getPath();
        Commit checkoutCommit = Commit.readCommit(SHA);
        String blobSHA = checkoutCommit.getTrackTree().get(fileName);
        Blob checkoutBlob = Blob.readBlob(blobSHA);
        byte[] content = checkoutBlob.getContent();
        writeContents(file, content);
    }
    public static void log(){
        String headCommitID = readContentsAsString(HEAD);
        Commit.printLog(headCommitID);
    }

    public void rm(File rmFile){
        if(rmFile.exists()){
            if(stagingArea.get().rmstage(rmFile)){
                stagingArea.get().save();
            }
        }else{
            System.out.println("File does not exist.");
        }
    }

}
