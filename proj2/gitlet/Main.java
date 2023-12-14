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
        if(args.length==0){
            System.out.println("Please enter a command.");
        }
        else {
            String firstArg = args[0];
            switch (firstArg) {
                case "init":
                    // TODO: handle the `init` command
                    new Repository().initRepository();
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
                        break;
                    } else if (args[1].equals("")) {
                        System.out.println("Please enter a commit message.");
                        break;
                    } else {
                        new Repository().commit(args[1]);
                    }
                    break;
                case "rm":
                    if (!Repository.checkGit()) {
                        break;
                    }
//                    File rmFile = join(CWD, args[1]);
                    new Repository().rm(args[1]);
                    break;
                case "rm-branch":
                    if(!Repository.checkGit()){
                        break;
                    }
                    new Repository().rmbranch(args[1]);
                    break;
                case "checkout":
                    if (!Repository.checkGit()) {
                        break;
                    }
                    switch (args.length) {
                        case 2:
                            String branchName = args[1];
                            new Repository().checkoutBranch(branchName);
                            break;
                        case 3:
                            if (args[1].equals("--")) {
                                File checkoutbyFile = join(CWD, args[2]);
                                new Repository().checkout(checkoutbyFile);
                            } else {
                                System.out.println("Incorrect operands.");
                            }
                            break;
                        case 4:
                            if (args[2].equals("--")) {
                                String fileCommitId = args[1];
                                File checkoutCommitFile = join(CWD, args[3]);
                                new Repository().checkout(fileCommitId, checkoutCommitFile);
                            } else {
                                System.out.println("Incorrect operands.");
                            }
                            break;
                    }
                    break;
                case "branch":
                    if (!Repository.checkGit()) {
                        break;
                    }
                    String addBranch = args[1];
                    new Repository().newBranch(addBranch);
                    break;

                case "log":
                    if (!Repository.checkGit()) {
                        break;
                    }
                    new Repository().log();
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
                case "status":
                    if (!Repository.checkGit()) {
                        break;
                    }
                    new Repository().status();
                    break;
                case "reset":
                    if (!Repository.checkGit()) {
                        break;
                    }
                    new Repository().reset(args[1]);
                    break;
                default:
                    System.out.println("No command with that name exists.");
            }
        }
    }
}
