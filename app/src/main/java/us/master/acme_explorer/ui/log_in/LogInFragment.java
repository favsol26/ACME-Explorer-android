package us.master.acme_explorer.ui.log_in;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import us.master.acme_explorer.MainActivity;
import us.master.acme_explorer.R;
import us.master.acme_explorer.common.Util;

public class LogInFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = LogInFragment.class.getSimpleName();
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = container.getContext();
        View root = inflater.inflate(R.layout.fragment_log_in, container, false);
        Button mBtnLogin = root.findViewById(R.id.login_button_email);
        Button nBtnGoogle = root.findViewById(R.id.login_button_google);
        Button mBtnSignUp = root.findViewById(R.id.login_button_sign_up);
        mBtnLogin.setOnClickListener(this);
        mBtnSignUp.setOnClickListener(this);
        nBtnGoogle.setOnClickListener(this);
        Log.d(TAG, "onCreateView: log in");
        return root;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button_email:
                startActivity(new Intent(context, MainActivity.class));
                requireActivity().finish();
                break;
            case R.id.login_button_google:
                Toast.makeText(context, "google", Toast.LENGTH_SHORT).show();
                break;
            case R.id.login_button_sign_up:
                Util.navigateTo(v, R.id.action_nav_log_in_fragment_to_nav_sign_up_fragment, null);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }
}
