package org.krendel.test.service.impl;

import org.krendel.test.data.Word;
import org.krendel.test.data.WordRepository;
import org.krendel.test.model.WordSummary;
import org.krendel.test.model.Words;
import org.krendel.test.service.WordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implements the Word Service
 */
@Service("wordService")
public class WordServiceImpl implements WordService {
    private static final Logger LOG = LoggerFactory.getLogger(WordServiceImpl.class);
    private static final String DICTIONARY_FILE = "dictionary.txt";

    private final WordRepository wordRepository;

    private Map<String, Word> inMemoryCorpus = new ConcurrentHashMap<>(100000);

    @Autowired
    public WordServiceImpl(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @Override
    public void addWords(Words words) {
        for (String aWord : words.getWords()) {
            inMemoryCorpus.put(aWord, new Word(aWord));    // add to memory
        }

        // discover anagrams
        for (Word word : inMemoryCorpus.values()) {
            discoverAnagrams(word, true);
        }

        wordRepository.save(inMemoryCorpus.values());
    }

    @Override
    public List<Word> getAllWords() {
        return inMemoryCorpus.values().stream().collect(Collectors.toList());
    }

    @Override
    public Word getWord(String input) {
        return inMemoryCorpus.get(input);
    }

    @Override
    public List<String> getAnagrams(String input) {
        // work from the in-memory corpus
        if (!inMemoryCorpus.containsKey(input)) {
            // return empty list
            return new ArrayList<>();
            // throw new EntityNotFoundException(String.format("Word %s not found in corpus.", input));
        }

        return inMemoryCorpus.get(input).getAnagrams();
    }

    /**
     * load all words into memory to calculate anagrams, then persist into Redis
     */
    @Override
    public void initializeCorpus() throws IOException {
        if (wordRepository.count() > 0) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("++++ NO NEED TO INITIALIZE, COUNT: " + wordRepository.count());
            }
            loadCorpus();   // load corpus into memory
            return; // no need to initialize
        }

