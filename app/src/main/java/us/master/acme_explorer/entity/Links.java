package us.master.acme_explorer.entity;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import us.master.acme_explorer.R;
import us.master.acme_explorer.common.Constants;

public class Links {
    private String description;
    private String imageURL;
    private int action;

    private Links(String description, String imageURL, int action) {
        this.description = description;
        this.imageURL = imageURL;
        this.action = action;
    }

    public static List<Links> generateLinks(Context context) {
        List<Links> linksList = new ArrayList<>();

        linksList.add(new Links(
                        context.getString(R.string.menu_available_trips),
                        Constants.urlImages[0],
                        R.id.action_nav_main_menu_to_nav_available_trips
                )
        );
        linksList.add(new Links(
                        context.getString(R.string.menu_selected_trips),
                        Constants.urlImages[1],
                        R.id.action_nav_main_menu_to_nav_selected_trips
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
