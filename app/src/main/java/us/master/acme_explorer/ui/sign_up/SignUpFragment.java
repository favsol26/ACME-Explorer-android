package us.master.acme_explorer.ui.sign_up;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import us.master.acme_explorer.R;

import static java.util.Objects.requireNonNull;
import static us.master.acme_explorer.common.Util.checkInstance;
import static us.master.acme_explorer.common.Util.mAuth;
import static us.master.acme_explorer.common.Util.mTxtChdLnr;
import static us.master.acme_explorer.common.Util.navigateTo;

public class SignUpFragment extends Fragment {

    private static final String TAG = SignUpFragment.class.getSimpleName();

    private TextInputLayout mTxtIptLytEmail;
    private TextInputLayout mTxtIptLytPassword;
    private TextInputLayout mTxtIptLytConfirmPassword;
    private TextInputEditText mIptEdtTxtEmail;
    private TextInputEditText mIptEdtTxtPassword;
    private TextInputEditText mIptEdtTxtConfirmPassword;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sign_up, container, false);
        setView(root);

        root.findViewById(R.id.sign_up_button_log_in).setOnClickListener(v ->
                navigateTo(v, R.id.action_nav_sign_up_to_nav_log_in, null));

        root.findViewById(R.id.sign_up_button).setOnClickListener(v -> createUser());
        return root;
    }

    private void setView(View root) {
        mTxtIptLytEmail = root.findViewById(R.id.sign_up_email);
        mTxtIptLytPassword = root.findViewById(R.id.sign_up_password);
        mIptEdtTxtEmail = root.findViewById(R.id.sign_up_email_et);
        mIptEdtTxtPassword = root.findViewById(R.id.sign_up_password_et);
        mTxtIptLytConfirmPassword = root.findViewById(R.id.sign_up_confirm_password);
        mIptEdtTxtConfirmPassword = root.findViewById(R.id.sign_up_confirm_password_et);

        mIptEdtTxtEmail.addTextChangedListener(mTxtChdLnr(mTxtIptLytEmail));
        mIptEdtTxtPassword.addTextChangedListener(mTxtChdLnr(mTxtIptLytPassword));
        mIptEdtTxtConfirmPassword.addTextChangedListener(mTxtChdLnr(mTxtIptLytConfirmPassword));
    }

    private void createUser() {
        Log.d(TAG, "createUser: ");
        String email = requireNonNull(mIptEdtTxtEmail.getText()).toString();
        String password = requireNonNull(mIptEdtTxtPassword.getText()).toString();
        String confirmPassword = requireNonNull(mIptEdtTxtConfirmPassword.getText()).toString();

        if (validateForm(email, password, confirmPassword)) {
            checkInstance();
            if (mAuth != null) {
//                mAuth.createUserWithEmailAndPassword(email, password);
                Log.d(TAG, String.format("createUser: e: %s p: %s cp: %s ",
                        email, password, confirmPassword));
            }
        } else Log.d(TAG, String.format("createUser: e: %s p: %s cp: %s ",
                email, password, confirmPassword));

    }

    private boolean validateForm(String email, String password, String confirmPassword) {
        boolean valid = true;
        if (TextUtils.isEmpty(email)) {
            mTxtIptLytEmail.setError("Required.");
            valid = false;
        } else {
            mTxtIptLytEmail.setError(null);
        }
        if (TextUtils.isEmpty(password)) {
            mTxtIptLytPassword.setError("Required.");
            valid = false;
        } else {
            if (!Objects.equals(password, confirmPassword)) {
                mTxtIptLytPassword.setError("Passwords aren't the same");
                mTxtIptLytConfirmPassword.setError("Passwords aren't the same");
                valid = false;
            } else {
                mTxtIptLytConfirmPassword.setError(null);
            }
            mTxtIptLytPassword.setError(null);
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            mTxtIptLytConfirmPassword.setError("Required.");
            valid = false;
        } else {
            mTxtIptLytConfirmPassword.setError(null);
        }
        return valid;
    }
}
