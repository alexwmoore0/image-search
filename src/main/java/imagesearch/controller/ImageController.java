package imagesearch.controller;

import imagesearch.domain.ImageResult;
import imagesearch.orchestrator.ImageOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ImageController {

    @Autowired private ImageOrchestrator imageOrchestrator;

    @RequestMapping(value = "/api/v1/images", method = RequestMethod.GET)
    public List<ImageResult> getImages(
            @RequestParam(value = "concept") String concept,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        return imageOrchestrator.getImages(concept.toLowerCase(), limit);
    }
}
