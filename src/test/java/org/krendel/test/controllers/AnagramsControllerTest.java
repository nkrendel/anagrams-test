package org.krendel.test.controllers;

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
 * Test AnagramsController
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class AnagramsControllerTest {
    @Mock
    private WordService wordService;

    private AnagramsController controller;

    @Before
    public void setUp() {
        controller = new AnagramsController(wordService);
    }

    @Test
    public void testGetanagramsForWordWithNegativeLimit() {
        List<String> wordList = Arrays.asList("arm", "mar", "ram");
        when(wordService.getAnagrams(anyString())).thenReturn(wordList);

        ResponseEntity<Anagrams> response = controller.getAnagramsForWord("rma", -200, true);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getAnagrams());
        assertEquals(3, response.getBody().getAnagrams().size());
    }

    @Test
    public void testGetanagramsForWordWithPositiveLimit() {
        List<String> wordList = Arrays.asList("arm", "mar", "ram");
        when(wordService.getAnagrams(anyString())).thenReturn(wordList);

        ResponseEntity<Anagrams> response = controller.getAnagramsForWord("rma", 1, true);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getAnagrams());
        assertEquals(1, response.getBody().getAnagrams().size());
    }

    @Test
    public void testGetanagramsForWordWithProperNouns() {
        List<String> wordList = Arrays.asList("arm", "mar", "ram", "Ram");
        when(wordService.getAnagrams(anyString())).thenReturn(wordList);

        ResponseEntity<Anagrams> response = controller.getAnagramsForWord("rma", null, true);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getAnagrams());
        assertEquals(4, response.getBody().getAnagrams().size());
    }

    @Test
    public void testGetanagramsForWordWithoutProperNouns() {
        List<String> wordList = Arrays.asList("arm", "mar", "ram", "Ram");
        when(wordService.getAnagrams(anyString())).thenReturn(wordList);

        ResponseEntity<Anagrams> response = controller.getAnagramsForWord("rma", null, false);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getAnagrams());
        assertEquals(3, response.getBody().getAnagrams().size());
    }
}
