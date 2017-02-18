import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class Parser {

    private final List<Lexer.Token> tokens;
    private int i;

    Parser(List<Lexer.Token> tokens) {
        this.tokens = tokens;
        this.i = 0;
    }

    String parse(Map<String, Object> context) throws Exception {
        StringBuilder sb = new StringBuilder();
        Scope scope = new Scope(null, null, context);
        parse(sb, scope);
        return sb.toString();
    }

    private void parse(StringBuilder sb, Scope rootScope) throws Exception {
        Lexer.Token token = nextToken();

        Stack<Scope> scopes = new Stack<>();
        scopes.push(rootScope);

        while (token != null) {
            Scope currentScope = scopes.peek();

            switch (token.type) {
                case TEXT: {
                    if (currentScope.isEnabled()) {
                        sb.append(token.data);
                    }
                }
                break;

                case VARIABLE: {
                    if (currentScope.isEnabled()) {
                        sb.append(currentScope.getOrEmptyString(token.data));
                    }
                }
                break;

                case IF_BEGIN: {
                    Object value = currentScope.get(token.data);

                    Map<String, Object> context;
                    if (value == null) {
                        context = null;
                    } else if (value instanceof Map) {
                        context = (Map) value;
                    } else {
                        context = new HashMap<>();
                    }

                    scopes.push(new Scope(token.data, currentScope, context));
                }
                break;

                case IF_END: {
                    Scope scopeToThrowAway = scopes.pop();
                    if (scopes.isEmpty() || !scopeToThrowAway.getName().equals(token.data)) {
                        throw new Exception(String.format("Unexpected IF_END: {{/%s}}", token.data));
                    }
                }
                break;
            }

            token = nextToken();
        }

        if (scopes.size() > 1) {
            throw new Exception(String.format("Unclosed IF_BEGIN: {{#%s}}", scopes.peek().getName()));
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
