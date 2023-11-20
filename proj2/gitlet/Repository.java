package gitlet;

import java.io.File;
import java.util.jar.JarEntry;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

// TODO: any imports you need here

/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File Objects_DIR = join(GITLET_DIR, "objects");
    //    public static final File Blobs_DIR = join(Objects_DIR, "blobs");
//    public static final File Commits_DIR = join(Objects_DIR, "commits");
//    public static final File Refs_DIR = join(GITLET_DIR, "refs");
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    /*Make a file named index*/
    public static final File index = join(GITLET_DIR, "index");
    /* TODO: fill in the rest of this class. */
    /* init */
    private final Lazy<File> nowbranch = lazy(() -> {
        File branch = HEAD.exists()
                ? readBranchFromHEAD()
                : join(GITLET_DIR, "master");
        return branch;
    });
    private final Lazy<staging> stagingArea = lazy(() -> {
        /*check if index exist*/
        staging s = index.exists()
                ? staging.fromFile()
                : new staging();
        s.setTrackedTree();
        return s;
    });

    public static boolean checkGit() {
        if (GITLET_DIR.exists()) {
            return true;
        }
        System.out.println("Not init yet.");
        return false;
    }

    public static void globalLog() {
        Commit.printGloballog();
    }

    public static void find(String message) {
        StringBuilder CommitIds = Commit.findCommitbyMessage(message);
        if (CommitIds != null) {
            System.out.print(CommitIds);
        } else {
            System.out.println("Found no commit with that message.");
        }

    }

    public static void deleteFile(String filename) {
        File file = new File(filename);
        restrictedDelete(file);
    }

    private File readBranchFromHEAD() {
        String branchName = readContentsAsString(HEAD);
        File branch = join(GITLET_DIR, branchName);
        return branch;
    }

    public File getNowbranch() {
        return nowbranch.get();
    }

    public void initRepository() {
        if (!GITLET_DIR.exists()) {
            /*Make Big Dir*/
            GITLET_DIR.mkdir();
            Objects_DIR.mkdir();
//            Blobs_DIR.mkdir();
//            Commits_DIR.mkdir();
//            Refs_DIR.mkdir();
//        HEAD_DIR.mkdir();
            /*Set init commit object*/
            Commit initcommit = new Commit("initial commit");
            initcommit.saveCommit(nowbranch.get());
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
    }

    public void checkout(File file) {
        String fileName = file.getPath();
        String lastCommitSha = readContentsAsString(nowbranch.get());
        Commit checkoutCommit = Commit.readCommit(lastCommitSha);
        String blobSHA = checkoutCommit.getTrackTree().get(fileName);
//        Blob checkoutBlob = new Blob(blobSHA);
//        Blob checkoutBlob = readObject(getObjectfileById(blobSHA), Blob.class);
        if (blobSHA != null) {
            Blob checkoutBlob = Blob.readBlob(blobSHA);
            byte[] content = checkoutBlob.getContent();
            writeContents(file, content);
        } else {
            System.out.println("File does not exist in that commit.");
        }
    }

    public void checkout(String SHA, File file) {
        String fileName = file.getPath();
        Commit checkoutCommit = Commit.readCommit(SHA);
        if (checkoutCommit != null) {
            String blobSHA = checkoutCommit.getTrackTree().get(fileName);
            if (blobSHA != null) {
                Blob checkoutBlob = Blob.readBlob(blobSHA);
                byte[] content = checkoutBlob.getContent();
                writeContents(file, content);
            } else {
                System.out.println("File does not exist in that commit.");
            }
        } else {
            System.out.println("No commit with that id exists.");
        }
    }

    public void checkoutBranch(String branchName) {
        File branch = join(GITLET_DIR, branchName);
        if (branch.exists()) {
            String HEADbranchname = readContentsAsString(HEAD);
            File HEADbranch = join(GITLET_DIR, HEADbranchname);
            String headBcommitId = readContentsAsString(HEADbranch);

            if (branchName.equals(HEADbranchname)) {
                System.out.println("No need to checkout the current branch.");
            } else {
                String checkoutId = readContentsAsString(branch);
                if (Commit.checkAllTracked(headBcommitId)) {
                    Commit.checkoutAll(checkoutId);
                    Commit.deleteDif(headBcommitId, checkoutId);
                    writeContents(HEAD, branchName);
                    stagingArea.get().clear();
                }else{
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                }
            }
        } else {
            System.out.println("No such branch exists.");
        }
    }

    public void reset(String commitId){
        String HEADbranchname = readContentsAsString(HEAD);
        File HEADbranch = join(GITLET_DIR, HEADbranchname);
        String headBcommitId = readContentsAsString(HEADbranch);

        String checkoutId = commitId;
        if (Commit.checkAllTracked(headBcommitId)) {
            Commit.checkoutAll(checkoutId);
            Commit.deleteDif(headBcommitId, checkoutId);
            writeContents(nowbranch.get(), commitId);
            stagingArea.get().clear();
        }else{
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
        }
    }

    public void newBranch(String branchName) {
        File branch = join(GITLET_DIR, branchName);
        /*cp the file which HEAD points to.*/
        String contents = readContentsAsString(nowbranch.get());
        writeContents(branch, contents);
        /*put nowbranch into HEAD*/
        writeContents(HEAD, branch.getName());
    }

    public void log() {
        String headCommitID = readContentsAsString(nowbranch.get());
        Commit.printLog(headCommitID);
    }

    public void add(File addFile) {
        if (addFile.exists()) {
            if (stagingArea.get().addstage(addFile)) {
                stagingArea.get().save();
            }
        } else {
            System.out.println("File does not exist.");
        }
    }

    public void commit(String message) {
        if (!stagingArea.get().isClear()) {
            String lastSha = readContentsAsString(nowbranch.get());
            Commit nowCommit = new Commit(lastSha, message);
            nowCommit.addToTrack(stagingArea.get().getAdd());
            nowCommit.rmToTrack(stagingArea.get().getRemove());
//        /* TODO: SET TREE */
            stagingArea.get().clear();
            stagingArea.get().save();
            nowCommit.saveCommit(nowbranch.get());
        } else {
//            System.out.println("No changes added to the commit.");
        }
    }

    public void rm(File rmFile) {
        if (rmFile.exists()) {
            if (stagingArea.get().rmstage(rmFile)) {
                stagingArea.get().save();
            }
        } else {
            System.out.println("File does not exist.");
        }
    }

}
