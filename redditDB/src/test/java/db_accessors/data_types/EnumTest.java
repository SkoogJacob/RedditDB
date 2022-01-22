package db_accessors.data_types;

import org.testng.Assert;

import static org.testng.Assert.*;

public class EnumTest {

    private Enum sut;
    private final String[] vals = new String[]{"hi", "there", "stranger!"};

    @org.testng.annotations.BeforeTest
    public void setSut() {
        sut = new Enum(vals);
    }

    @org.testng.annotations.Test
    public void testGetName() {
        Assert.assertEquals(sut.getName(), "enum");
    }

    @org.testng.annotations.Test
    public void testGetTypeParams() {
        String expected = "";
        for (int i = 0; i < vals.length; i++) {
            expected += "'" + vals[i] + "'";
            if (i != vals.length - 1) expected += ", ";
        }
        Assert.assertTrue(sut.getTypeParams().isPresent());
        Assert.assertEquals(sut.getTypeParams().get(), expected);
    }
}