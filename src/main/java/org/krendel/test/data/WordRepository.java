package org.krendel.test.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Redis repository of words and anagrams
 */
@Repository
public interface WordRepository extends CrudRepository<Word, String> {
}
