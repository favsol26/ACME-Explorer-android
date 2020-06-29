package us.master.acme_explorer.ui.log_in;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import us.master.acme_explorer.MainActivity;
import us.master.acme_explorer.R;
import us.master.acme_explorer.common.Constants;
import us.master.acme_explorer.common.Util;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;
import static java.util.Objects.requireNonNull;
import static us.master.acme_explorer.common.Util.checkInstance;
import static us.master.acme_explorer.common.Util.mAuth;
import static us.master.acme_explorer.common.Util.mTxtChdLnr;
import static us.master.acme_explorer.common.Util.showDialogMessage;
import static us.master.acme_explorer.common.Util.showTransitionForm;

public class LogInFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = LogInFragment.class.getSimpleName();
    private final int LOGIN_GOOGLE = 0x55;
    private LinearLayout mLayoutFormLogin;
    private ProgressBar mProgressBar;
    private TextInputLayout mTextInputLayoutEmail;
    private TextInputLayout mTextInputLayoutPassword;
    private TextInputEditText mInputEditTextEmail;
    private TextInputEditText mInputEditTextPassword;
    private Context context;
    private ViewGroup container;

    private void setView(View root) {
        Button mBtnLogin = root.findViewById(R.id.login_button_email);
        Button nBtnGoogle = root.findViewById(R.id.login_button_google);
        Button mBtnSignUp = root.findViewById(R.id.login_button_sign_up);
        mBtnLogin.setOnClickListener(this);
        mBtnSignUp.setOnClickListener(this);
        nBtnGoogle.setOnClickListener(this);

        mLayoutFormLogin = root.findViewById(R.id.login_form);
        mProgressBar = root.findViewById(R.id.login_progress_bar);
        mTextInputLayoutEmail = root.findViewById(R.id.login_email);
        mTextInputLayoutPassword = root.findViewById(R.id.login_password);

        mInputEditTextEmail = root.findViewById(R.id.login_email_et);
        mInputEditTextPassword = root.findViewById(R.id.login_password_et);
        mInputEditTextEmail.addTextChangedListener(mTxtChdLnr(mTextInputLayoutEmail));
        mInputEditTextPassword.addTextChangedListener(mTxtChdLnr(mTextInputLayoutPassword));
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_log_in, container, false);
        setView(root);
        this.container = container;
        this.context = container.getContext();
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button_email:
                attemptLogin();
                break;
            case R.id.login_button_google:
                attemptLoginGoogle(Util.googleSignInOptions);
                break;
            case R.id.login_button_sign_up:
                Bundle bundle = new Bundle();
                bundle.putString(Constants.newEmail, mInputEditTextEmail.getEditableText().toString());
                Util.navigateTo(v, R.id.action_nav_log_in_to_nav_sign_up, bundle);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == LOGIN_GOOGLE) {
            try {
                Task<GoogleSignInAccount> result = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = result.getResult(ApiException.class);
                assert account != null;
                AuthCredential credential
                        = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                checkInstance();
                if (mAuth != null)
                    mAuth.signInWithCredential(credential)
                            .addOnCompleteListener(requireActivity(), this::verifyTask);
                else googlePlayServicesError();
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed -> " + resultCode, e);
                showTransitionForm(true, container, mProgressBar, mLayoutFormLogin);
            }
        }
    }

    private void attemptLogin() {
        boolean user, pass;
        mTextInputLayoutEmail.setError(null);
        mTextInputLayoutPassword.setError(null);
        user = alertLoginUser(mTextInputLayoutEmail, mInputEditTextEmail);
        pass = alertLoginUser(mTextInputLayoutPassword, mInputEditTextPassword);

        if (user && pass)
            loginWithEmail(mInputEditTextEmail.getEditableText(),
                    mInputEditTextPassword.getEditableText());
//        else showLoginForm(true);
    }

    private void loginWithEmail(Editable email, Editable password) {
        checkInstance();
        if (mAuth != null) {
            showTransitionForm(false, container, mProgressBar, mLayoutFormLogin);
            mAuth.signInWithEmailAndPassword(email.toString(), password.toString())
                    .addOnCompleteListener(requireActivity(), this::verifyTask);
        } else {
            googlePlayServicesError();
        }
    }

    private void attemptLoginGoogle(GoogleSignInOptions googleSignInOptions) {
        showTransitionForm(false, container, mProgressBar, mLayoutFormLogin);
        GoogleSignInClient signIn = GoogleSignIn.getClient(context, googleSignInOptions);
        Intent signInIntent = signIn.getSignInIntent();
        startActivityForResult(signInIntent, LOGIN_GOOGLE);
    }

    private void verifyTask(Task<AuthResult> task) {
        try {
            AuthResult taskResult = requireNonNull(task.getResult());
            if (!task.isSuccessful() || taskResult.getUser() == null) {
                showErrorDialogMail();
            } else if (!taskResult.getUser().isEmailVerified()) {
                showErrorEmailVerified(taskResult.getUser());
            } else {
                FirebaseUser user = task.getResult().getUser();
                assert user != null;
                checkUserDataBaseLogin(user);
                Log.d(TAG, "verifyTask: " + user.getEmail());
            }
        } catch (Exception e) {
            showTransitionForm(true, container, mProgressBar, mLayoutFormLogin);
            showErrorMessage(task);
        }
    }

    private void showErrorMessage(Task<AuthResult> task) {
        int stringId = 0;
        int errCod = requireNonNull(requireNonNull(task.getException()).getMessage()).hashCode();
        Log.d(TAG, errCod + " loginWithEmail: " + task.getException().getMessage());
        switch (errCod) {
            case 787766007:
                mInputEditTextEmail.setError(context.getString(R.string.email_badly_formatted_error));
                break;
            case -1710700802:
                mInputEditTextPassword.setError(context.getString(R.string.wrong_password_error));
                break;
            case -1446840658:
                stringId = R.string.user_no_found_error;
                break;
            case -1501900565:
                stringId = R.string.device_temporal_blocked_error;
                break;
            case 1054830334:
                stringId = R.string.network_error;
                break;
            case -1760623302:
                stringId = R.string.user_disabled_error;
                break;
            default:
                Snackbar.make(mProgressBar, "unhandled error, code:" + errCod,
                        LENGTH_LONG).show();
        }
        if (stringId != 0) Util.mSnackBar(mProgressBar, context, stringId, LENGTH_LONG);
    }

    private void checkUserDataBaseLogin(FirebaseUser user) {
//        TODO complete
        Util.currentUser = user;
        Log.d(TAG, "checkUserDataBaseLogin: user -> " + user.getUid());
        loginSucceeded();
    }

    private void showErrorEmailVerified(FirebaseUser user) {
        DialogInterface.OnClickListener posBtn = (dialog, which) ->
                user.sendEmailVerification().addOnCompleteListener(task -> {
                    String text = context.getString(task.isSuccessful()
                            ? R.string.login_verified_mail_error_sent
                            : R.string.login_verified_mail_error_no_sent);
                    Snackbar.make(mProgressBar, text, LENGTH_SHORT).show();
                    showTransitionForm(true, container, mProgressBar, mLayoutFormLogin);
                }),
                negBtn = (dialog, which) -> showTransitionForm(true,
                        container, mProgressBar, mLayoutFormLogin);

        showDialogMessage(context,
                null, R.string.login_verified_mail_error,
                R.string.login_verified_mail_error_ok,
                R.string.login_verified_mail_error_cancel, posBtn, negBtn);
        showTransitionForm(true, container, mProgressBar, mLayoutFormLogin);
        mAuth.signOut();
    }

    private void showErrorDialogMail() {
        Util.mSnackBar(mProgressBar, context, R.string.login_mail_access_error, LENGTH_LONG);
    }

    private void googlePlayServicesError() {
        Snackbar.make(mProgressBar, context.getString(R.string.log_in_google_play_services_error),
                LENGTH_INDEFINITE).setAction(context.getString(R.string.log_in_download_gps),
                v -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(context.getString(R.string.gps_download_url))));
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(context.getString(R.string.market_download_url))));
                    }
                }).show();
    }

    private boolean alertLoginUser(TextInputLayout layout, TextInputEditText mTextView) {
        boolean validate =  mTextView.getEditableText().toString().length() < 1;
        if (validate) {
            layout.setErrorEnabled(true);
            layout.setError(context.getString(layout.getId() == R.id.login_email
                    ? R.string.login_error_message_1
                    : R.string.login_error_message_2));
        }
        return !validate;
    }

    private void loginSucceeded() {
        Util.mSnackBar(mProgressBar, context, R.string.welcome, LENGTH_SHORT);
        new Handler().postDelayed(() -> {
                    startActivity(new Intent(context, MainActivity.class));
                    requireActivity().finish();
                }, 1500
        );
    }
}
