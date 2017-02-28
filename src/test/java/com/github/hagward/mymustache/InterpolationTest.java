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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("WeakerAccess")
@RunWith(Parameterized.class)
public class InterpolationTest {

    private static class Spec {
        private List<Map<String, Object>> tests;
    }

    private static Gson gson = new Gson();

    @Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() throws FileNotFoundException {
        List<Object[]> params = new ArrayList<>();

        Stream.of(
                "src/test/java/com/github/hagward/mymustache/comments.json",
                "src/test/java/com/github/hagward/mymustache/delimiters.json",
                "src/test/java/com/github/hagward/mymustache/interpolation.json",
                "src/test/java/com/github/hagward/mymustache/inverted.json",
                "src/test/java/com/github/hagward/mymustache/partials.json",
                "src/test/java/com/github/hagward/mymustache/sections.json")
                .map(InterpolationTest::parseSpec)
                .forEach(spec -> params.addAll(createTests(spec)));

        return params;
    }

    private static List<Object[]> createTests(Spec spec) {
        return spec.tests.stream()
                .map(test -> new Object[] {
                        test.get("name"),
                        test.get("desc"),
                        test.get("data"),
                        test.get("template"),
                        test.get("expected")
                })
                .collect(Collectors.toList());
    }

    private static Spec parseSpec(String fileName) {
        try {
            return gson.fromJson(new FileReader(fileName), Spec.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new Spec();
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
