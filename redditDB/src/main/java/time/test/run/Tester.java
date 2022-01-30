package time.test.run;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import comments.FullComment;
import db.accessors.*;
import files.readers.RedditJSONExtractor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public final class Tester {
    private final SQLAccessParams params;
    private final File srcFile;
    private final String schema;
    private final Moshi moshi;

    public Tester(@NotNull SQLAccessParams params, String srcFile, String targetSchema) {
        this.params = params;
        this.srcFile = new File(srcFile);
        this.schema = targetSchema;
        this.moshi = new Moshi.Builder().build();
        assert this.srcFile.isFile();
    }
    public Test unconstrainedTest() throws IOException, SQLException, InterruptedException {
        final int coreCount = Runtime.getRuntime().availableProcessors();
        RedditJSONExtractor extractor = new RedditJSONExtractor(srcFile.getAbsolutePath());
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<FullComment> adapter = moshi.adapter(FullComment.class);

        long startTime = System.nanoTime();
        loadTables(true, extractor, adapter, coreCount);
        long totalTime = System.nanoTime() - startTime;
        String tableName = schema + "comments_unconstrained";
        return collateResult(totalTime, tableName, Test.TestType.UNCONSTRAINED);
    }

    public Test constrainedTest() throws IOException, SQLException, InterruptedException {
        final int batchSize = 15000;
        RedditJSONExtractor extractor = new RedditJSONExtractor(srcFile.getAbsolutePath());
        JsonAdapter<FullComment> adapter = moshi.adapter(FullComment.class);

        long startTime = System.nanoTime();
        loadTables(false, extractor, adapter, 1);
        long totalTime = System.nanoTime() - startTime;
        String tableName = schema + ".comments_constrained";

        return collateResult(totalTime, tableName, Test.TestType.CONSTRAINED);
    }
    public Test stagingTest() throws IOException, SQLException, InterruptedException {
        final int coreCount = Runtime.getRuntime().availableProcessors();
        RedditJSONExtractor extractor = new RedditJSONExtractor(srcFile.getAbsolutePath());
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<FullComment> adapter = moshi.adapter(FullComment.class);

        long startTime = System.nanoTime();
        loadTables(true, extractor, adapter, coreCount);
        new LoadAfterStaging(params, schema).load();
        long totalTime = System.nanoTime() - startTime;
        String tableName = schema + ".comments_constrained";

        return collateResult(totalTime, tableName, Test.TestType.PRESTAGED);
    }

    private void loadTables(boolean multiThreaded, RedditJSONExtractor extractor, JsonAdapter<FullComment> adapter, int coreCount) throws IOException, SQLException, InterruptedException {
        final int batchSize = multiThreaded ? 2000 : 15000;
        LinkedList<FullComment> comments = new LinkedList<>();
        LinkedList<Thread> threads = new LinkedList<>();

        while (extractor.hasNext()) {
            FullComment comment = adapter.fromJson(extractor.extractJSONObject());
            comments.add(comment);
            if (comments.size() == batchSize || !extractor.hasNext()) {
                FullComment[] data = new FullComment[comments.size()];
                comments.toArray(data);
                comments.clear();
                if (multiThreaded) {
                    Thread t = new Thread(new DBLoaderUnconstrained(params, schema, data));
                    t.start();
                    threads.add(t);
                    if (threads.size() == coreCount) {
                        threads.getFirst().join();
                        threads.removeFirst();
                    }
                } else {
                    DBLoaderConstrained loader = new DBLoaderConstrained(params, schema, data);
                    loader.run();
                }
            }
        }
        for (Thread t : threads) try { t.join(); } catch (InterruptedException ignored) { }
    }

    private Test collateResult(long totalTime, String tableName, Test.TestType type) throws SQLException {
        Connection conn = params.getConnection();
        Statement statement = conn.createStatement();
        ResultSet result = statement.executeQuery("""
            SELECT COUNT(*) FROM %1$s;
            """.formatted(tableName));
        result.first();
        int addedEntries = result.getInt(1);
        Test test = new Test(type, totalTime, addedEntries);
        result.close();statement.close();conn.close(); // Closing all SQL resources opened here.

        return test;
    }
}
