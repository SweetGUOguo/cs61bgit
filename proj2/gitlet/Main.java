package gitlet;

import java.io.File;

import static gitlet.Repository.*;

import java.io.File;

import static gitlet.Utils.*;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author TODO
 */
public class Main {

    static final File CWD = new File(System.getProperty("user.dir"));

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                // TODO: handle the `init` command
                Repository.initRepository();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                if (!Repository.checkGit()) {
                    break;
                }
                File addFile = join(CWD, args[1]);
                new Repository().add(addFile);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                if (!Repository.checkGit()) {
                    break;
                }
                if (args.length == 1) {
                    System.out.println("Please enter a commit message.");
                } else {
                    new Repository().commit(args[1]);
                }
                break;
            case "rm":
                if (!Repository.checkGit()) {
                    break;
                }
                File rmFile = join(CWD, args[1]);
                new Repository().rm(rmFile);
                break;
            case "checkout":
                if (!Repository.checkGit()) {
                    break;
                }
                switch (args.length) {
                    case 2:
                        break;
                    case 3:
                        File checkoutbyFile = join(CWD, args[2]);
                        checkout(checkoutbyFile);
                        break;
                    case 4:
                        String fileCommitId = args[1];
                        File checkoutCommitFile = join(CWD, args[3]);
                        checkout(fileCommitId, checkoutCommitFile);
                        break;
                }
                break;

//                File checkoutbyFile = join(CWD, args[1]);
//                checkout(checkoutbyFile);
//                break;
//            case "checkout":
//                if(!Repository.checkGit()){
//                    break;
//                }
//                File checkoutFile = join(CWD, args[4]);
//                checkout(args[1],checkoutFile);
//                break;
            case "log":
                if (!Repository.checkGit()) {
                    break;
                }
                log();
                break;
            case "global-log":
                if (!Repository.checkGit()) {
                    break;
                }
                globalLog();
                break;
            case "find":
                if (!Repository.checkGit()) {
                    break;
                }
                if (args.length == 1) {
                    System.out.println("Please enter a commit message.");
                } else {
                    new Repository().find(args[1]);
                }
                break;
        }
    }
}
