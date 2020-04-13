package us.master.acme_explorer.ui.available_trips;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import us.master.acme_explorer.FilterActivity;
import us.master.acme_explorer.R;
import us.master.acme_explorer.adapters.TripAdapter;
import us.master.acme_explorer.common.Util;

import static android.app.Activity.RESULT_OK;

public class AvailableTripsFragment extends Fragment {

    private static final String TAG = AvailableTripsFragment.class.getSimpleName();
    private static final int PICK_FILTER_LIST = 29;
    private RecyclerView myRecyclerView;
    private Switch mySwitch;
    private ViewGroup container;
    private TripAdapter tripAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trips_base_view,
                container, false);
        myRecyclerView = root.findViewById(R.id.my_trips_base_view_recyclerview);
        mySwitch = root.findViewById(R.id.my_trips_base_view_switch);
        RelativeLayout myLayout = root.findViewById(R.id.my_trips_base_view_filter);
        myLayout.setOnClickListener(getLayoutOnClickListener(container));
        this.container = container;
        this.tripAdapter = new TripAdapter(Util.tripList, TAG,
                mySwitch.isChecked() ? 2 : 1, this.container.getContext());

        Util.setRecyclerView(container, mySwitch, myRecyclerView, tripAdapter);
        mySwitch.setOnClickListener(
                V -> Util.setRecyclerView(container, mySwitch, myRecyclerView, tripAdapter)
        );

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILTER_LIST)
            if (resultCode == RESULT_OK)
                Log.d(TAG, "onActivityResult: " + requestCode + data + resultCode);
        tripAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        mySwitch.setChecked(false);
//        Util.setRecyclerView(this.container, mySwitch, myRecyclerView, tripAdapter);
    }


    private View.OnClickListener getLayoutOnClickListener(ViewGroup container) {
        return v -> {
            Log.d("onCreateView: ", "");
            Toast.makeText(container.getContext(), "filter", Toast.LENGTH_SHORT).show();
            startActivityForResult(
                    new Intent(container.getContext(), FilterActivity.class), PICK_FILTER_LIST
            );
        };
    }
}
