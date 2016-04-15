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
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import me.syniuhin.storyteller.net.model.Message;
import me.syniuhin.storyteller.net.model.User;
import me.syniuhin.storyteller.net.service.ServiceGenerator;
import me.syniuhin.storyteller.net.service.UserService;
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
public class LoginActivity extends AppCompatActivity
    implements LoaderCallbacks<Cursor> {

  /**
   * Id to identity READ_CONTACTS permission request.
   */
  private static final int REQUEST_READ_CONTACTS = 0;

  /**
   * A dummy authentication store containing known user names and passwords.
   * TODO: remove after connecting to a real authentication system.
   */
  private static final String[] DUMMY_CREDENTIALS = new String[] {
      "foo@example.com:hello", "bar@example.com:world"
  };

  private AutoCompleteTextView mEmailView;
  private EditText mPasswordView;
  private View mProgressView;
  private View mLoginFormView;

  private UserService mUserService = null;
  private CompositeSubscription mCompositeSubscription;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    findViews();

    initRetrofit();
    setupViews();
  }

  private void findViews() {
    mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
    mPasswordView = (EditText) findViewById(R.id.password);
    mLoginFormView = findViewById(R.id.login_form);
    mProgressView = findViewById(R.id.login_progress);
  }

  private void setupViews() {
    populateAutoComplete();

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

    Button mEmailSignInButton = (Button) findViewById(
        R.id.email_sign_in_button);
    mEmailSignInButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        attemptLogin();
      }
    });
  }

  private void initRetrofit() {
    mUserService = ServiceGenerator.createService(UserService.class);
    mCompositeSubscription = new CompositeSubscription();
  }

  private void populateAutoComplete() {
    if (!mayRequestContacts()) {
      return;
    }

    getLoaderManager().initLoader(0, null, this);
  }

  private boolean mayRequestContacts() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      return true;
    }
    if (checkSelfPermission(
        READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
      return true;
    }
    if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
      Snackbar.make(mEmailView, R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
              .setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                @TargetApi(Build.VERSION_CODES.M)
                public void onClick(View v) {
                  requestPermissions(new String[] { READ_CONTACTS },
                                     REQUEST_READ_CONTACTS);
                }
              });
    } else {
      requestPermissions(new String[] { READ_CONTACTS }, REQUEST_READ_CONTACTS);
    }
    return false;
  }

  /**
   * Callback received when a permissions request has been completed.
   */
  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    if (requestCode == REQUEST_READ_CONTACTS) {
      if (grantResults.length == 1 && grantResults[0] == PackageManager
          .PERMISSION_GRANTED) {

        populateAutoComplete();
      }
    }
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
    String email = mEmailView.getText().toString();
    String password = mPasswordView.getText().toString();

    showProgress(true);
    Observable<Response<Message>> o = mUserService.login(
        User.create().setEmail(email).setPassword(password));
    mCompositeSubscription.add(
        o.observeOn(AndroidSchedulers.mainThread())
         .subscribeOn(Schedulers.newThread())
         .subscribe(new Action1<Response<Message>>() {
           @Override
           public void call(Response<Message> response) {
             showProgress(false);
             if (response.isSuccessful()) {
               final Message responseBody = response.body();
               Snackbar.make(mLoginFormView, responseBody.getMessage(),
                             Snackbar.LENGTH_SHORT)
                       .setAction("Action", null)
                       .show();
               onLoginSuccess(responseBody.getUserId());
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
               Snackbar.make(mLoginFormView,
                             "Unexpected error happened, try later.",
                             Snackbar.LENGTH_SHORT)
                       .setAction("Action", null)
                       .show();
             }
           }
         }, new Action1<Throwable>() {
           @Override
           public void call(Throwable throwable) {
             showProgress(false);
             throwable.printStackTrace();
             Snackbar.make(mLoginFormView,
                           "Unexpected error happened, try later.",
                           Snackbar.LENGTH_LONG)
                     .setAction("Retry", new OnClickListener() {
                       @Override
                       public void onClick(View v) {
                         attemptLogin();
                       }
                     })
                     .show();
           }
         })
    );
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

  private void onLoginSuccess(long userId) {
    PreferenceManager.getDefaultSharedPreferences(this)
                     .edit()
                     .putLong("userId", userId)
                     .putBoolean("isLoggedIn", true)
                     .commit();
    startActivity(new Intent(this, MainActivity.class));
  }

  @Override
  public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    return new CursorLoader(
        this,
        // Retrieve data rows for the device user's
        // 'profile' contact.
        Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                             ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
        ProfileQuery.PROJECTION,

        // Select only email addresses.
        ContactsContract.Contacts.Data.MIMETYPE +
            " = ?",
        new String[] { ContactsContract.CommonDataKinds
            .Email
            .CONTENT_ITEM_TYPE },

        // Show primary email addresses first. Note that
        // there won't be
        // a primary email address if the user hasn't
        // specified one.
        ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
  }

  @Override
  public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    List<String> emails = new ArrayList<>();
    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      emails.add(cursor.getString(ProfileQuery.ADDRESS));
      cursor.moveToNext();
    }

    addEmailsToAutoComplete(emails);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> cursorLoader) {

  }

  private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
    //Create adapter to tell the AutoCompleteTextView what to show in its
    // dropdown list.
    ArrayAdapter<String> adapter =
        new ArrayAdapter<>(LoginActivity.this,
                           android.R.layout.simple_dropdown_item_1line,
                           emailAddressCollection);

    mEmailView.setAdapter(adapter);
  }


  private interface ProfileQuery {
    String[] PROJECTION = {
        ContactsContract.CommonDataKinds.Email.ADDRESS,
        ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
    };

    int ADDRESS = 0;
    int IS_PRIMARY = 1;
  }
}

