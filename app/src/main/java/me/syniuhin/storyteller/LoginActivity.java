package me.syniuhin.storyteller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import me.syniuhin.storyteller.net.model.BasicResponse;
import me.syniuhin.storyteller.net.model.User;
import me.syniuhin.storyteller.net.service.api.UserService;
import me.syniuhin.storyteller.net.service.creator.SimpleServiceCreator;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

  private AutoCompleteTextView mEmailView;
  private EditText mPasswordView;
  private View mProgressView;
  private View mLoginFormView;

  private UserService mUserService = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    findViews();
    initService();
    setupViews();
  }

  protected void findViews() {
    mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
    mPasswordView = (EditText) findViewById(R.id.password);
    mLoginFormView = findViewById(R.id.login_form);
    mProgressView = findViewById(R.id.login_progress);
  }

  protected void setupViews() {
    mPasswordView.setOnEditorActionListener(
        new TextView.OnEditorActionListener() {
          @Override
          public boolean onEditorAction(TextView textView, int id,
                                        KeyEvent keyEvent) {
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
              attemptLogin();
              return true;
            }
            return false;
          }
        });

    Button mSignInButton = (Button) findViewById(R.id.email_sign_in_button);
    if (mSignInButton != null) {
      mSignInButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          attemptLogin();
        }
      });
    }

    Button mRegisterButton = (Button) findViewById(R.id.email_register_button);
    if (mRegisterButton != null) {
      mRegisterButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          attemptRegister();
        }
      });
    }

    Button mGuestSessionButton = (Button) findViewById(
        R.id.guest_session_button);
    if (mGuestSessionButton != null) {
      mGuestSessionButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          startDemoSession();
        }
      });
    }
  }

  @Override
  protected void initService() {
    mUserService = new SimpleServiceCreator().createInitializer(this)
                                             .create(UserService.class);
    compositeSubscription = new CompositeSubscription();
  }

  /**
   * Attempts to sign in or register the account specified by the login form.
   * If there are form errors (invalid email, missing fields, etc.), the
   * errors are presented and no actual login attempt is made.
   */
  private void attemptLogin() {
    // Reset errors.
    mEmailView.setError(null);
    mPasswordView.setError(null);

    // Store values at the time of the login attempt.
    final String email = mEmailView.getText().toString();
    final String password = mPasswordView.getText().toString();

    showProgress(true);
    Observable<Response<BasicResponse>> o = mUserService.login(
        User.create().setEmail(email).setPassword(password));
    compositeSubscription.add(
        o.observeOn(AndroidSchedulers.mainThread())
         .subscribeOn(Schedulers.newThread())
         .subscribe(new Action1<Response<BasicResponse>>() {
           @Override
           public void call(Response<BasicResponse> response) {
             showProgress(false);
             if (response.isSuccessful()) {
               final BasicResponse responseBody = response.body();
               indicateSuccess(responseBody.getMessage());
               onLoginSuccess(email, password, responseBody.getUserId());
             } else if (response.code() == 401) {
               try {
                 Snackbar.make(mLoginFormView,
                               response.errorBody().string(),
                               Snackbar.LENGTH_SHORT)
                         .setAction("Action", null)
                         .show();
               } catch (IOException e) {
                 e.printStackTrace();
               }
               mPasswordView.setError(
                   getString(R.string.error_invalid_password));
               mEmailView.requestFocus();
             } else {
               onUndefinedError(null);
             }
           }
         }, new Action1<Throwable>() {
           @Override
           public void call(Throwable throwable) {
             throwable.printStackTrace();
             onUndefinedError(new OnClickListener() {
               @Override
               public void onClick(View v) {
                 attemptLogin();
               }
             });
           }
         })
    );
  }

  private void attemptRegister() {
    // Reset errors.
    mEmailView.setError(null);
    mPasswordView.setError(null);

    // Store values at the time of the login attempt.
    String email = mEmailView.getText().toString();
    String password = mPasswordView.getText().toString();

    showProgress(true);
    Observable<Response<BasicResponse>> o = mUserService.register(
        User.create().setEmail(email).setPassword(password));
    compositeSubscription.add(
        o.observeOn(AndroidSchedulers.mainThread())
         .subscribeOn(Schedulers.newThread())
         .subscribe(new Action1<Response<BasicResponse>>() {
           @Override
           public void call(Response<BasicResponse> response) {
             showProgress(false);
             if (response.isSuccessful()) {
               indicateSuccess(response.body().getMessage());
               attemptLogin();
             } else if (response.code() == 403) {
               try {
                 onDefinedError(response.errorBody().string());
               } catch (IOException e) {
                 e.printStackTrace();
               }
             } else {
               handleUnexpectedError(mLoginFormView);
             }
           }
         }, new Action1<Throwable>() {
           @Override
           public void call(Throwable throwable) {
             throwable.printStackTrace();
             onUndefinedError(new OnClickListener() {
               @Override
               public void onClick(View v) {
                 attemptRegister();
               }
             });
           }
         })
    );
  }

  private void startDemoSession() {
    getSharedPreferences(PREFS_KEY, MODE_PRIVATE)
        .edit()
        .putBoolean("isLoggedIn", true)
        .putString("basicAuthHeader",
                   BaseActivity.DEMO_HEADER_BASIC)
        .commit();
    startActivity(new Intent(this, UploadingActivity.class));
  }

  @Override
  protected boolean isDemoRunning() {
    return false;
  }

  /**
   * Shows the progress UI and hides the login form.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  private void showProgress(final boolean show) {
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      int shortAnimTime = getResources().getInteger(
          android.R.integer.config_shortAnimTime);

      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
      mLoginFormView.animate().setDuration(shortAnimTime).alpha(
          show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
      });

      mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      mProgressView.animate().setDuration(shortAnimTime).alpha(
          show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
      });
    } else {
      // The ViewPropertyAnimator APIs are not available, so simply show
      // and hide the relevant UI components.
      mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
  }

  private void indicateSuccess(String message) {
    Snackbar.make(mLoginFormView, message,
                  Snackbar.LENGTH_SHORT)
            .show();
  }

  private void onLoginSuccess(final String email, final String password,
                              final long userId) {
    final String credentials = email + ":" + password;
    getSharedPreferences(PREFS_KEY, MODE_PRIVATE)
        .edit()
        .putLong("userId", userId)
        .putBoolean("isLoggedIn", true)
        .putString("basicAuthHeader",
                   Base64.encodeToString(credentials.getBytes(),
                                         Base64.NO_WRAP))
        .commit();
    startActivity(new Intent(this, MainActivity.class));
    finish();
  }

  private void onDefinedError(String message) {
    Snackbar.make(mLoginFormView, message,
                  Snackbar.LENGTH_SHORT)
            .show();
    mEmailView.requestFocus();
  }

  private void onUndefinedError(OnClickListener action) {
    showProgress(false);
    handleUnexpectedError(mLoginFormView);
  }
}

