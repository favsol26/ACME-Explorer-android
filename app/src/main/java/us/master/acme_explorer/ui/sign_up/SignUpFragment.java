package us.master.acme_explorer.ui.sign_up;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.regex.Pattern;

import us.master.acme_explorer.R;
import us.master.acme_explorer.common.Constants;

import static java.util.Objects.requireNonNull;
import static us.master.acme_explorer.common.Util.checkInstance;
import static us.master.acme_explorer.common.Util.mAuth;
import static us.master.acme_explorer.common.Util.mTxtChdLnr;
import static us.master.acme_explorer.common.Util.navigateTo;
import static us.master.acme_explorer.common.Util.showTransitionForm;

public class SignUpFragment extends Fragment {
    //    private static final String TAG = SignUpFragment.class.getSimpleName();
    private TextInputLayout mTxtIptLytEmail;
    private TextInputLayout mTxtIptLytPassword;
    private TextInputLayout mTxtIptLytConfirmPassword;
    private TextInputEditText mIptEdtTxtEmail;
    private TextInputEditText mIptEdtTxtPassword;
    private ProgressBar mProgressBar;
    private LinearLayout mLayoutForm;
    private TextInputEditText mIptEdtTxtConfirmPassword;
    private ViewGroup container;
    private Button mSignUpButton;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.container = container;
        View root = inflater.inflate(R.layout.fragment_sign_up, container, false);
        setView(root);

        root.findViewById(R.id.sign_up_button_log_in).setOnClickListener(v ->
                navigateTo(v, R.id.action_nav_sign_up_to_nav_log_in, null));

        mSignUpButton.setOnClickListener(v -> createUser());
        return root;
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

        mIptEdtTxtEmail.setText(requireArguments().getString(Constants.newEmail));
        mSignUpButton.setText(R.string.sign_in);
    }

    private void createUser() {
        String email = mIptEdtTxtEmail.getEditableText().toString();
        String password = mIptEdtTxtPassword.getEditableText().toString();
        String confirmPassword = mIptEdtTxtConfirmPassword.getEditableText().toString();

        if (validateForm(email, password, confirmPassword)) {
            showTransitionForm(false, container, mProgressBar, mLayoutForm);
            checkInstance();
            if (mAuth != null) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this::onComplete);
            }
        }
    }

    private boolean validateForm(String... dataToValidate) {
        boolean valid = true;
        if (TextUtils.isEmpty(dataToValidate[0])) {
            mTxtIptLytEmail.setError(getString(R.string.required_field));
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(dataToValidate[0]).matches()) {
            mTxtIptLytEmail.setError(getString(R.string.email_badly_formatted_error));
            valid = false;
        }
        if (TextUtils.isEmpty(dataToValidate[1])) {
            mTxtIptLytPassword.setError(getString(R.string.required_field));
            valid = false;
        } else if (!Pattern.matches(Constants.regExPassword, dataToValidate[1])) {
            mTxtIptLytPassword.setError(checkRegEx(dataToValidate[1]));
            valid = false;
        } else if (!Objects.equals(dataToValidate[1], dataToValidate[2])
                && !(TextUtils.isEmpty(dataToValidate[2]))) {
            mTxtIptLytPassword.setError(getString(R.string.passwords_do_not_match));
            mTxtIptLytConfirmPassword.setError(getString(R.string.passwords_do_not_match));
            valid = false;
        }
        if (TextUtils.isEmpty(dataToValidate[2])) {
            mTxtIptLytConfirmPassword.setError(getString(R.string.required_field));
            valid = false;
        }
        return valid;
    }

    private String checkRegEx(String password) {
        StringBuilder error = new StringBuilder();
        error.append(getString(R.string.password_requirements));

        if (password.length() < 6) error.append(getString(R.string.requirement_1));

        if (!Pattern.matches(".*[0-9].*", password))
            error.append(getString(R.string.requirement_2));

        if (!Pattern.matches(".*[A-Z].*", password))
            error.append(getString(R.string.requirement_3));

        if (!Pattern.matches(".*[@#$%^&+=!].*", password))
            error.append(getString(R.string.requirement_4));

        if (Pattern.matches(".*\\s.*", password))
            error.append(getString(R.string.requirement_5));
        error.setCharAt(error.lastIndexOf(","), '.');
        return String.valueOf(error);
    }

    private void onComplete(Task<AuthResult> task) {
        if (task.isSuccessful()) {
            Toast.makeText(requireContext(), R.string.user_create_message,
                    Toast.LENGTH_LONG).show();
            FirebaseUser user = requireNonNull(task.getResult()).getUser();
            assert user != null;
            user.sendEmailVerification().addOnCompleteListener(task1 -> {
                if (task1.isSuccessful())
                    Toast.makeText(requireContext(), R.string.verification_mail_sent,
                            Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                new Handler().postDelayed(() -> navigateTo(mIptEdtTxtConfirmPassword,
                        R.id.action_nav_sign_up_to_nav_log_in, null), 1000);
            });
        } else {
            Toast.makeText(requireContext(),
                    R.string.user_create_error, Toast.LENGTH_LONG).show();
        }
        showTransitionForm(true, this.container, mProgressBar, mLayoutForm);
    }
}
