package io.github.tomykaira.accelvoice.projectdict.tokenizer

import java.util.regex.Pattern

abstract class AbstractTokenizer {
    private final List<String> extensions

    private final List<String> keywords

    private final List<Pattern> commentPatterns

    protected final Map<String, Integer> tokens = [:]

    def AbstractTokenizer(List<String> extensions, List<String> keywords, List<Pattern> commentPatterns) {
        this.extensions = extensions
        this.keywords = keywords
        this.commentPatterns = commentPatterns
    }

    public void tokenize(String code) {
        List<Character> buf = []
        commentPatterns.each { pattern ->
            code = code.replaceAll(pattern, '')
        }
        code.toCharArray().each { c ->
            if (buf.isEmpty() && isIdentStart(c) || !buf.isEmpty() && isIdentChar(c)) {
                buf.add(c)
            } else if (!buf.isEmpty()) {
                insert(buf)
            }
        }
        if (!buf.isEmpty()) {
            insert(buf)
        }
    }

    protected void insert(List<Character> buffer) {
        String token = buffer.join('')
        if (keywords.indexOf(token) == -1)
            tokens[token] = (tokens[token]?:0) + 1
        buffer.clear()
    }

    abstract boolean isIdentStart(char c);

    abstract boolean isIdentChar(char c);

    // Syntax suger to workaround groovy literal
    protected static char cc(String s) {
        s as char
    }

    protected static boolean isAlpha(char c) {
        cc('A') <= c && c <= cc('Z') || cc('a') <= c && c <= cc('z')
    }

    protected static boolean isDigit(char c) {
        cc('0') <= c && c <= cc('9')
    }
}
