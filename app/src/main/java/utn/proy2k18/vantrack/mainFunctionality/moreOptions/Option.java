package utn.proy2k18.vantrack.mainFunctionality.moreOptions;

public class Option {

    private String title;
    private int image;

    public Option(String title, int image) {
        this.title = title;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public int getImage() {
        return image;
    }
}