package gitlet;

import java.io.File;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

import java.util.ArrayList;
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
        System.out.println("Not in an initialized Gitlet directory.");
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

    public String getrightSha(String sha) {
        String allsha = new String();
        String shadir = makeSha2DIR(sha);
        File dir = join(OBJECTS_DIR, shadir);
        List<String> files = traverseFolder(dir);
        for (String filename : files) {
            File file = new File(filename);
            filename = file.getName();
            String allname = shadir + filename;
            if (allname.contains(sha)) {
                allsha = allname;
            }
        }
        return allsha;
    }

    public void checkout(String sha, File file) {
        String fileName = file.getPath();
//        File shaFile = join(OBJECTS_DIR, sha);
        if (sha.length() < 16) {
            sha = getrightSha(sha);
        }
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
                if (Commit.checkAllTracked(headBcommitId, checkoutId)) {
                    Commit.checkoutAll(checkoutId);
                    Commit.deleteDiftxt(headBcommitId, checkoutId);
                    writeContents(HEAD, branchName);
                    stagingArea.get().clear();
                    stagingArea.get().save();
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

    public void checkoutMerge(String branchName, String currentCommitid, List<File> conflictFiles) {
        File branch = join(GITLET_DIR, branchName);
        if (branch.exists()) {
//            String headBranchname = readContentsAsString(HEAD);
//            File headBranch = join(GITLET_DIR, headBranchname);
//            String headBcommitId = currentCommitid;

            String checkoutId = readContentsAsString(branch);
            if (Commit.checkAllTracked(currentCommitid, checkoutId)) {
                Commit.checkoutAllexp(checkoutId, conflictFiles);
                Commit.deleteDiftxt(currentCommitid, checkoutId);
                writeContents(HEAD, branchName);
                stagingArea.get().clear();
                stagingArea.get().save();
            } else {
                System.out.println("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
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
            if (Commit.checkAllTracked(headBcommitId, checkoutId)) {
                Commit.checkoutAll(checkoutId);
                Commit.deleteDiftxt(headBcommitId, checkoutId);
                writeContents(nowbranch.get(), commitId);
                stagingArea.get().clear();
                stagingArea.get().save();
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

    public void merge(String targetBranchname) {
        if (!stagingArea.get().isClear()) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        File targetBranch = join(GITLET_DIR, targetBranchname);
        if (!targetBranch.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        String currentBranchname = nowbranch.get().getName();
        if (targetBranchname.equals(currentBranchname)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        File currentBranchfile = join(GITLET_DIR, currentBranchname);
        String currentCommitId = readContentsAsString(currentBranchfile);
        File targetCommitfile = join(GITLET_DIR, targetBranchname);
        String targetCommitId = readContentsAsString(targetCommitfile);

        /*Check if there is an untracked file that would be overwritten or deleted */
        if (!Commit.checkAllTracked(currentCommitId, targetCommitId)) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
            return;
        }

        /*Find the splitCommit*/
        String splitCommitid = findSplit(currentCommitId, targetCommitId);

        if (splitCommitid.equals(currentCommitId)) {
            checkoutBranch(targetBranchname);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        if (splitCommitid.equals(targetCommitId)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }

        Commit splitCommit = Commit.readCommit(splitCommitid);
        Commit currentCommit = Commit.readCommit(currentCommitId);
        Commit targetCommit = Commit.readCommit(targetCommitId);
        Commit newCommit = Commit.readCommit(splitCommitid);

        TreeMap<String, String> splitTree = splitCommit.getTrackTree();
        TreeMap<String, String> currentTree = currentCommit.getTrackTree();
        TreeMap<String, String> targetTree = targetCommit.getTrackTree();
        TreeMap<String, String> allTree = new TreeMap<>();
        allTree.putAll(splitTree);
        allTree.putAll(currentTree);
        allTree.putAll(targetTree);

        boolean conflict = false;
        List<File> conflictFiles = new ArrayList<File>();

        for (String filename : allTree.keySet()) {
            if (splitTree.containsKey(filename)) {
                if (currentTree.containsKey(filename)) {
                    /*two contains*/
                    if (targetTree.containsKey(filename)) {
                        //targetTree v
                        //currentTree v
                        if (splitTree.get(filename).equals(currentTree.get(filename))) {
                            if (!splitTree.get(filename).equals(targetTree.get(filename))) {
                                newCommit.getTrackTree().put(filename, targetTree.get(filename));
                            }
                        } else {
                            if (splitTree.get(filename).equals(targetTree.get(filename))) {
                                newCommit.getTrackTree().put(filename, currentTree.get(filename));
                            } else {
                                if (currentTree.get(filename).equals(targetTree.get(filename))) {
                                    newCommit.getTrackTree()
                                            .put(filename, currentTree.get(filename));
                                } else {
                                    /*both diff*/
                                    String tblobSHA = targetTree.get(filename);
                                    String cblobSHA = currentTree.get(filename);
                                    Blob checkoutcBlob = Blob.readBlob(cblobSHA);
                                    Blob checkouttBlob = Blob.readBlob(tblobSHA);
                                    byte[] ccontent = checkoutcBlob.getContent();
                                    byte[] tcontent = checkouttBlob.getContent();
                                    File file = new File(filename);
                                    writeContents(file, "<<<<<<< HEAD", "\n", ccontent, "=======",
                                            "\n", tcontent, ">>>>>>>", "\n");
                                    conflict = true;
                                    conflictFiles.add(file);

                                    Blob addblob = new Blob(file);
                                    addblob.saveblob();
                                    newCommit.getTrackTree().put(filename, addblob.getRefs());
                                }
                            }
                        }
                    } else {
                        //targetTree x
                        //currentTree v
                        if (splitTree.get(filename).equals(currentTree.get(filename))) {
                            newCommit.getTrackTree().remove(filename);
                        } else {
//                            String tblobSHA = targetTree.get(filename);
                            String cblobSHA = currentTree.get(filename);
                            Blob checkoutcBlob = Blob.readBlob(cblobSHA);
//                            Blob checkouttBlob = Blob.readBlob(tblobSHA);
                            byte[] ccontent = checkoutcBlob.getContent();
//                            byte[] tcontent = checkouttBlob.getContent();
                            File file = new File(filename);
                            writeContents(file, "<<<<<<< HEAD", "\n", ccontent,
                                    "=======", "\n", ">>>>>>>", "\n");
                            conflict = true;
                            conflictFiles.add(file);

                            Blob addblob = new Blob(file);
                            addblob.saveblob();
                            newCommit.getTrackTree().put(filename, addblob.getRefs());
                        }
                    }
                } else {
                    if (targetTree.containsKey(filename)) {
                        //targetTree v
                        //currentTree x
                        if (splitTree.get(filename).equals(targetTree.get(filename))) {
                            newCommit.getTrackTree().remove(filename);
                        } else {
                            String tblobSHA = targetTree.get(filename);
//                            String cblobSHA = currentTree.get(filename);
//                            Blob checkoutcBlob = Blob.readBlob(cblobSHA);
                            Blob checkouttBlob = Blob.readBlob(tblobSHA);
//                            byte[] ccontent = checkoutcBlob.getContent();
                            byte[] tcontent = checkouttBlob.getContent();
                            File file = new File(filename);
                            writeContents(file, "<<<<<<< HEAD", "\n", "=======",
                                    "\n", tcontent, ">>>>>>>", "\n");
                            conflict = true;
                            conflictFiles.add(file);

                            Blob addblob = new Blob(file);
                            addblob.saveblob();
                            newCommit.getTrackTree().put(filename, addblob.getRefs());
                        }

                    } else {
                        newCommit.getTrackTree().remove(filename);
                    }
                }
            } else {
                if (currentTree.containsKey(filename)) {
                    /*two contains*/
                    if (targetTree.containsKey(filename)) {
                        //targetTree v
                        //currentTree v
                        if (currentTree.get(filename).equals(targetTree.get(filename))) {
                            newCommit.getTrackTree().put(filename, currentTree.get(filename));
                        } else {
                            String tblobSHA = targetTree.get(filename);
                            String cblobSHA = currentTree.get(filename);
                            Blob checkoutcBlob = Blob.readBlob(cblobSHA);
                            Blob checkouttBlob = Blob.readBlob(tblobSHA);
                            byte[] ccontent = checkoutcBlob.getContent();
                            byte[] tcontent = checkouttBlob.getContent();
                            File file = new File(filename);
                            writeContents(file, "<<<<<<< HEAD", "\n", ccontent, "=======",
                                    "\n", tcontent, ">>>>>>>", "\n");
                            conflict = true;
                            conflictFiles.add(file);

                            Blob addblob = new Blob(file);
                            addblob.saveblob();
                            newCommit.getTrackTree().put(filename, addblob.getRefs());
                        }
                    } else {
                        //targetTree x
                        //currentTree v
                        newCommit.getTrackTree().put(filename, currentTree.get(filename));
                    }
                } else {
                    if (targetTree.containsKey(filename)) {
                        //targetTree v
                        //currentTree x
                        newCommit.getTrackTree().put(filename, targetTree.get(filename));
                    }
                }
            }
        }

        newCommit.setMessage(currentBranchname, targetBranchname);
        newCommit.setMerge(currentCommit, targetCommit);
        newCommit.saveCommit(nowbranch.get());
        stagingArea.get().setTrackedTree();

        checkoutMerge(nowbranch.get().getName(), currentCommitId, conflictFiles);

        if (conflict) {
            System.out.println("Encountered a merge conflict.");
//            for (File conflictfile : conflictFiles) {
//                add(conflictfile);
//            }
        }
    }

    private String findSplit(String currentId, String targetId) {
        TreeMap<String, Integer> currentMap = buildCommitMap(currentId);
        TreeMap<String, Integer> targetMap = buildCommitMap(targetId);
        String splitCommit = "";
        int minDepth = Integer.MAX_VALUE;
        for (String commitId : currentMap.keySet()) {
            if (targetMap.containsKey(commitId)) {
                if (currentMap.get(commitId) < minDepth) {
                    splitCommit = commitId;
                    minDepth = currentMap.get(commitId);
                }

            }
        }
        return splitCommit;
    }

    //    private TreeMap<String, Integer> buildCommitMap(String commitId) {
//        TreeMap<String, Integer> treeCommit = new TreeMap<>();
//        int i = 0;
//        while (commitId != null) {
//            treeCommit.put(commitId, i);
//            Commit addCommit = Commit.readCommit(commitId);
//            if (addCommit.getParent() != null) {
//                commitId = addCommit.getParent().get(0);
//                i = i + 1;
//            } else {
//                break;
//            }
//        }
//        return treeCommit;
//    }
    private TreeMap<String, Integer> buildCommitMap(String commitId) {
        TreeMap<String, Integer> treeCommit = new TreeMap<>();
        buildCommitMapHelper(commitId, 0, treeCommit);
        return treeCommit;
    }

    private void buildCommitMapHelper(String commitId, int i, TreeMap<String, Integer> treeCommit) {
        if (commitId == null || treeCommit.containsKey(commitId)) {
            return;
        }
        treeCommit.put(commitId, i);
        Commit addCommit = Commit.readCommit(commitId);
        if (addCommit.getParent() != null) {
            for (String parentId : addCommit.getParent()) {
                buildCommitMapHelper(parentId, i + 1, treeCommit);
            }
        }
    }
}
