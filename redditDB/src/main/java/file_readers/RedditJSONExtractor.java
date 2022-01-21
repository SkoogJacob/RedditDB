package file_readers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * This class reads the Reddit data files.
 *
 * The files data are written as
 * ```{
 *     JSONDATA
 * } {
 *     JSONDATA
 * } ...etc
 * ```
 * This class' task is to extract the valid JSON objects, so they can be parsed by a
 * JSON parser.
 */
public class RedditJSONExtractor {
    private final Scanner scanner;
    private String next;
    /**
     * Constructs a JSONExtractor that will read from the file at the given path.
     *
     * @param filepath The path to the JSON file
     */
    public RedditJSONExtractor(String filepath) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        scanner = new Scanner(reader);
        scanner.useDelimiter("[{}]");
        loadNextJSON();
    }

    /**
     * Loads the next JSON object from the reader if there are more JSON objects.
     * It then loads the result into this.next, or it sets next to the empty string
     * if no next object was found.
     */
    private void loadNextJSON() {
        if (!scanner.hasNext()) {
            next = "";
            return;
        }
        String retVal;
        do {
            retVal = scanner.next().trim();
        } while (retVal.equals("") && scanner.hasNext());
        next = retVal.equals("") ? "" : "{" + retVal + "}";
    }

    /**
     * Extracts an object from the file as a JSON parse-able string.
     *
     * @return A JSON string if the reader had more objects in it, or null if eof is reached.
     */
    public String extractJSONObject() {
        String retVal = next;
        loadNextJSON();
        return retVal;
    }

    public boolean hasNext() {
        return !next.equals("");
    }
}
