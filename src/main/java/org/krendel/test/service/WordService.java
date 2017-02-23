package org.krendel.test.service;

import org.krendel.test.data.Word;
import org.krendel.test.model.WordSummary;
import org.krendel.test.model.Words;

import java.io.IOException;
import java.util.List;

public interface WordService {
    /**
     * Add a set of words to the corpus
     */
    void addWords(Words words);

    /**
     * Return a list of all the words in the corpus
     */
    List<Word> getAllWords();

    /**
     * Return a word from the corpus
     */
    Word getWord(String input);

    /**
     * Return anagrams for a word
     */
    List<String> getAnagrams(String input);

    /**
     * Initialize (load) corpus from dictionary file, if not already initialized
     */
    void initializeCorpus() throws IOException;

    /**
     * Load the corpus into memory
     */
    void loadCorpus();

    /**
     * Delete a word from the corpus
     */
    void deleteWord(String word, boolean deleteAnagrams);

    /**
     * Delete all words from the corpous
     */
    void deleteAllWords();

    /**
     * Return summary information about the words in the corpus
     */
    WordSummary getWordInfo();

    /**
     * Return a list of words with the given number of anagrams, if the parameter is null, return words
     * with most number of anagrams.
     * @param groupSize
     */
    List<Word> getMostAnagrams(Integer groupSize);

    /**
     * Return whether all the words in the given list are anagrams of each other
     */
    Boolean areAnagrams(Words words);
}
