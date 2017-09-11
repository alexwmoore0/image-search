package imagesearch;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import okhttp3.OkHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
    }

    @Bean
    public ClarifaiClient clarifaiClient() {
        String apiKey = Optional.ofNullable(System.getProperty("clarifaiApiKey"))
                .orElse(""); // my API key

        System.out.println("Using Clarifai API Key: " + apiKey);

        return new ClarifaiBuilder(apiKey)
                .client(new OkHttpClient())
                .buildSync();
    }
}
