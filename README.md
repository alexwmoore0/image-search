Alex Moore's Clarifai Image Search

#Running the project

The project runs with Java 8.

Build the jar: `./gradlew build`

Run the jar: `java -jar -DclarifaiApiKey=your_key_here build/libs/image-search.jar`

Build and run in one fell swoop: `./gradlew bootRun -DclarifaiApiKey=your_key_here`

This will start the server on port `8080`.

Once the app has started, it will call the Clarifai API to predict the images, which should take a couple of minutes.

Once that is finished, navigate to `localhost:8080/index` in a modern browser.


#Thoughts and reflections

Reading the file and calling the Clarifai API went fairly well, although I faced some issues getting
the bulk API to work, so right now the application makes individual, synchronous calls for every image source.
However, I'm using Java 8's parallel streaming to do this step, which will spawn a thread for each core on the
machine. On my machine (quad-core), the application takes about 3 minutes to perform these 1000 calls and place the
results into memory.

I also implemented a very simple caching layer to make subsequent search requests not have to search all keys of the
image store if the request has already happened.


#Downfalls:

The server only reads in and classifies images on start-up, so if anyone ever wanted to add more images for searching,
the server has to be restarted with the new image URLs in the text file. This could be resolved with a new endpoint
that accepts an image URL, then classifies it and stores it in the in-memory store, while also performing some
sort of cache invalidation for the new image's concepts.


#Future work

- Better Clarifai API error-handling (retries, exponential back-off)
- Use async Clarifai client (rate-limiting becomes a little bit trickier)
- Allow for real-time image classifications (makes search-caching more difficult)
- Improve UI (loading spinners, adjustable image count, adjustable probability threshold, adjust search type)
- Use a more legitimate caching mechanism (TTL, invalidation)
- Use an actual logger
- Unit tests
- Functional tests