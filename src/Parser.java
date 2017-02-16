import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class Parser {

    private final List<Lexer.Token> tokens;
    private int i;

    public Parser(List<Lexer.Token> tokens) {
        this.tokens = tokens;
        this.i = 0;
    }

    public String parse(Map<String, Object> context) {
        StringBuilder sb = new StringBuilder();
        Scope scope = new Scope(null, context);
        parse(sb, scope);
        return sb.toString();
    }

    private void parse(StringBuilder sb, Scope scope) {
        Lexer.Token token = nextToken();

        while (token != null) {
            switch (token.type) {
                case TEXT:
                    sb.append(token.data);
                    break;
                case VARIABLE:
                    sb.append(scope.getOrEmptyString(token.data));
                    break;
                case IF_BEGIN: {
                    Object value = scope.get(token.data);
                    if (value == null) {
                        Lexer.Token nextToken = nextToken();
                        while (nextToken != null && nextToken.type != Lexer.TokenType.IF_END && !Objects.equals(nextToken.data, token.data)) {
                            nextToken = nextToken();
                        }
                    } else if (value instanceof Map) {
                        Scope newScope = new Scope(scope, (Map<String, Object>) value);
                        parse(sb, newScope);
                    }
                }
                break;
                case IF_END: {
                    return;
                }
            }

            token = nextToken();
        }
    }

    private Lexer.Token nextToken() {
        if (i < tokens.size()) {
            return tokens.get(i++);
        } else {
            return null;
        }
    }
}
