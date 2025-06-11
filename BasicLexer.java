import java.util.*;
import java.util.regex.*;

public class BasicLexer {

    enum TokenType {
        KEYWORD, IDENTIFIER, NUMBER, STRING, OPERATOR, DELIMITER, COMMENT, UNKNOWN
    }

    public static class Token {
        TokenType type;
        String value;
        int position;

        Token(TokenType type, String value, int position) {
            this.type = type;
            this.value = value;
            this.position = position;
        }

        public String toString() {
            return "[" + type + ": " + value + "] at position " + position;
        }
    }

    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "IF", "THEN", "PRINT", "GOTO", "FOR", "NEXT", "LET", "REM", "INPUT", "END"
    ));

    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "\\s*(REM.*$|\"[^\"]*\"|\\d+(\\.\\d+)?|[A-Za-z_][A-Za-z0-9_]*|<>|<=|>=|[=+\\-*/<>(),])",
            Pattern.MULTILINE
    );

    private static boolean isIdentifierFA(String s) {
        if (s == null || s.isEmpty()) return false;
        if (!Character.isLetter(s.charAt(0)) && s.charAt(0) != '_') return false;
        for (char c : s.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '_') return false;
        }
        return true;
    }

    private static boolean isNumberFA(String s) {
        if (s == null || s.isEmpty()) return false;

        boolean seenDigit = false;
        boolean seenDot = false;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Character.isDigit(c)) {
                seenDigit = true;
            } else if (c == '.') {
                if (seenDot) return false;
                seenDot = true;
            } else {
                return false;
            }
        }

        return seenDigit && !s.endsWith(".");
    }

    public static List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(input);

        while (matcher.find()) {
            String lexeme = matcher.group().trim();
            int pos = matcher.start();

            if (lexeme.isEmpty()) continue;

            if (lexeme.startsWith("REM")) {
                tokens.add(new Token(TokenType.COMMENT, lexeme, pos));
            } else if (lexeme.matches("\"[^\"]*\"")) {
                tokens.add(new Token(TokenType.STRING, lexeme, pos));
            } else if (isNumberFA(lexeme)) {
                tokens.add(new Token(TokenType.NUMBER, lexeme, pos));
            } else if (KEYWORDS.contains(lexeme.toUpperCase())) {
                tokens.add(new Token(TokenType.KEYWORD, lexeme.toUpperCase(), pos));
            } else if (isIdentifierFA(lexeme)) {
                tokens.add(new Token(TokenType.IDENTIFIER, lexeme, pos));
            } else if (lexeme.matches("<>|<=|>=|=|[+\\-*/<>]")) {
                tokens.add(new Token(TokenType.OPERATOR, lexeme, pos));
            } else if (lexeme.matches("[(),]")) {
                tokens.add(new Token(TokenType.DELIMITER, lexeme, pos));
            } else {
                System.err.println("Unknown token: \"" + lexeme + "\" at position " + pos);
                tokens.add(new Token(TokenType.UNKNOWN, lexeme, pos));
            }
        }

        return tokens;
    }

    public static void main(String[] args) {
        String code = "LET X = 10\nIF X >= 10 THEN PRINT \"Hello\"\nREM This is a comment";

        List<Token> result = tokenize(code);
        for (Token token : result) {
            System.out.println(token);
        }
    }
}
