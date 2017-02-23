package org.krendel.test.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.krendel.test.data.Word;
import org.krendel.test.model.Anagrams;
import org.krendel.test.service.WordService;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Test Word class
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class WordTest {
    @Test
    public void testGetAnagramsOnNewWord() {
        Word word = new Word("word");
        assertNotNull(word.getAnagrams());
        assertEquals(0, word.getAnagrams().size());
    }

    @Test
    public void testAddAnagrams() {
        Word word = new Word("ram");
        word.addAnagram("mar");
        word.addAnagram("arm");
        assertEquals(2, word.getAnagrams().size());
    }
}
