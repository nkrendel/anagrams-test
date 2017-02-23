package org.krendel.test.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of words
 */
public class Words {
    List<String> words;

    public List<String> getWords() {
        if (words == null) {
            words = new ArrayList<>();
        }

        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }
}
