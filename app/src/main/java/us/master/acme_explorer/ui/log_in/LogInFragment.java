package us.master.acme_explorer.ui.log_in;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import us.master.acme_explorer.R;

public class LogInFragment extends Fragment {
    private static final String TAG = LogInFragment.class.getSimpleName();
//    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        this.context = container.getContext();
        View root = inflater.inflate(R.layout.fragment_log_in, container, false);

        TextView textView = root.findViewById(R.id.text_gallery);
        textView.setText(String.format("%s", "log in"));
        Log.d(TAG, "onCreateView: log in");
        return root;
    }
}
