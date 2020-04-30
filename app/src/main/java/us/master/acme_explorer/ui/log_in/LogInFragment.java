package us.master.acme_explorer.ui.log_in;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import us.master.acme_explorer.MainActivity;
import us.master.acme_explorer.R;
import us.master.acme_explorer.common.Util;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;
import static java.util.Objects.requireNonNull;
import static us.master.acme_explorer.common.Util.mAuth;

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
    private GoogleSignInOptions googleSignInOptions;
    private ViewGroup container;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.container = container;
        this.context = container.getContext();
        View root = inflater.inflate(R.layout.fragment_log_in, container, false);
        setView(root);

        googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        return root;
    }

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
        mInputEditTextEmail.addTextChangedListener(mTxtChdLnr(mInputEditTextEmail));
        mInputEditTextPassword.addTextChangedListener(mTxtChdLnr(mInputEditTextPassword));
    }

    private TextWatcher mTxtChdLnr(TextInputEditText mInputEditTextEmail) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mInputEditTextEmail.getId() == R.id.login_email_et) {
                    mTextInputLayoutEmail.setError(null);
                } else {
                    mTextInputLayoutPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button_email:
                attemptLogin();
                break;
            case R.id.login_button_google:
                attemptLoginGoogle(googleSignInOptions);
                break;
            case R.id.login_button_sign_up:
                Util.navigateTo(v, R.id.action_nav_log_in_fragment_to_nav_sign_up_fragment,
                        null);
                break;
        }
    }

    private void attemptLogin() {
        boolean user, pass;
        mTextInputLayoutEmail.setError(null);
        mTextInputLayoutPassword.setError(null);
        user = alertLoginUser(mTextInputLayoutEmail, mInputEditTextEmail);
        pass = alertLoginUser(mTextInputLayoutPassword, mInputEditTextPassword);

        if (user && pass)
            loginWithEmail(requireNonNull(mInputEditTextEmail.getText()),
                    requireNonNull(mInputEditTextPassword.getText()));
    }

    private void loginWithEmail(Editable email, Editable password) {
        checkInstance();
        if (mAuth != null) {
            showLoginForm(false);
            mAuth.signInWithEmailAndPassword(email.toString(), password.toString())
                    .addOnCompleteListener(requireActivity(), this::verifyTask);
        } else {
            googlePlayServicesError();
        }
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
            }
        } catch (Exception e) {
            showLoginForm(true);
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
                stringId = R.string.device_temporaly_blocked_error;
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
        Log.d(TAG, "checkUserDataBaseLogin: user -> " + user.getUid());
        loginSucceeded();
    }

    private void showErrorEmailVerified(FirebaseUser user) {
        showLoginForm(true);
        DialogInterface.OnClickListener posBtn = (dialog, which) ->
                user.sendEmailVerification().addOnCompleteListener(task -> {
                    String text = context.getString(task.isSuccessful()
                            ? R.string.login_verified_mail_error_sent
                            : R.string.login_verified_mail_error_no_sent);
                    Snackbar.make(mProgressBar, text, LENGTH_SHORT).show();
                }),
                negBtn = (dialog, which) -> {
                };
        Util.showDialogMessage(context,
                R.string.login_verified_mail_error,
                R.string.login_verified_mail_error_ok,
                R.string.login_verified_mail_error_cancel, posBtn, negBtn);
    }

    private void showErrorDialogMail() {
        showLoginForm(true);
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

    private void attemptLoginGoogle(GoogleSignInOptions googleSignInOptions) {
        GoogleSignInClient signIn = GoogleSignIn.getClient(context, googleSignInOptions);
        Intent signInIntent = signIn.getSignInIntent();
        startActivityForResult(signInIntent, LOGIN_GOOGLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        GoogleSignInAccount account;
        AuthCredential credential;
        if (requestCode == LOGIN_GOOGLE) {
            Task<GoogleSignInAccount> result = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                account = result.getResult(ApiException.class);
                assert account != null;
                credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                checkInstance();
                if (mAuth != null)
                    mAuth.signInWithCredential(credential)
                            .addOnCompleteListener(requireActivity(), this::verifyTask);
                else googlePlayServicesError();
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void showLoginForm(boolean show) {
        TransitionSet transitionSet = new TransitionSet();
        Transition layoutFade = new AutoTransition();
        layoutFade.setDuration(500);
        transitionSet.addTransition(layoutFade);
        TransitionManager.beginDelayedTransition(this.container, transitionSet);
        mLayoutFormLogin.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressBar.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private boolean alertLoginUser(TextInputLayout layout, TextInputEditText mTextView) {
        boolean validate = requireNonNull(mTextView.getText()).toString().length() < 1;
        if (validate) {
            layout.setErrorEnabled(true);
            layout.setError(context.getString(layout.getId() == R.id.login_email
                    ? R.string.login_error_message_1
                    : R.string.login_error_message_2));
        }
        return !validate;
    }

    private void checkInstance() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
    }

    private void loginSucceeded() {
        Util.mSnackBar(mProgressBar, context, R.string.welcome, LENGTH_SHORT);
        new Handler().postDelayed(() -> {
                    startActivity(new Intent(context, MainActivity.class));
                    requireActivity().finish();
                }, 1000
        );
    }
}
