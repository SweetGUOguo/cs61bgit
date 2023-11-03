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
                File addFile = join(CWD, args[1]);
                /*produce a blob*/
                Blob addblob = new Blob(addFile);
                addblob.saveblob();
                /*save file to stage*/
//                buildTree(addFile,addblob);

                break;
            // TODO: FILL THE REST IN
        }
    }
}
