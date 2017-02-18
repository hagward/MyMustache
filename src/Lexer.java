import java.util.ArrayList;
import java.util.List;

class Lexer {

    public enum TokenType {
        IF_BEGIN, IF_END, TEXT, VARIABLE,
    }

    public static class Token {
        final TokenType type;
        final String data;

        Token(TokenType type, String data) {
            this.type = type;
            this.data = data;
        }

        @Override
        public String toString() {
            return String.format("%s(%s)", type, data);
        }
    }

    static List<Token> lex(String input) {
        List<Token> result = new ArrayList<>();
        for (int i = 0; i < input.length(); ) {
            if (isStartOfIf(input, i)) {
                String variable = getVariable(input, i);
                i += variable.length() + 4;
                result.add(new Token(TokenType.IF_BEGIN, variable.substring(1)));
            } else if (isEndOfIf(input, i)) {
                String variable = getVariable(input, i);
                i += variable.length() + 4;
                result.add(new Token(TokenType.IF_END, variable.substring(1)));
            } else if (isStartOfVariable(input, i)) {
                String variable = getVariable(input, i);
                i += variable.length() + 4;
                result.add(new Token(TokenType.VARIABLE, variable));
            } else {
                String text = getText(input, i);
                i += text.length();
                result.add(new Token(TokenType.TEXT, text));
            }
        }
        return result;
    }

    private static boolean isStartOfIf(String input, int i) {
        return input.charAt(i) == '{' && i < input.length() - 2 && input.charAt(i + 1) == '{' && input.charAt(i + 2) == '#';
    }

    private static boolean isEndOfIf(String input, int i) {
        return input.charAt(i) == '{' && i < input.length() - 2 && input.charAt(i + 1) == '{' && input.charAt(i + 2) == '/';
    }

    private static boolean isStartOfVariable(String input, int i) {
        return input.charAt(i) == '{' && i < input.length() - 1 && input.charAt(i + 1) == '{';
    }

    private static boolean isEndOfVariable(String input, int i) {
        return input.charAt(i) == '}' && i < input.length() - 1 && input.charAt(i + 1) == '}';
    }

    private static String getVariable(String input, int i) {
        int j = i;
        for ( ; j < input.length(); ) {
            if (isEndOfVariable(input, j)) {
                return input.substring(i + 2, j);
            } else {
                j++;
            }
        }
        throw new RuntimeException("Unclosed variable");
    }

    private static String getText(String input, int i) {
        int j = i;
        for ( ; j < input.length(); ) {
            if (isStartOfVariable(input, j)) {
                break;
            } else {
                j++;
            }
        }
        return input.substring(i, j);
    }
}
