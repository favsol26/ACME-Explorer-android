package us.master.acme_explorer.ui.main_menu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import us.master.acme_explorer.R;
import us.master.acme_explorer.adapters.MenuAdapter;
import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.entity.Links;

import static android.content.Context.MODE_PRIVATE;

public class MainMenuFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main_menu, container, false);
        GridView mGridView = root.findViewById(R.id.my_grid_view_main_menu);

        MenuAdapter menuAdapter = new MenuAdapter(Links.generateLinks(container.getContext()),
                container.getContext());

        mGridView.setAdapter(menuAdapter);
        SharedPreferences sharedPreferences = container.getContext()
                .getSharedPreferences(Constants.filterPreferences, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.clear().apply();
        return root;
    }
}
