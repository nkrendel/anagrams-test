package org.krendel.test.controllers;

import org.krendel.test.data.Word;
import org.krendel.test.model.Words;
import org.krendel.test.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/words")
public class WordsController {
    private final WordService wordService;

    @Autowired
    public WordsController(WordService wordService) {
        this.wordService = wordService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity addWordsToCorpus(@RequestBody Words words) {
        wordService.addWords(words);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Word>> getAllWords() throws IOException {
        return ResponseEntity.ok(wordService.getAllWords());
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllWords() {
        wordService.deleteAllWords();
    }

    @RequestMapping(path = "/{word}", method = RequestMethod.DELETE)
    public ResponseEntity deleteWord(@PathVariable("word") String word,
                                     @RequestParam(value = "deleteAnagrams", required = false) boolean deleteAnagrams) {
        wordService.deleteWord(word, deleteAnagrams);
        return ResponseEntity.ok(null);
    }
}
