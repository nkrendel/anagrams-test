package org.krendel.test.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.krendel.test.data.Word;
import org.krendel.test.data.WordRepository;
import org.krendel.test.model.WordSummary;
import org.krendel.test.model.Words;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Test AnagramsController
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class WordServiceImplTest {
    @Mock
    private WordRepository wordRepository;

    private WordServiceImpl wordService;

    @Before
    public void setUp() {
        wordService = new WordServiceImpl(wordRepository);
    }

    @Test
    public void testThatAddWordsWorks() {
        Words words = new Words();
        words.setWords(Arrays.asList("aaa", "bbb", "ccd"));

        int initialCount = wordService.getAllWords().size();

        // add the words
        wordService.addWords(words);

        // compare numbers
        int finalCount = wordService.getAllWords().size();
        assertEquals(3, finalCount - initialCount);
    }

    @Test
    public void testThatDeleteallWorks() {
        Words words = new Words();
        words.setWords(Arrays.asList("aaa", "bbb", "ccd"));

        // add some words
        wordService.addWords(words);

        // delete all words
        wordService.deleteAllWords();

        assertEquals(0, wordService.getAllWords().size());
    }

    @Test
    public void testThatAnagramsAreDiscovered() {
        wordService.deleteAllWords();   // start with empty corpus

        Words words = new Words();
        words.setWords(Arrays.asList("arm", "ram"));

        // add the words
        wordService.addWords(words);

        // verify that anagrams were discovered
        List<Word> allWords = wordService.getAllWords();

        assertEquals(2, allWords.size());
        assertEquals(1, allWords.get(0).getAnagrams().size());
        assertEquals(1, allWords.get(1).getAnagrams().size());

        // add another word
        words.setWords(Arrays.asList("mar"));

        wordService.addWords(words);

        // verify that all words were updated
        allWords = wordService.getAllWords();
        assertEquals(3, allWords.size());
        assertEquals(2, allWords.get(0).getAnagrams().size());
        assertEquals(2, allWords.get(1).getAnagrams().size());
        assertEquals(2, allWords.get(2).getAnagrams().size());
    }

    @Test
    public void testThatGetWordWorks() {
        wordService.deleteAllWords();   // start with empty corpus

        Words words = new Words();
        words.setWords(Arrays.asList("arm", "ram"));

        // add the words
        wordService.addWords(words);

        Word arm = wordService.getWord("arm");

        assertNotNull(arm);
        assertEquals("arm", arm.getWord());
        assertEquals(1, arm.getAnagrams().size());
        assertEquals("ram", arm.getAnagrams().get(0));
    }

    @Test
    public void testThatGetAnagramsWorks() {
        wordService.deleteAllWords();   // start with empty corpus

        Words words = new Words();
        words.setWords(Arrays.asList("arm", "ram", "mar"));

        // add the words
        wordService.addWords(words);

        List<String> anagrams = wordService.getAnagrams("arm");

        assertNotNull(anagrams);
        assertEquals(2, anagrams.size());
        assertTrue(anagrams.contains("ram"));
    }

    @Test
    public void testLoadCorpus() {
        List<Word> wordList = getWordList();
        when(wordRepository.count()).thenReturn((long) wordList.size());
        when(wordRepository.findAll()).thenReturn(wordList);

        wordService.deleteAllWords();   // start with empty corpus

        wordService.loadCorpus();

        assertEquals(wordList.size(), wordService.getAllWords().size());
    }

    @Test
    public void testWordDeleted() {
        wordService.deleteAllWords();   // start with empty corpus

        Words words = new Words();
        words.setWords(Arrays.asList("arm", "ram", "mar"));

        // add the words
        wordService.addWords(words);

        wordService.deleteWord("arm", false);

        assertEquals(2, wordService.getAllWords().size());
    }

    @Test
    public void testWordAndAnagramsDeleted() {
        wordService.deleteAllWords();   // start with empty corpus

        Words words = new Words();
        words.setWords(Arrays.asList("arm", "ram", "mar"));

        // add the words
        wordService.addWords(words);

        wordService.deleteWord("arm", true);

        assertEquals(0, wordService.getAllWords().size());
    }

    @Test
    public void testWordSummaryCalculations() {
        wordService.deleteAllWords();   // start with empty corpus

        Words words = new Words();
        words.setWords(Arrays.asList("arm", "ram", "mar", "roam", "moar", "amor"));

        // add the words
        wordService.addWords(words);

        WordSummary summary = wordService.getWordInfo();

        assertNotNull(summary);
        assertEquals(6, summary.getCount());
        assertEquals(3, summary.getMinLength());
        assertEquals(4, summary.getMaxLength());
        assertEquals((float) 3.5, summary.getAvgLength(), 0.00001);
        assertEquals((float) 3.5, summary.getMedLength(), 0.00001);

    }

    @Test
    public void testMostAnagramsWithNoGroupSize() {
        wordService.deleteAllWords();   // start with empty corpus

        Words words = new Words();
        words.setWords(Arrays.asList("arm", "ram", "mar", "roam", "moar", "amor", "maro"));

        // add the words
        wordService.addWords(words);

        List<Word> mostAnagrams = wordService.getMostAnagrams(null);

        assertNotNull(mostAnagrams);
        assertEquals(4, mostAnagrams.size());
        assertEquals(3, mostAnagrams.get(0).getAnagrams().size());
    }

    @Test
    public void testMostAnagramsWithGroupSize() {
        wordService.deleteAllWords();   // start with empty corpus

        Words words = new Words();
        words.setWords(Arrays.asList("arm", "ram", "mar", "roam", "moar", "amor", "maro"));

        // add the words
        wordService.addWords(words);

        List<Word> mostAnagrams = wordService.getMostAnagrams(2);

        assertNotNull(mostAnagrams);
        assertEquals(7, mostAnagrams.size());
    }

    @Test
    public void testFindingIfWordsAreAnagrams() {
        Words set1 = new Words();
        set1.setWords(Arrays.asList("yarn", "nary", "rayn"));
        Words set2 = new Words();
        set2.setWords(Arrays.asList("yarn", "nary", "rain"));

        Boolean b1 = wordService.areAnagrams(set1);
        Boolean b2 = wordService.areAnagrams(set2);

        assertTrue(b1);
        assertFalse(b2);
    }

    /**
     * Return a dummy list of Words
     */
    private List<Word> getWordList() {
        Word arm = new Word("arm");
        Word ram = new Word("ram");
        Word mar = new Word("mar");
        arm.setAnagrams(Arrays.asList("ram", "mar"));
        ram.setAnagrams(Arrays.asList("arm", "mar"));
        mar.setAnagrams(Arrays.asList("arm", "ram"));

        return Arrays.asList(arm, ram, mar);
    }
}
