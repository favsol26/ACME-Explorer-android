package us.master.acme_explorer.ui.log_in;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import us.master.acme_explorer.MainActivity;
import us.master.acme_explorer.R;

public class LogInFragment extends Fragment {
    private static final String TAG = LogInFragment.class.getSimpleName();
//    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        this.context = container.getContext();
        View root = inflater.inflate(R.layout.fragment_log_in, container, false);
        Button button = root.findViewById(R.id.login_button_email);
        button.setOnClickListener(V -> {
            startActivity(new Intent(container.getContext(), MainActivity.class));
            requireActivity().finish();
        });
        Log.d(TAG, "onCreateView: log in");
        return root;
    }


}
