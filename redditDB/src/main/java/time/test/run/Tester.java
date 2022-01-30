package time.test.run;

import db.accessors.SQLAccessParams;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class Tester {
    private final SQLAccessParams params;
    private final File srcFile;
    public Tester(@NotNull SQLAccessParams params, String srcFile) {
        this.params = params;
        this.srcFile = new File(srcFile);
        assert this.srcFile.isFile();
    }
    public Test unconstrainedTest() {
        return null; // TODO
    }
    public Test constrainedTest() {
        return null; // TODO
    }
    public Test stagingTest() {
        return null; // TODO
    }
}
