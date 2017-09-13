package imagesearch.classifier;

import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.ClarifaiStatus;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import imagesearch.dao.ImageStoreDao;
import imagesearch.domain.ImageResult;
import imagesearch.manager.ImageFileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Component
public class ImageClassifier {

    @Autowired private ClarifaiClient clarifaiClient;
    @Autowired private ImageFileManager imageFileManager;
    @Autowired private ImageStoreDao imageStoreDao;

    private static final double API_RATE_LIMIT = 10.0; // per second
    private RateLimiter rateLimiter = RateLimiter.create(API_RATE_LIMIT);

    @PostConstruct
    public void startClassification() {
        System.out.println("\nStarting classification...\n");

        imageFileManager.getImageUrls().parallelStream().forEach(imageUrl -> {
            rateLimiter.acquire();

            List<ClarifaiOutput<Concept>> predictions = predictImage(imageUrl);

            predictions.forEach(prediction ->
                prediction.data().forEach(concept -> {
                    // ensure concept name is not null
                    Optional.ofNullable(concept.name()).ifPresent(conceptName -> {
                        String nameLower = conceptName.toLowerCase();
                        ImageResult imageResult = new ImageResult(imageUrl, concept.value(), nameLower);
                        System.out.print(nameLower + ", ");
                        imageStoreDao.put(imageResult);
                    });
                })
            );
        });

        System.out.println("\n\nFinished classification!");
    }

    private List<ClarifaiOutput<Concept>> predictImage(String imageUrl) {
        System.out.println("\nCalling Clarifai API for " + imageUrl);
        ClarifaiResponse<List<ClarifaiOutput<Concept>>> response = clarifaiClient.getDefaultModels().generalModel()
                .predict()
                .withInputs(ClarifaiInput.forImage(imageUrl).withID(imageUrl))
                .executeSync();

        if (response.isSuccessful()) {
            return response.get();
        } else {
            ClarifaiStatus status = response.getStatus();
            System.out.println("ERROR for " + imageUrl +
                    "  description: " + status.description() +
                    "  details: " + status.errorDetails() +
                    "  response code: " + response.responseCode());
            return Lists.newArrayList();
        }
    }
}
