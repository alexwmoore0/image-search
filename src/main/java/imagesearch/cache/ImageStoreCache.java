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

    private Map<String, Set<ImageResult>> imageCache = Maps.newHashMap();

    public Set<ImageResult> getImages(String query) {
        if (imageCache.containsKey(query)) {
            return imageCache.get(query);
        }

        Set<ImageResult> results = imageStoreDao.search(query);
        imageCache.put(query, results);
        return results;
    }
}
