package imagesearch.manager;

import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import imagesearch.dao.ImageStoreDao;
import imagesearch.domain.ImageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Component
public class ImageStoreManager {

    @Autowired private ClarifaiClient clarifaiClient;
    @Autowired private ImageFileManager imageFileManager;
    @Autowired private ImageStoreDao imageStoreDao;

    // useful for debugging when you don't want to start up and read all 1000 images
    private static final int IMAGE_COUNT = 1000;

    @PostConstruct
    public void startClassification() {
        System.out.println("\nStarting classification...\n");

        imageFileManager.getImageUrls().parallelStream().limit(IMAGE_COUNT).forEach(imageUrl -> {
            List<ClarifaiOutput<Concept>> predictions = predictImage(imageUrl);

            predictions.forEach(prediction -> {
                // make sure input is not null before getting ID
                String imageId = Optional.ofNullable(prediction.input()).map(ClarifaiInput::id)
                        .orElse(imageUrl);

                prediction.data().forEach(concept -> {
                    Optional<String> conceptNameOpt = Optional.ofNullable(concept.name());

                    // ensure concept name is not null
                    conceptNameOpt.ifPresent(conceptName -> {
                        String nameLower = conceptName.toLowerCase();
                        ImageResult imageResult = new ImageResult(imageId, concept.value(), nameLower);
                        System.out.print(nameLower + ",");
                        imageStoreDao.put(imageResult);
                    });
                });
            });
        });

        System.out.println("\n\nFinished classification!");
    }

    private List<ClarifaiOutput<Concept>> predictImage(String imageUrl) {
        System.out.println("\nCalling Clarifai API for " + imageUrl);
        return clarifaiClient.getDefaultModels().generalModel()
                .predict()
                .withInputs(ClarifaiInput.forImage(imageUrl).withID(imageUrl)) // set ID to imageUrl
                .executeSync()
                .getOrThrow(new RuntimeException("Failed to predict image for " + imageUrl));
    }
}
