package org.krendel.test.controllers;

import org.krendel.test.data.Word;
import org.krendel.test.model.WordSummary;
import org.krendel.test.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatsController {
    private final WordService wordService;

    @Autowired
    public StatsController(WordService wordService) {
        this.wordService = wordService;
    }

    @RequestMapping(path = "/words", method = RequestMethod.GET)
    public ResponseEntity<WordSummary> getWordInfo() throws IOException {
        WordSummary wInfo = wordService.getWordInfo();
        return ResponseEntity.ok(wInfo);
    }

    @RequestMapping(path = "/anagrams", method = RequestMethod.GET)
    public ResponseEntity<List<Word>> getAnagramInfo() throws IOException {
        List<Word> words = wordService.getMostAnagrams(null);
        return ResponseEntity.ok(words);
    }
}
