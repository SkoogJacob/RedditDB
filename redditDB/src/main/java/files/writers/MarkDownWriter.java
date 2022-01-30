package files.writers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MarkDownWriter {
    /**
     * <p>
     * Writes the passed results to results.md in the current directory.
     * Expects a markdown formatted string.
     * </p>
     * <p>
     * If the file exists, this function call will append to the end of it. If
     * the file does not exist, it will create the file.
     * </p>
     *
     * @param toWrite The text to append to the file.
     * @throws IOException If there was en error in creating or writing to the file.
     */
    public static void writeToFile(String toWrite) throws IOException {
        writeToFilePrivate(toWrite, "./results.md");
    }

    /**
     * <p>
     *     Writes the String in the passed <b>'toWrite'</b> parameter to the file specified by the
     *     <b>'to'</b> parameter. If <b>'to'</b> is null, will write to 'results.md'.
     * </p>
     * <p>
     *     This function call will create the file (be it the passed file name or results.md) if it
     *     does not already exist.
     * </p>
     * <p>
     *     This method will append to the file if it already exists.
     * </p>
     *
     * @param toWrite The string to append to the file.
     * @param to The file to append the string to.
     * @throws IOException If the file could not be opened or written to.
     */
    public static void writeToFile(String toWrite, String to) throws IOException {
        if (to == null) {
            to = "./results.md";
        }
        writeToFilePrivate(toWrite, to);
    }
    private static void writeToFilePrivate(String toWrite, String to) throws IOException {
        File target = new File(to.trim());
        if (target.createNewFile()) {
            System.out.printf("%1$s was created!%n", target.getName());
            if (target.getName().split("\\.")[target.getName().split("\\.").length - 1].equalsIgnoreCase("md")) {
                FileWriter writer = new FileWriter(target.getCanonicalFile(), false);
                BufferedWriter buff = new BufferedWriter(writer);
                buff.write("# Reddit DB Write Testing\n");
                buff.close();
            }
        }
        FileWriter writer = new FileWriter(target.getCanonicalFile(), true);
        BufferedWriter buff = new BufferedWriter(writer);
        buff.write(toWrite);
        buff.close();
    }
}
