package notes.com.azri.applauncher;

import android.graphics.drawable.Drawable;

/**
 * Created by nirav on 21/02/16.
 */
public class Post {

    private CharSequence postTitle;

    private CharSequence postSubTitle;
    private Drawable icon;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public CharSequence getPostSubTitle() {
        return postSubTitle;
    }

    public CharSequence getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(CharSequence postTitle) {
        this.postTitle = postTitle;
    }

    public void setPostSubTitle(CharSequence postSubTitle) {
        this.postSubTitle = postSubTitle;
    }

    public Post(CharSequence postTitle, CharSequence postSubTitle , Drawable icon) {
        this.postTitle = postTitle;
        this.postSubTitle = postSubTitle;
        this.icon = icon;
    }
}
