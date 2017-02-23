package org.krendel.test.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of anagrams
 */
public class Anagrams {
    List<String> anagrams;

    public List<String> getAnagrams() {
        if (anagrams == null) {
            anagrams = new ArrayList<>();
        }

        return anagrams;
    }

    public void setAnagrams(List<String> anagrams) {
        this.anagrams = anagrams;
    }
}
