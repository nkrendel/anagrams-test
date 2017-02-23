package org.krendel.test.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a word and its anagrams
 */
@RedisHash("words")
public class Word {
    @Id
    String word;

    List<String> anagrams;


    public Word(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<String> getAnagrams() {
        if (anagrams == null) {
            anagrams = new ArrayList<>();
        }

        return anagrams;
    }

    public void setAnagrams(List<String> anagrams) {
        this.anagrams = anagrams;
    }

    public List<String> addAnagram(String anagram) {
        getAnagrams().add(anagram);
        return anagrams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Word word1 = (Word) o;

        if (word != null ? !word.equals(word1.word) : word1.word != null) return false;
        return anagrams != null ? anagrams.equals(word1.anagrams) : word1.anagrams == null;
    }

    @Override
    public int hashCode() {
        int result = word != null ? word.hashCode() : 0;
        result = 31 * result + (anagrams != null ? anagrams.hashCode() : 0);
        return result;
    }
}
