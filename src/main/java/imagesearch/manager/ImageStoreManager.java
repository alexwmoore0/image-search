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

@Component
public class ImageStoreManager {

    @Autowired private ClarifaiClient clarifaiClient;
    @Autowired private ImageFileManager imageFileManager;
    @Autowired private ImageStoreDao imageStoreDao;

    // useful for debugging when you don't want to start up and read all 1000 images
    private static final int IMAGE_COUNT = 10;

    @PostConstruct
    public void startClassification() {
        boolean skipClassify = Boolean.parseBoolean(System.getProperty("skipClassify"));

        if (skipClassify) {

        } else {
            classify();
        }
    }

    private void classify() {
        System.out.println("\nStarting classification...\n");

        imageFileManager.getImageUrls().parallelStream().limit(IMAGE_COUNT).forEach(imageUrl -> {
            List<ClarifaiOutput<Concept>> outputs = getOutput(imageUrl);

            outputs.forEach(output ->
                    output.data().forEach(concept -> {
                        ImageResult imageResult = new ImageResult(imageUrl, concept.value(), concept.name());
                        System.out.print(concept.name() + ", ");
                        imageStoreDao.put(imageResult);
                    })
            );
        });

        System.out.println("\n\nFinished classification!\n\n");

//        int pageCount = 0;
//
//        List<Set<ClarifaiInput>> inputPages = Lists.newArrayList();
//        inputPages.add(Sets.newHashSet());
//
//        for (String imageUrl : imageFileManager.getImageUrls()) {
//            Set<ClarifaiInput> currentPage = inputPages.get(pageCount);
//
//            if (currentPage.size() >= MAX_INPUT) {
//                pageCount++;
//                inputPages.add(Sets.newHashSet());
//                currentPage = inputPages.get(pageCount);
//            }
//
//            currentPage.add(ClarifaiInput.forImage(imageUrl));
//        }
//
//        for (Set<ClarifaiInput> inputPage : inputPages) {
//            System.out.println(inputPage.size());
//            List<ClarifaiOutput<Concept>> outputs = getOutputs(inputPage);
//            outputs.forEach(output -> {
//                System.out.println(output.input().metadata());
//                output.data().forEach(concept -> {
//                    System.out.print(concept.name() + ", ");
//                   // imageStoreDao.put(concept.name(),));
//                });
//            });
//        }
    }



    private List<ClarifaiOutput<Concept>> getOutput(String imageUrl) {
        System.out.println("\nCalling Clarifai API for " + imageUrl);
        return clarifaiClient.getDefaultModels().generalModel()
                .predict()
                .withInputs(ClarifaiInput.forImage(imageUrl))
                .executeSync()
                .getOrThrow(new RuntimeException("Failed to predict image for " + imageUrl));
    }
}
