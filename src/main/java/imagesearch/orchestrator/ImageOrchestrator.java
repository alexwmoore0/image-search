package imagesearch.orchestrator;

import imagesearch.cache.ImageStoreCache;
import imagesearch.domain.ImageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

@Component
public class ImageOrchestrator {

    @Autowired private ImageStoreCache imageStoreCache;

    public List<ImageResult> getImages(String query, int limit) {
        return imageStoreCache.getImages(query).stream()
                .sorted((image1, image2) -> Double.compare(image2.getMatchPercent(), image1.getMatchPercent())) // sort first
                .filter(distinctByKey(ImageResult::getImageUrl)) // get distinct image URLs
                .limit(limit)
                .collect(toList());
    }

    // borrowed from https://stackoverflow.com/a/27872852
    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}
