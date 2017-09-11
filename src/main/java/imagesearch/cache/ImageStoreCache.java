package imagesearch.cache;

import com.google.common.collect.Maps;
import imagesearch.dao.ImageStoreDao;
import imagesearch.domain.ImageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class ImageStoreCache {

    @Autowired private ImageStoreDao imageStoreDao;

    private Map<String, Set<ImageResult>> imagesByQuery = Maps.newConcurrentMap();

    public Set<ImageResult> getImages(String query) {
        if (imagesByQuery.containsKey(query)) {
            System.out.println("Cache hit: " + query);
            return imagesByQuery.get(query);
        }

        System.out.println("Cache miss: " + query);
        Set<ImageResult> matchingImages = imageStoreDao.search(query);
        imagesByQuery.put(query, matchingImages);
        return matchingImages;
    }
}
