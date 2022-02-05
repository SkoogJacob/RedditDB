package files.readers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * This class reads the Reddit data files.
 *
 * The files data are written as
 * <p>
 * {JSONDATA} <br />
 * {JSONDATA}  <br />
 * {JSONDATA}  <br />
 * ...etc
 * </p>
 * This class' task is to extract the valid JSON objects, so they can be parsed by a
 * JSON parser.
 */
public class RedditJSONExtractor {
    private final List<String> filePaths;
    private final BufferedReader reader;
    private Scanner scanner;
    private String next;
    /**
     * Constructs a JSONExtractor that will read from the file at the given path.
     *
     * @param filepath The path to the JSON file
     */
    public RedditJSONExtractor(String filepath) throws IOException {
        this.reader = getReader(filepath);
        this.scanner = new Scanner(reader);
        this.filePaths = null;
//        scanner.useDelimiter("[{}]");
        loadNextJSON();
    }

    public RedditJSONExtractor(List<String> filepaths) throws FileNotFoundException {
        this.reader = getReader(filepaths.get(0));
        this.scanner = new Scanner(reader);
        this.filePaths = filepaths;
        this.filePaths.remove(0);
    }

    /**
     * Loads the next JSON object from the reader if there are more JSON objects.
     * It then loads the result into this.next, or it sets next to the empty string
     * if no next object was found.
     */
    private void loadNextJSON() throws IOException {
        if (!scanner.hasNext() && (this.filePaths == null || this.filePaths.isEmpty())) {
            next = "";
            return;
        } else if (!scanner.hasNext()) {
            scanner.close();
            reader.close();
            scanner = new Scanner(getReader(filePaths.get(0)));
            filePaths.remove(0);
        }
        next = scanner.nextLine();
    }

    /**
     * Extracts an object from the file as a JSON parse-able string.
     *
     * @return A JSON string if the reader had more objects in it, or null if eof is reached.
     */
    public String extractJSONObject() throws FileNotFoundException {
        if (!hasNext()) throw new NoSuchElementException("There are no more JSON objects!");
        String retVal = next;
        loadNextJSON();
        return retVal;
    }

    public boolean hasNext() {
        return !next.equals("");
    }

    private BufferedReader getReader(String path) throws FileNotFoundException {
        return new BufferedReader(new FileReader(path));
    }
}