        LOG.info("**** INITIALIZING THE CORPUS!!! ****");
        // read words to memory
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                    this.getClass().getClassLoader().getResourceAsStream(DICTIONARY_FILE)));
        String line = reader.readLine();
        while (line != null) {
            inMemoryCorpus.put(line.trim(), null);
            line = reader.readLine();
        }
        reader.close();

        int batchSize = 1000;
        int start = 0;

        // find anagrams
        for (String dictionaryWord : inMemoryCorpus.keySet()) {
            Word newWord = new Word(dictionaryWord);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Trying to discover anagrams for: " + newWord.getWord());
            }

            discoverAnagrams(newWord, false);

            if (LOG.isDebugEnabled()) {
                if (newWord.getAnagrams().size() > 1) {
                    LOG.info("Anagrams: " + newWord.getAnagrams());
                }
            }

            Collection<Word> allWords = inMemoryCorpus.values();

            // persist to redis in batches
            // TODO: spawn a thread to do this
            if (allWords.size() % batchSize == 0) {
                doPersist(allWords, start++ * batchSize, batchSize);
            }
        }

        // persist the last batch
        doPersist(inMemoryCorpus.values(), start * batchSize, batchSize);
    }

    @Override
    public void loadCorpus() {
        if (wordRepository.count() <= 0) {
            return;
        }

        LOG.info("Reading Corpus into Memory... ");
        for (Word word : wordRepository.findAll()) {
            inMemoryCorpus.put(word.getWord(), word);
        }
        LOG.info("Reading Done...");
    }

    @Override
    public void deleteWord(String word, boolean deleteAnagrams) {
        Word aWord = getWord(word);
        if (aWord != null) {
            wordRepository.delete(aWord);
            inMemoryCorpus.remove(word);

            for (String anagram : aWord.getAnagrams()) {
                Word anagramWord = getWord(anagram);
                if (deleteAnagrams) {
                    if (anagramWord != null) {
                        wordRepository.delete(anagramWord);
                    }
                    inMemoryCorpus.remove(anagram);
                } else {
                    if (anagramWord != null) {
                        anagramWord.getAnagrams().remove(word);
                        wordRepository.save(anagramWord);
                    }
                    List<String> anagrams = inMemoryCorpus.get(anagram).getAnagrams();
                    if (anagrams != null) {
                        anagrams.remove(word);
                    }
                }
            }
        }
    }

    @Override
    public void deleteAllWords() {
        wordRepository.deleteAll();
        inMemoryCorpus.clear();
    }

    @Override
    public WordSummary getWordInfo() {
        // calculate number of words, min/max/median/avg length
        WordSummary summary = new WordSummary();

        if (inMemoryCorpus.size() == 0) {
            return summary;
        }

        // count/min/max
        summary.setCount(inMemoryCorpus.size());
        List<Integer> lengths = inMemoryCorpus.keySet().stream().map(String::length).collect(Collectors.toList());
        summary.setMinLength(Collections.min(lengths));
        summary.setMaxLength(Collections.max(lengths));

        // calculate average
        int totalLength = 0;
        for (Integer length : lengths) {
            totalLength += length;
        }
        summary.setAvgLength((float) totalLength / (float) lengths.size());

        // calculate median
        float median;
        Collections.sort(lengths);
        if (lengths.size() % 2 != 0) {
            median = lengths.get(lengths.size() / 2);
        } else {
            median = (lengths.get(lengths.size() / 2 - 1) + lengths.get(lengths.size() / 2)) / (float) 2.0;
        }
        summary.setMedLength(median);

        return summary;
    }

    @Override
    public List<Word> getMostAnagrams(Integer groupSize) {
        List<Map.Entry<String, Word>> validEntries;

        // filter out words with no anagrams when getting max group size or groupSize is > 0
        if (groupSize == null || groupSize > 0) {
            validEntries = inMemoryCorpus.entrySet().stream()
                    .filter(e -> e.getValue() != null && e.getValue().getAnagrams().size() > 0)
                    .collect(Collectors.toList());
        } else {
            validEntries = inMemoryCorpus.entrySet().stream().collect(Collectors.toList());
        }

        int max = -1;
        if (groupSize == null) {
            for (Map.Entry<String, Word> entry : validEntries) {
                if (entry.getValue().getAnagrams().size() > max) {
                    max = entry.getValue().getAnagrams().size();
                }
            }
        }

        final int minGroupSize = groupSize != null ? groupSize : max;
        List<Word> mostAnagrams = validEntries.stream().filter(e -> e.getValue().getAnagrams().size() >= minGroupSize)
                .map(Map.Entry::getValue).collect(Collectors.toList());

        return mostAnagrams;
    }

    @Override
    public Boolean areAnagrams(Words words) {
        if (words == null || words.getWords() == null || words.getWords().size() <= 1) {
            return false;
        }

        List<Integer> lengths = words.getWords().stream().map(String::length).collect(Collectors.toList());
        if (Integer.compare(Collections.min(lengths), Collections.max(lengths)) != 0) {
            return false;
        }

        // enough to check the first word to see if all the others are anagrams
        final String baseWord = words.getWords().get(0);
        for (int i = 1; i < words.getWords().size(); i++) {
            if (!isAnagram(baseWord, words.getWords().get(i))) {
                return false;
            }
        }

        return true;
    }

    private void discoverAnagrams(Word word, boolean persist) {
        final String theWord = word.getWord();
        if (theWord.length() <= 1) {
            return; // no anagrams
        }

        // collect all words of same length
        List<String> anagrams = inMemoryCorpus.keySet().stream()
                .filter(w -> w.length() == theWord.length())
                .filter(w -> isAnagram(w, theWord))
                .collect(Collectors.toList());

        word.setAnagrams(anagrams);
        inMemoryCorpus.put(theWord, word);

        // loop through the anagrams and add this word as their anagram
        for (String anagram : anagrams) {
            Word existing = inMemoryCorpus.computeIfAbsent(anagram, k -> new Word(anagram));
            if (!existing.getAnagrams().contains(theWord)) {
                existing.addAnagram(theWord);
            }

            inMemoryCorpus.put(anagram, existing);

            if (persist) {
                // when initializing the corpus we need to also update records in the db
                Word existingWord = getWord(anagram);
                if (existingWord == null) {
                    existingWord = new Word(anagram);
                }
                if (!existingWord.getAnagrams().contains(theWord)) {
                    existingWord.getAnagrams().add(theWord);
                    wordRepository.save(existingWord);
                }
            }
        }
    }

    private long doPersist(Collection<Word> wordList, int start, int batchSize) {
        long startTime = System.currentTimeMillis();
        List<Word> slice = wordList.stream().skip(start).limit(batchSize).collect(Collectors.toList());
        wordRepository.save(slice);
        long elapsed = System.currentTimeMillis() - startTime;
        long count = slice.size();
        LOG.info("Persisted " + count + " words in " + elapsed + " milliseconds.");
        return count;
    }

    private boolean isAnagram(String word1, String word2) {
        if (word1.equals(word2)) {
            return false;
        }

        // anagram decisions are case-insensitive
        char[] a1 = word1.toLowerCase().toCharArray();
        char[] a2 = word2.toLowerCase().toCharArray();
        Arrays.sort(a1);
        Arrays.sort(a2);
        return Arrays.equals(a1, a2);
    }
}
