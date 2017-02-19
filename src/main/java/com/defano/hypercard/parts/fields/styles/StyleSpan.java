/*
 * StyleSpan
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.fields.styles;

import javax.swing.text.AttributeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StyleSpan {

    private final AttributeSet styleSet;
    private final int start, end;
    private final String text;
    private final Integer startChar, endChar;
    private final Integer startWord, endWord;

    StyleSpan(String text, int startChar, int endChar, AttributeSet styleSet) {
        this.styleSet = styleSet;
        this.start = startChar;
        this.end = endChar;
        this.text = text;

        // Start char is beginning of document
        if (startChar == 0) {
            this.startChar = 0;
            this.startWord = null;
        }

        // Start char is in whitespace between words
        else if (Character.isWhitespace(text.charAt(startChar))) {
            this.startChar = null;
            this.startWord = getWordAt(text, startChar) + 1;
        }

        // Start char is start of a word
        else if (Character.isWhitespace(text.charAt(startChar - 1))) {
            this.startChar = null;
            this.startWord = getWordAt(text, startChar);
        }

        // Start char is middle of word
        else {
            this.startChar = startChar;
            this.startWord = null;
        }

        // End char is at end of document
        if (endChar >= text.length() - 1) {
            this.endChar = Integer.MAX_VALUE;
            this.endWord = null;
        }

        // End char is between words
        else if (Character.isWhitespace(text.charAt(endChar))) {
            this.endChar = null;
            this.endWord = getWordAt(text, endChar);
        }

        // End char is last char in word
        else if (Character.isWhitespace(text.charAt(endChar + 1))) {
            this.endChar = null;
            this.endWord = getWordAt(text, endChar);
        }

        // End char is in middle of word
        else {
            this.endChar = endChar;
            this.endWord = null;
        }
    }

    public int getStartOfSpan(String text) {
        if (startChar != null) {
            return startChar;
        } else {
            return getStartIndexOfWord(text, startWord);
        }
    }

    public int getEndOfSpan(String text) {
        if (endChar != null) {
            return endChar < text.length() ? endChar : text.length();
        } else {
            return getEndIndexOfWord(text, endWord);
        }
    }

    public AttributeSet getStyleSet() {
        return styleSet;
    }

    public int getSpanLength(String text) {
        return getEndOfSpan(text) - getStartOfSpan(text);
    }

    private int getEndIndexOfWord(String text, int endWord) {
        try {
            Matcher wordMatcher = Pattern.compile("\\s+").matcher(text);
            while (endWord-- >= 0) {
                wordMatcher.find();
            }

            return wordMatcher.start();
        } catch (IllegalStateException e) {
            return text.length();
        }
    }

    private int getStartIndexOfWord(String text, int startWord) {
        try {
            Matcher wordMatcher = Pattern.compile("\\s+").matcher(text);
            while (--startWord >= 0) {
                wordMatcher.find();
            }

            return wordMatcher.end();
        } catch (IllegalStateException e) {
            return 0;
        }
    }

    private int getWordAt(String text, int charIndex) {
        int word = 0;

        for (int thisChar = 0; thisChar <= charIndex; thisChar++) {
            if (thisChar != 0 && Character.isWhitespace(text.charAt(thisChar - 1)) && !Character.isWhitespace(thisChar)) {
                word++;
            }
        }

        return word;
    }

}
