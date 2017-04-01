package com.github.hagward.mymustache;

import java.util.ArrayList;
import java.util.List;

class Lexer {

    public enum TokenType {
        IF_BEGIN,
        IF_BEGIN_INV,
        IF_END,
        M_LEFT,
        M_LEFT_T,
        M_RIGHT,
        M_RIGHT_T,
        M_AMP,
        TEXT
    }

    public static class Token {
        final TokenType type;
        final String data;

        Token(TokenType type) {
            this.type = type;
            this.data = null;
        }

        Token(TokenType type, String data) {
            this.type = type;
            this.data = data;
        }

        @Override
        public String toString() {
            if (data == null) {
                return type.toString();
            } else {
                return String.format("%s(%s)", type, data);
            }
        }
    }

    private List<Token> tokens;

    List<Token> lex(String input) {
        tokens = new ArrayList<>();
        char[] v = input.toCharArray();

        for (int i = 0; i < v.length; ) {
            if (v[i] == '{' && i < v.length - 2 && v[i + 1] == '{' && v[i + 2] == '#') {
                tokens.add(new Token(TokenType.IF_BEGIN));
                i += 3;
            } else if (v[i] == '{' && i < v.length - 2 && v[i + 1] == '{' && v[i + 2] == '^') {
                tokens.add(new Token(TokenType.IF_BEGIN_INV));
                i += 3;
            } else if (v[i] == '{' && i < v.length - 2 && v[i + 1] == '{' && v[i + 2] == '/') {
                tokens.add(new Token(TokenType.IF_END));
                i += 3;
            } else if (v[i] == '{' && i < v.length - 2 && v[i + 1] == '{' && v[i + 2] == '&') {
                tokens.add(new Token(TokenType.M_AMP));
                i += 3;
            } else if (v[i] == '{' && i < v.length - 2 && v[i + 1] == '{' && v[i + 2] == '{') {
                tokens.add(new Token(TokenType.M_LEFT_T));
                i += 3;
            } else if (v[i] == '}' && i < v.length - 2 && v[i + 1] == '}' && v[i + 2] == '}') {
                tokens.add(new Token(TokenType.M_RIGHT_T));
                i += 3;
            } else if (v[i] == '{' && i < v.length - 1 && v[i + 1] == '{') {
                tokens.add(new Token(TokenType.M_LEFT));
                i += 2;
            } else if (v[i] == '}' && i < v.length - 1 && v[i + 1] == '}') {
                tokens.add(new Token(TokenType.M_RIGHT));
                i += 2;
            } else {
                Token oldToken = peekBack(2);
                if (oldToken != null && (oldToken.type == TokenType.IF_BEGIN ||
                                         oldToken.type == TokenType.IF_BEGIN_INV ||
                                         oldToken.type == TokenType.IF_END)) {

                    while (i < v.length && (v[i] == '\n' || v[i] == '\r')) {
                        i++;
                    }
                }

                int j = i;
                for (; i < v.length; i++) {
                    if (i < v.length - 1 && ((v[i] == '{' && v[i+1] == '{') || (v[i] == '}' && v[i+1] == '}'))) {
                        break;
                    }
                }
                tokens.add(new Token(TokenType.TEXT, input.substring(j, i)));
            }
        }

        return tokens;
    }

    private Token peekBack(int steps) {
        int i = tokens.size() - steps - 1;
        return (i >= 0) ? tokens.get(i) : null;
    }
}
