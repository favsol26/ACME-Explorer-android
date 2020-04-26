package us.master.acme_explorer.entity;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import us.master.acme_explorer.R;
import us.master.acme_explorer.common.Constants;

public class links {
    private String description;
    private String imageURL;
    private int action;

    private links(String description, String imageURL, int action) {
        this.description = description;
        this.imageURL = imageURL;
        this.action = action;
    }

    public static List<links> generateLinks(Context context) {
        List<links> linksList = new ArrayList<>();

        linksList.add(new links(
                        context.getString(R.string.menu_available_trips),
                        Constants.urlImages[0],
                        R.id.nav_available_trips
                )
        );
        linksList.add(new links(
                        context.getString(R.string.menu_selected_trips),
                        Constants.urlImages[1],
                        R.id.nav_selected_trips
                )
        );
        return linksList;
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public int getAction() {
        return action;
    }
/*
    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setNextView(int nextView) {
        this.nextView = nextView;
    }
*/
}
