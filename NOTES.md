# Notes

- Features you think would be useful to add to the API

    Possibly a way to load the corpus from a file or a url.  This would need to be asynchronous and support some sort of progress indicator.
    
- Implementation details (which data store you used, etc.)

    1. I decided to use Redis for this.  It's supported by Spring and it can store a hash.
    
        To make this easily accessible I deployed the app into Heroku, and used a Heroku Redis addon.  The problem with this 
    approach, however, is if you are running the app locally, the connection to Heroku Redis is insanely slow.  
    A better alternative when running locally is to run a local version of Redis.  The easiest way to do this is via Docker:
    
        ```docker run --name redis-local -d -p 6379:6379 redis:latest```
    
        Then just update ```application.properties``` to point to your local Redis instance.
        
    2. I documented the API using Swagger.  The Swagger file is a manually created JSON file that is located
    at ```src/main/resources/swagger.json```.  The SpringFox-UI library is used to generate a pretty UI from this
    file and serve it in the app.
    
        The Swagger UI is accessible via:  ```/swagger-ui.html```

- Limits on the length of words that can be stored or limits on the number of results that will be returned

    I think the only limit is the memory available to the JVM for storing words, and the memory available for the Redis server,
    as by default it's running with no persistence.
    
- Any edge cases you find while working on the project

    I was not sure what the purpose of the ```dictionary.txt``` file was, as the tests seem to delete the corpus after
    every test.
    
    I decided to use the dictionary file as sample data and to illustrate the api on a real world sample.  The problem with
    this is that, on my local machine, it takes roughly 45 minutes to ingest the dictionary file and persist it to a locally
    running Redis instance, so obviously this isn't a quick deployment.
    
    If you want to load the dictionary file locally, you need to do the following:

    1. Delete all words in the corpus (either via the DELETE /words api or via a redis client).
    2. Edit ```application.properties``` and set ```corpus.initialize``` to ```true```.
    3. Rebuild the app.  The corpus currently gets initialized in the Spring Test phase of the build.
    4. Run the app.  The corpus will now contain all the words in the dictionary file with their anagrams.
    
- Design overview and trade-offs you considered

    1. The first problem was how to discover anagrams.  At first I tried a brute-force approach, which would loop
    through all the words in the input, and, for each word, calculate all its permutations, and lookup each 
    permutation to see if it's a valid word.
    
        This seemed like a good idea in theory, but practically, when looking at permutations of a 9-letter word, for
    example, you're already looking at 362,880 words generated, which is already more than all the dictionary file.
    Needless to say this proved to be an unfeasible approach.

        I decided to find anagrams for a word by looking in the corpus for all words of the same length, converting
    both words to lowercase, sorting the letters, and comparing the two resulting words.  This proved much faster.
    
    2. The second design question was whether to do everything in memory, or to use some sort of persistence layer.
    
        The advantage of an in-memory-only model is that it's fairly quick.  The obvious drawback is that if you spend
    the time to ingest the dictionary file (roughly 30 minutes for in-memory-only model), if you restart the app all
    the data is gone and you would have to spend those 30 minutes again, while if you have a persistence
    layer you would only have to do that once, and an application restart would not have any impact.
    