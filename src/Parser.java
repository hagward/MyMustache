import java.util.List;
import java.util.Map;

class Parser {

    static String parse(List<Lexer.Token> tokens, Map<String, String> scope) {
        StringBuilder sb = new StringBuilder();
        for (Lexer.Token token : tokens) {
            switch (token.type) {
                case TEXT:
                    sb.append(token.data);
                    break;
                case VARIABLE:
                    sb.append(scope.getOrDefault(token.data, ""));
                    break;
            }
        }
        return sb.toString();
    }
}
