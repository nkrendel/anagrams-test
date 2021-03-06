package org.krendel.test;

import org.krendel.test.service.WordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent>{
    private static final Logger LOG = LoggerFactory.getLogger(ContextRefreshedListener.class);

    @Value("${corpus.initialize:true}")
    private boolean initializeCorpus;

    private final WordService wordService;

    @Autowired
    public ContextRefreshedListener(WordService wordService) {
        this.wordService = wordService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (initializeCorpus) {
            try {
                wordService.initializeCorpus(); // initialize (load) corpus if not already initialized
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        } else {
            wordService.loadCorpus();
        }
    }
}
