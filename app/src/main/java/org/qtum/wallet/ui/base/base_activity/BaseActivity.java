package org.qtum.wallet.ui.base.base_activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.qtum.wallet.R;
import org.qtum.wallet.utils.ThemeUtils;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements BaseContextView {
    private static final String LOG_TAG = "BaseActivity";

    private GoogleApiAvailability googleApiAvailability;
    private static final int REQUEST_CODE = 1;

    protected abstract void createPresenter();

    protected abstract BasePresenter getPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        ThemeUtils.setAppTheme(this,ThemeUtils.getCurrentTheme(this));

        super.onCreate(savedInstanceState);
        createPresenter();
        getPresenter().onCreate();

        // Verifying or install Play Services
        verifyOrInstallPlayServices();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bindView();
        getPresenter().initializeViews();
        getPresenter().onPostCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().onResume();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getPresenter().onPostResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPresenter().onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getPresenter().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().onDestroy();
    }

    @Override
    public void hideKeyBoard() {
        Activity activity = this;
        if (isFinishing(activity)) return;
        View view = activity.getCurrentFocus();
        if (view != null) {
            hideKeyBoard(activity, view);
        }
    }

    @Override
    public void hideKeyBoard(View v) {
        Activity activity = this;
        if (isFinishing(activity)) return;
        if (v != null) {
            hideKeyBoard(activity, v);
        }
    }

    public void hideKeyBoard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public void setFocusTextInput(View textInputEditText, View textInputLayout) {
        textInputEditText.setFocusableInTouchMode(true);
        textInputEditText.requestFocus();
        textInputLayout.setFocusableInTouchMode(true);
        textInputLayout.requestFocus();
    }

    public boolean isFinishing(Activity activity) {
        return activity == null || activity.isFinishing();
    }

    protected void bindView() {
        ButterKnife.bind(this);
    }

    @Override
    public Context getContext() {
        return getBaseContext();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void setSoftMode() {
    }

    protected abstract void updateTheme();

    public void reloadActivity(){
        updateTheme();
    }

    public void verifyOrInstallPlayServices() {

        googleApiAvailability = GoogleApiAvailability.getInstance();
        int googleApAvailabilityResult = googleApiAvailability.isGooglePlayServicesAvailable(this);
        boolean dialogRequired = false;

        switch (googleApAvailabilityResult) {

            case ConnectionResult.SERVICE_DISABLED:
                Log.w(LOG_TAG, "Google Play Services Disabled");
                dialogRequired = true;
                break;

            case ConnectionResult.SERVICE_MISSING:
                Log.w(LOG_TAG, "Google Play Services Missing");
                dialogRequired = true;
                break;

            case ConnectionResult.SERVICE_INVALID:
                Log.w(LOG_TAG, "Google Play Services Disabled");
                dialogRequired = true;
                break;

            case ConnectionResult.SERVICE_MISSING_PERMISSION:
                Log.w(LOG_TAG, "Google Play Services Missing Permission");
                dialogRequired = true;
                break;

            case ConnectionResult.SERVICE_UPDATING:
                Log.w(LOG_TAG, "Google Play Services Updating");
                dialogRequired = true;
                break;

            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Log.w(LOG_TAG, "Google Play Services Version Update Required");
                dialogRequired = true;
                break;

            default:
                Log.w(LOG_TAG, "Google Play Services Available version code: " + googleApAvailabilityResult);
                break;
        }

        if (dialogRequired) {
            googleApiAvailability.getErrorDialog(this, googleApAvailabilityResult, REQUEST_CODE, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(BaseActivity.this, R.string.warning_play_services_required_user_cancelled, Toast.LENGTH_LONG).show();
                    System.exit(0);
                }
            });
        }
    }
}
