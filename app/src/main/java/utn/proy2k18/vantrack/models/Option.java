package utn.proy2k18.vantrack.models;

import android.view.View;

public class Option {

    private String title;
    private int image;
    private View.OnClickListener onClickListener;

    public Option(String title, int image, View.OnClickListener onClickListener) {
        this.title = title;
        this.image = image;
        this.onClickListener = onClickListener;
    }

    public String getTitle() {
        return title;
    }

    public int getImage() {
        return image;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }
}