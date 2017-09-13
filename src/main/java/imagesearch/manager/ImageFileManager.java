package imagesearch.manager;

import com.google.common.collect.Sets;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

@Component
public class ImageFileManager {

    private static final String IMAGE_FILE = "images.txt";

    private Set<String> imageUrls = Sets.newHashSet();

    @PostConstruct
    private void init() {
        try (InputStream imageFileStream = new ClassPathResource(IMAGE_FILE).getInputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(imageFileStream));

            br.lines().forEach(imageUrls::add);

            System.out.println(String.format("Added %d images", imageUrls.size()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse file " + IMAGE_FILE);
        }
    }

    public Set<String> getImageUrls() {
        return imageUrls;
    }
}
