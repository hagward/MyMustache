package com.github.hagward.mymustache;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
@RunWith(Parameterized.class)
public class InterpolationTest {

    private static class Spec {
        private List<Map<String, Object>> tests;
    }

    @Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() throws FileNotFoundException {
        Gson gson = new Gson();
        Spec spec = gson.fromJson(new FileReader("src/test/java/com/github/hagward/mymustache/interpolation.json"), Spec.class);

        List<Object[]> params = new ArrayList<>();

        for (Map<String, Object> test : spec.tests) {
            params.add(new Object[] {
                    test.get("name"),
                    test.get("desc"),
                    test.get("data"),
                    test.get("template"),
                    test.get("expected")
            });
        }

        return params;
    }

    @Parameter
    public String name;

    @Parameter(1)
    public String description;

    @Parameter(2)
    public Map<String, Object> data;

    @Parameter(3)
    public String template;

    @Parameter(4)
    public String expected;

    @Test
    public void test() throws Exception {
        Parser parser = new Parser(Lexer.lex(template));
        Assert.assertEquals(description, expected, parser.parse(data));
    }
}
