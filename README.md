Alex Moore's Clarifai Image Search

#Running the project

The project runs with Java 8.

You can build the jar with `./gradlew build`, then run with `java -jar build/libs/image-search.jar`,
or just `./gradlew bootRun` to build and run in one step.

If my hard-coded Clarifai API Key stops working (runs out of operations), you can also add

`-DclarifaiApiKey=your_key_here`

before the jar path, or after `bootRun`

This will start the server on port `8080`.

Once the app has started, navigate to `localhost:8080/index` in a modern browser.


#Thoughts and reflections

Reading the file and calling the Clarifai API went fairly well, although I faced some issues getting
the bulk API to work, so right now the application makes individual, synchronous calls for every image source.
However, I'm using Java 8's parallel streaming to do this step, which will spawn a thread for each core on the
machine. On my machine, the application takes about 3 minutes to perform these 1000 calls and place the
results into memory.

I also implemented a caching layer to make subsequent search requests not have to search all keys of the
image store if the request has already happened.


#Downfalls:

The server only reads in and classifies images on start-up, so if anyone ever wanted to add more images for searching,
the server has to be restarted with the new image URLs in the text file (which can take a while, as mentioned above).
This could be resolved with a new endpoint that accepts an image URL, then classifies it and stores it in the in-memory
store, while also performing some sort of cache invalidation for the image's concepts.


#Future work

- Use async Clarifai client
- Allow for real-time image classifications (makes caching more difficult)
- Improve UI (loading spinners, adjustable image count, adjustable probability threshold)