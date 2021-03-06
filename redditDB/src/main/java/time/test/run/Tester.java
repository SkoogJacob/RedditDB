package time.test.run;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import comments.FullComment;
import db.accessors.*;
import db.accessors.loaders.DBLoaderConstrained;
import db.accessors.loaders.DBLoaderUnconstrained;
import db.accessors.loaders.LoadAfterStaging;
import files.readers.RedditJSONExtractor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public final class Tester {
    private final SQLAccessParams params;
    private final File srcFile;
    private final List<File> srcFiles;
    private final String schema;
    private final Moshi moshi;
    private final JsonAdapter<FullComment> adapter;

    /**
     * Creates a tester object loading data from only one file.
     *
     * @param params Contains url, username, and password to create connection to SQL server.
     * @param srcFile The file containing the data to load into the SQL file.
     * @param targetSchema The target schema in the database to store the tables and data.
     */
    public Tester(@NotNull SQLAccessParams params, String srcFile, String targetSchema) {
        this.params = params;
        this.srcFile = new File(srcFile);
        this.srcFiles = null;
        this.schema = targetSchema;
        this.moshi = new Moshi.Builder().build();
        this.adapter = this.moshi.adapter(FullComment.class);
        assert this.srcFile.isFile();
    }

    /**
     * Creates a tester object that loads data from multiple files.
     *
     * @param params Contains url, username, and password to create connection to SQL server.
     * @param srcFiles The files containing the data to load into the SQL file.
     * @param targetSchema The target schema in the database to store the tables and data.
     */
    public Tester(@NotNull SQLAccessParams params, List<String> srcFiles, String targetSchema) {
        this.params = params;
        this.srcFile = null;
        this.srcFiles = new LinkedList<>();
        for (String src : srcFiles) {
            this.srcFiles.add(new File(src));
        }
        this.schema = targetSchema;
        this.moshi = new Moshi.Builder().build();
        this.adapter = this.moshi.adapter(FullComment.class);
        for (File src : this.srcFiles) assert src.isFile();
    }

    public Test run(Test.TestType type) throws SQLException, IOException, InterruptedException {
        Test ret = null;
        switch (type) {
            case UNCONSTRAINED -> ret = unconstrainedTest();
            case CONSTRAINED -> ret = constrainedTest();
            case PRESTAGED -> ret = stagingTest();
        }
        return ret;
    }
    public Test unconstrainedTest() throws IOException, SQLException, InterruptedException {
        final int coreCount = Runtime.getRuntime().availableProcessors();
        RedditJSONExtractor extractor = getJSONExtractor();

        long startTime = System.nanoTime();
        loadTables(true, extractor, adapter, coreCount);
        long totalTime = System.nanoTime() - startTime;
        String tableName = schema + ".comments_unconstrained";
        return collateResult(totalTime, tableName, Test.TestType.UNCONSTRAINED);
    }

    public Test constrainedTest() throws IOException, SQLException, InterruptedException {
        RedditJSONExtractor extractor = getJSONExtractor();
        final int coreCount = Runtime.getRuntime().availableProcessors();

        Connection conn = params.getConnection();
        conn.setCatalog(schema);
        Statement statement = conn.createStatement();
        SQLTableManager.dropForeignIndices(statement);
        SQLTableManager.dropCommentIndexes(statement);
        SQLTableManager.addForeignIndices(statement);
        statement.executeBatch();
        statement.close();
        conn.close();

        long startTime = System.nanoTime();
        loadTables(false, extractor, adapter, 1);
        long totalTime = System.nanoTime() - startTime;
        String tableName = schema + ".comments_constrained";

        return collateResult(totalTime, tableName, Test.TestType.CONSTRAINED);
    }
    public Test stagingTest() throws IOException, SQLException, InterruptedException {
        final int coreCount = Runtime.getRuntime().availableProcessors();
        RedditJSONExtractor extractor = getJSONExtractor();

        long startTime = System.nanoTime();
        loadTables(true, extractor, adapter, coreCount);
        new LoadAfterStaging(params, schema).load();

        Connection conn = params.getConnection();
        conn.setCatalog(schema);
        Statement statement = conn.createStatement();
        SQLTableManager.addCommentIndices(statement);
        statement.executeBatch();
        statement.close();
        conn.close();

        long totalTime = System.nanoTime() - startTime;
        String tableName = schema + ".comments_constrained";

        return collateResult(totalTime, tableName, Test.TestType.PRESTAGED);
    }

    private void loadTables(boolean unconstrained, RedditJSONExtractor extractor, JsonAdapter<FullComment> adapter, int coreCount) throws IOException, SQLException, InterruptedException {
        final int batchSize = unconstrained ? 6000 : 50000; // Work with smaller batches when multithreaded
        LinkedList<FullComment> comments = new LinkedList<>();
        LinkedList<Thread> threads = new LinkedList<>();

        while (extractor.hasNext()) {
            FullComment comment = adapter.fromJson(extractor.extractJSONObject());
            comments.add(comment);
            if (comments.size() == batchSize || !extractor.hasNext()) {
                FullComment[] data = new FullComment[comments.size()];
                comments.toArray(data);
                comments.clear();
                Thread t;
                if (unconstrained) {
                    t = new Thread(new DBLoaderUnconstrained(params, schema, data));
                } else {
                    t = new Thread(new DBLoaderConstrained(params, schema, data));
                }
                t.start();
                threads.add(t);
                if (threads.size() == coreCount) {
                    threads.getFirst().join();
                    threads.removeFirst();
                }
            }
        }
        for (Thread t : threads) try { t.join(); } catch (InterruptedException ignored) { }
        Connection conn = params.getConnection();
        conn.setCatalog(schema);
        Statement statement = conn.createStatement();
        SQLTableManager.addCommentIndices(statement);
        statement.executeBatch();
        statement.close();
        conn.close();
    }

    private Test collateResult(long totalTime, String tableName, Test.TestType type) throws SQLException {
        Connection conn = params.getConnection();
        conn.setCatalog(schema);
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

    private RedditJSONExtractor getJSONExtractor() throws IOException {
        RedditJSONExtractor extractor;
        if (this.srcFiles == null) {
            extractor = new RedditJSONExtractor(this.srcFile.getAbsolutePath());
        } else {
            List<String> srcPaths = new LinkedList<>();
            for (File f : this.srcFiles) srcPaths.add(f.getAbsolutePath());
            extractor = new RedditJSONExtractor(srcPaths);
        }
        return extractor;
    }

}
