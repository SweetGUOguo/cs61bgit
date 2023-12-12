package gitlet;

import java.io.File;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;


/**
 * Represents a gitlet repository.
 * does at a high level.
 *
 * @author GG
 */
public class Repository {
    /**
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
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    //    public static final File Blobs_DIR = join(Objects_DIR, "blobs");
//    public static final File Commits_DIR = join(Objects_DIR, "commits");
//    public static final File Refs_DIR = join(GITLET_DIR, "refs");
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    /*Make a file named index*/
    public static final File INDEX = join(GITLET_DIR, "index");
    /* init */
    private final Lazy<File> nowbranch = lazy(() -> {
        File branch = HEAD.exists()
                ? readBranchFromHEAD()
                : join(GITLET_DIR, "master");
        return branch;
    });
    private final Lazy<Staging> stagingArea = lazy(() -> {
        /*check if index exist*/
        Staging s = INDEX.exists()
                ? Staging.fromFile()
                : new Staging();
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
        StringBuilder commitIds = Commit.findCommitbyMessage(message);
        if (commitIds != null) {
            System.out.print(commitIds);
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
            OBJECTS_DIR.mkdir();
//            Blobs_DIR.mkdir();
//            Commits_DIR.mkdir();
//            Refs_DIR.mkdir();
//        HEAD_DIR.mkdir();
            /*Set init commit object*/
            Commit initcommit = new Commit("initial commit");
            initcommit.saveCommit(nowbranch.get());
        } else {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
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

    public void checkout(String sha, File file) {
        String fileName = file.getPath();
        File shaFile = join(OBJECTS_DIR, sha);
        if (MyUtils.ifObjectisCommit(shaFile)) {
            Commit checkoutCommit = Commit.readCommit(sha);
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
            String headBranchname = readContentsAsString(HEAD);
            File headBranch = join(GITLET_DIR, headBranchname);
            String headBcommitId = readContentsAsString(headBranch);

            if (!branchName.equals(headBranchname)) {
                String checkoutId = readContentsAsString(branch);
                if (Commit.checkAllTracked(headBcommitId)) {
                    Commit.checkoutAll(checkoutId);
                    Commit.deleteDif(headBcommitId, checkoutId);
                    writeContents(HEAD, branchName);
                    stagingArea.get().clear();
                } else {
                    System.out.println("There is an untracked file in the way;"
                            + " delete it, or add and commit it first.");
                }
            }
//            else {System.out.println("No need to checkout the current branch.");}
        } else {
            System.out.println("No such branch exists.");
        }
    }

    public void rmbranch(String branchName) {
        File branch = join(GITLET_DIR, branchName);
        if (branchName.equals(nowbranch.get().getName())) {
            System.out.println("Cannot remove the current branch.");
        } else if (branch.exists()) {
            branch.delete();
        } else {
            System.out.println("A branch with that name does not exist.");
        }

    }

    public void reset(String commitId) {
        String headBranchname = readContentsAsString(HEAD);
        File headBranch = join(GITLET_DIR, headBranchname);
        String headBcommitId = readContentsAsString(headBranch);

        String checkoutId = commitId;
        if (Commit.checkAllTracked(headBcommitId)) {
            Commit.checkoutAll(checkoutId);
            Commit.deleteDif(headBcommitId, checkoutId);
            writeContents(nowbranch.get(), commitId);
            stagingArea.get().clear();
        } else {
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
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
            stagingArea.get().clear();
            stagingArea.get().save();
            nowCommit.saveCommit(nowbranch.get());
        } else {
            System.out.println("No changes added to the commit.");
        }
    }

    public void rm(File rmFile) {
        if (rmFile.exists()) {
            if (stagingArea.get().rmstage(rmFile)) {
                stagingArea.get().save();
            }
        }
//        else {System.out.println("File does not exist.");}
    }
}
