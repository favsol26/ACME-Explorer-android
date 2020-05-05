package us.master.acme_explorer.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import us.master.acme_explorer.R;

import static us.master.acme_explorer.common.Util.mTxtChdLnr;
import static us.master.acme_explorer.common.Util.navigateTo;

public class ProfileFragment extends Fragment {

    private TextInputLayout mTxtIptLytEmail;
    private TextInputLayout mTxtIptLytPassword;
    private TextInputLayout mTxtIptLytConfirmPassword;
    private TextInputEditText mIptEdtTxtEmail;
    private TextInputEditText mIptEdtTxtPassword;
    private ProgressBar mProgressBar;
    private LinearLayout mLayoutForm;
    private TextInputEditText mIptEdtTxtConfirmPassword;
    private ViewGroup container;
    private AppCompatButton mSignUpButton;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.container = container;
        View root = inflater.inflate(R.layout.fragment_sign_up, container, false);

        setView(root);

        root.findViewById(R.id.sign_up_button_log_in).setVisibility(View.GONE);
        root.findViewById(R.id.user_form_personal_data).setVisibility(View.VISIBLE);

        mSignUpButton.setOnClickListener(v -> saveUser());
        return root;
    }

    private void saveUser() {
        navigateTo(mProgressBar, R.id.nav_main_menu, null);
    }

    private void setView(View root) {
        mProgressBar = root.findViewById(R.id.sign_up_progress_bar);
        mLayoutForm = root.findViewById(R.id.sign_up_form);
        mTxtIptLytEmail = root.findViewById(R.id.user_form_email);
        mTxtIptLytPassword = root.findViewById(R.id.user_form_password);
        mIptEdtTxtEmail = root.findViewById(R.id.user_form_email_et);
        mIptEdtTxtPassword = root.findViewById(R.id.user_form_password_et);
        mTxtIptLytConfirmPassword = root.findViewById(R.id.user_form_confirm_password);
        mIptEdtTxtConfirmPassword = root.findViewById(R.id.user_form_confirm_password_et);
        mSignUpButton = root.findViewById(R.id.user_form_button);

        mIptEdtTxtEmail.addTextChangedListener(mTxtChdLnr(mTxtIptLytEmail));
        mIptEdtTxtPassword.addTextChangedListener(mTxtChdLnr(mTxtIptLytPassword));
        mIptEdtTxtConfirmPassword.addTextChangedListener(mTxtChdLnr(mTxtIptLytConfirmPassword));
        mSignUpButton.setText(R.string.save_user);
    }

}
