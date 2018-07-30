package de.fbl.menual.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import de.fbl.menual.LoginActivity;
import de.fbl.menual.MainActivity;
import de.fbl.menual.R;
import de.fbl.menual.SettingsActivity;

/**
 * The {@link YesNoPreference} is a preference to show a dialog with Yes and No
 * buttons.
 * <p>
 * This preference will store a boolean into the SharedPreferences.
 */
public class YesNoPreference extends DialogPreference implements DialogInterface.OnClickListener {
    private boolean mWasPositiveResult;
    public static final int yesNoPreferenceStyle = 16842896;

    public YesNoPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public YesNoPreference(Context context, AttributeSet attrs) {
        this(context, attrs, yesNoPreferenceStyle);
    }

    public YesNoPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (callChangeListener(positiveResult)) {
            setValue(positiveResult);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        if (this.getKey().equals("logout_key")) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                signOut();
            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                // do your stuff to handle negative button
            }
        }

    }

    private void signOut() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getContext(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getContext().getString(R.string.default_web_client_id))
            .requestEmail()
            .build());

        mGoogleSignInClient.signOut()
            .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent myIntent = new Intent(getContext(), LoginActivity.class);
                    getContext().startActivity(myIntent);
                }
            });
    }

    /**
     * Sets the value of this preference, and saves it to the persistent store
     * if required.
     *
     * @param value The value of the preference.
     */
    public void setValue(boolean value) {
        mWasPositiveResult = value;

        persistBoolean(value);

        notifyDependencyChange(!value);
    }

    /**
     * Gets the value of this preference.
     *
     * @return The value of the preference.
     */
    public boolean getValue() {
        return mWasPositiveResult;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getBoolean(index, false);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedBoolean(mWasPositiveResult) :
                (Boolean) defaultValue);
    }

    @Override
    public boolean shouldDisableDependents() {
        return !mWasPositiveResult || super.shouldDisableDependents();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.wasPositiveResult = getValue();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setValue(myState.wasPositiveResult);
    }

    private static class SavedState extends BaseSavedState {
        boolean wasPositiveResult;

        public SavedState(Parcel source) {
            super(source);
            wasPositiveResult = source.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(wasPositiveResult ? 1 : 0);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

}
