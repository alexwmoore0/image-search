package imagesearch.dao;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import imagesearch.domain.ImageResult;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
public class ImageStoreDao {

    private Map<String, Set<ImageResult>> imagesByConcept = Maps.newConcurrentMap();

    public void put(ImageResult image) {
        imagesByConcept.merge(image.getConcept(), Sets.newHashSet(image), Sets::union);
    }

    public Set<ImageResult> get(String concept) {
        return imagesByConcept.getOrDefault(concept, Sets.newHashSet());
    }

    public Set<ImageResult> search(String query) {
        Set<String> matchingKeys = imagesByConcept.keySet().stream()
                .filter(key -> key.startsWith(query))
                .collect(toSet());

        return matchingKeys.parallelStream().map(imagesByConcept::get)
                .flatMap(Set::stream)
                .collect(toSet());
    }
}
