package imagesearch.domain;

public class ImageResult {

    private final String imageUrl;
    private final double matchPercent;
    private final String concept;

    public ImageResult(String imageUrl, double matchPercent, String concept) {
        this.imageUrl = imageUrl;
        this.matchPercent = matchPercent;
        this.concept = concept;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getMatchPercent() {
        return matchPercent;
    }

    public String getConcept() {
        return this.concept;
    }
}
