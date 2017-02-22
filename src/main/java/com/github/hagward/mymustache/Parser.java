package com.github.hagward.mymustache;

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

                case M_LEFT: {
                    token = expect(Lexer.TokenType.TEXT);
                    if (currentScope.isEnabled()) {
                        //noinspection ConstantConditions: TEXT tokens should always be created with data.
                        String s = String.valueOf(currentScope.getOrEmptyString(token.data.trim()));
                        sb.append(escapeHtml(s));
                    }
                    expect(Lexer.TokenType.M_RIGHT);
                }
                break;

                case M_LEFT_T: {
                    token = expect(Lexer.TokenType.TEXT);
                    if (currentScope.isEnabled()) {
                        //noinspection ConstantConditions: TEXT tokens should always be created with data.
                        sb.append(currentScope.getOrEmptyString(token.data.trim()));
                    }
                    expect(Lexer.TokenType.M_RIGHT_T);
                }
                break;

                case M_AMP: {
                    token = expect(Lexer.TokenType.TEXT);
                    if (currentScope.isEnabled()) {
                        //noinspection ConstantConditions: TEXT tokens should always be created with data.
                        sb.append(currentScope.getOrEmptyString(token.data.trim()));
                    }
                    expect(Lexer.TokenType.M_RIGHT);
                }
                break;

                case IF_BEGIN: {
                    token = expect(Lexer.TokenType.TEXT);

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

                    expect(Lexer.TokenType.M_RIGHT);
                }
                break;

                case IF_END: {
                    token = expect(Lexer.TokenType.TEXT);

                    Scope scopeToThrowAway = scopes.pop();
                    if (scopes.isEmpty() || !scopeToThrowAway.getName().equals(token.data)) {
                        throw new Exception(String.format("Unexpected IF_END: {{/%s}}", token.data));
                    }

                    expect(Lexer.TokenType.M_RIGHT);
                }
                break;

                case M_RIGHT: {
                    throw new Exception("Unexpected }}");
                }
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

    private Lexer.Token expect(Lexer.TokenType type) throws Exception {
        Lexer.Token nextToken = nextToken();

        if (nextToken == null) {
            throw new Exception(String.format("Unexpected end of input; expected %s", type));
        } else if (nextToken.type != type) {
            throw new Exception(String.format("Expected %s, got %s", type, nextToken.type));
        }

        return nextToken;
    }

    private String escapeHtml(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                default:
                    sb.append(s.charAt(i));
            }
        }
        return sb.toString();
    }
}
