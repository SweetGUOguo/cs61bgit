package gitlet;

import java.io.File;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

import java.util.List;
import java.util.Set;
import java.util.TreeMap;


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

    public static void deletetxtFile(String filename) {
        File file = new File(filename);
        if (isTxtFile(file)) {
            restrictedDelete(file);
        }
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
//        File shaFile = join(OBJECTS_DIR, sha);
        File shaFile = getObjectfileById(sha);
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
                    Commit.deleteDiftxt(headBcommitId, checkoutId);
                    writeContents(HEAD, branchName);
                    stagingArea.get().clear();
                } else {
                    System.out.println("There is an untracked file in the way;"
                            + " delete it, or add and commit it first.");
                }
            } else {
                System.out.println("No need to checkout the current branch.");
            }
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
        if (MyUtils.ifObjectisCommit(getObjectfileById(commitId))) {
            if (Commit.checkAllTracked(headBcommitId)) {
                Commit.checkoutAll(checkoutId);
                Commit.deleteDiftxt(headBcommitId, checkoutId);
                writeContents(nowbranch.get(), commitId);
                stagingArea.get().clear();
            } else {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
            }
        } else {
            System.out.println("No commit with that id exists.");
        }
    }

    public void newBranch(String branchName) {
        File branch = join(GITLET_DIR, branchName);
        /*cp the file which HEAD points to.*/
        if (branch.exists()) {
            System.out.println("A branch with that name already exists.");
        } else {
            String contents = readContentsAsString(nowbranch.get());
            writeContents(branch, contents);
            /*no! not put nowbranch into HEAD*/
//        writeContents(HEAD, branch.getName());
        }
    }

    public void log() {
        String headCommitID = readContentsAsString(nowbranch.get());
        Commit.printLog(headCommitID);
    }

    public void status() {
        printBranch();
        printAddstage();
        printRemovestage();
        printModifications();
        printUntracked();
    }

    private void printBranch() {
        System.out.println("=== Branches ===");
        String headBranchname = readContentsAsString(HEAD);

        String[] fileNames = traverseFiles(GITLET_DIR);
        for (String fileName : fileNames) {
            if (fileName.equals(headBranchname)) {
                System.out.print("*");
            }
            System.out.println(fileName);
        }
        System.out.println();
    }

    private void printAddstage() {
        System.out.println("=== Staged Files ===");
        Set<String> adds = stagingArea.get().getAdd().keySet();
        for (String add : adds) {
            File filewithPath = new File(add);
            String filename = filewithPath.getName();
            System.out.println(filename);
        }
        System.out.println();
    }

    private void printRemovestage() {
        System.out.println("=== Removed Files ===");
        Set<String> removes = stagingArea.get().getRemove();
        for (String remove : removes) {
            File filewithPath = new File(remove);
            String filename = filewithPath.getName();
            System.out.println(filename);
        }
        System.out.println();
    }

    private void printModifications() {
        System.out.println("=== Modifications Not Staged For Commit ===");
        // get the file
        List<String> workFiles = plainFilenamesIn(CWD);
        // get the now-tracked tree
        TreeMap<String, String> fileTrackTree = Commit.getNowtracked();

        for (String workFile : workFiles) {
            File file = join(CWD, workFile);
            if (isTxtFile(file)) {
                if (fileTrackTree.containsKey(file.getPath())) {
                    if (!Commit.checknowCommit(file)) {
                        if (!checknowAdd(file)) {
                            System.out.println(file.getName() + " (modified)");
                        }
                    }
                } else {
                    if (stagingArea.get().getAdd().containsKey(file.getPath())) {
                        if (!checknowAdd(file)) {
                            System.out.println(file.getName() + " (modified)");
                        }
                    }
                }
            }
        }

        for (String addstageFile : stagingArea.get().getAdd().keySet()) {
            File file = new File(addstageFile);
            if (!workFiles.contains(file.getName())) {
                System.out.println(file.getName() + " (deleted)");
            }
        }

        for (String nowommitFile : Commit.getNowtracked().keySet()) {
            File file = new File(nowommitFile);
            if (!workFiles.contains(file.getName())) {
                if (!stagingArea.get().getRemove().contains(file.getPath())) {
                    System.out.println(file.getName() + " (deleted)");
                }
            }
        }
        System.out.println();
    }

    private boolean checknowAdd(File file) {
        Blob fileblob = new Blob(file);
        String blobId = fileblob.getRefs();
        if (stagingArea.get().getAdd().containsKey(file.getPath())) {
            if (stagingArea.get().getAdd().get(file.getPath()).equals(blobId)) {
                return true;
            }
        }
        return false;
    }

    private void printUntracked() {
        System.out.println("=== Untracked Files ===");

        List<String> workFiles = plainFilenamesIn(CWD);
        for (String workFile : workFiles) {
            File file = join(CWD, workFile);
            if (isTxtFile(file)) {
                if (!Commit.getNowtracked().containsKey(file.getPath())) {
                    if (!stagingArea.get().getAdd().containsKey(file.getPath())) {
                        System.out.println(file.getName());
                    }
                }
            }
        }

        for (String rmfile : stagingArea.get().getRemove()) {
            File file = new File(rmfile);
            if (workFiles.contains(file.getName())) {
                System.out.println(file.getName());
            }
        }

        System.out.println();
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

    public void rm(String rm) {
        File rmFile = join(CWD, rm);
        if (rmFile.exists()) {
            if (stagingArea.get().rmstage(rmFile)) {
                stagingArea.get().save();
            }
        } else {
            if (stagingArea.get().rmstage(rmFile)) {
                stagingArea.get().save();
            }
        }
//        else {System.out.println("File does not exist.");}
    }
}
