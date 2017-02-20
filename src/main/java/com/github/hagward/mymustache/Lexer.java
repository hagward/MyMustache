import java.util.ArrayList;
import java.util.List;

class Lexer {

    public enum TokenType {
        IF_BEGIN,
        IF_END,
        M_LEFT,
        M_LEFT_T,
        M_RIGHT,
        M_RIGHT_T,
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

    static List<Token> lex(String input) {
        char[] v = input.toCharArray();
        List<Token> tokens = new ArrayList<>();

        for (int i = 0; i < v.length; ) {
            if (v[i] == '{' && i < v.length - 2 && v[i + 1] == '{' && v[i + 2] == '#') {
                tokens.add(new Token(TokenType.IF_BEGIN));
                i += 3;
            } else if (v[i] == '{' && i < v.length - 2 && v[i + 1] == '{' && v[i + 2] == '/') {
                tokens.add(new Token(TokenType.IF_END));
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
}
