package org.krendel.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "corpus.initialize=false")
public class AnagramsApplicationTests {

	@Test
	public void contextLoads() {
	}

}
