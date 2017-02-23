package org.krendel.test.controllers;

import org.krendel.test.data.Word;
import org.krendel.test.model.Anagrams;
import org.krendel.test.model.Words;
import org.krendel.test.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/anagrams")
public class AnagramsController {
    private final WordService wordService;

    @Autowired
    public AnagramsController(WordService wordService) {
        this.wordService = wordService;
    }


    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Word>> getAnagramGroups(@RequestParam(value = "minGroupSize") Integer minGroupSize) {
        List<Word> words = wordService.getMostAnagrams(minGroupSize);
        return ResponseEntity.ok(words);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Boolean> areWordsAnagrams(@RequestBody Words words) {
        return ResponseEntity.ok(wordService.areAnagrams(words));
    }

    @RequestMapping(path = "/{word}", method = RequestMethod.GET)
    public ResponseEntity<Anagrams> getAnagramsForWord(@PathVariable("word") String word,
              @RequestParam(value = "limit", required = false) Integer limit,
              @RequestParam(value = "includeProper", required  = false, defaultValue = "true") Boolean includeProper) {
        List<String> words = wordService.getAnagrams(word);
        Anagrams anagrams = new Anagrams();

        // filter proper nouns, if requests
        if (!includeProper) {
            words = words.stream().filter(w -> !(w.charAt(0) >= 'A' && w.charAt(0) <= 'Z')).collect(Collectors.toList());
        }

        // limit results, if requested (negative value indicates no limit)
        if (limit != null && limit >= 0) {
            anagrams.setAnagrams(words.stream().limit(limit).collect(Collectors.toList()));
        } else {
            anagrams.setAnagrams(words);
        }

        return ResponseEntity.ok(anagrams);
    }
}
