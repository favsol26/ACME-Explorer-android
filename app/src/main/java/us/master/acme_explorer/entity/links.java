package us.master.acme_explorer.entity;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import us.master.acme_explorer.R;
import us.master.acme_explorer.ui.gallery.GalleryFragment;
import us.master.acme_explorer.ui.slideshow.SlideshowFragment;

public class links {
    private String description;
    private int resourceImageView;
    private Fragment nextFragment;

    private links(String description, int resourceImageView, Fragment nextFragment) {
        this.description = description;
        this.resourceImageView = resourceImageView;
        this.nextFragment = nextFragment;
    }

    public static List<links> generateLinks() {
        List<links> linksList = new ArrayList<>();
        linksList.add(
                new links("Viajes disponibles ",
                        R.mipmap.ic_launcher,
                        new GalleryFragment()
                )
        );
        linksList.add(
                new links("Viajes seleccionados",
                        R.mipmap.ic_launcher_round,
                        new SlideshowFragment()));
        return linksList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getResourceImageView() {
        return resourceImageView;
    }

    public void setResourceImageView(int resourceImageView) {
        this.resourceImageView = resourceImageView;
    }

    public Fragment getNextFragment() {
        return nextFragment;
    }

    public void setNextFragment(Fragment nextFragment) {
        this.nextFragment = nextFragment;
    }
}
