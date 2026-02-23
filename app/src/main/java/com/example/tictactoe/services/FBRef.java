package com.example.tictactoe.services;

import android.content.Context;
import android.util.Log;

import com.example.tictactoe.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Minimal Firebase reference hub for the login lessons.
 * Keep it small in 018c; expand user-specific RTDB refs in later steps.
 */
public final class FBRef {

    public static final FirebaseAuth refAuth = FirebaseAuth.getInstance();
    public static final FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static final DatabaseReference refUsers = FBDB.getReference("Users");
    public static GoogleSignInClient googleSignInClient;

    public static String uid;
    public static DatabaseReference refUser;

    private FBRef() {
        // Utility class
    }

    public static void getUser(FirebaseUser fbuser) {
        if (fbuser == null) {
            uid = null;
            refUser = null;
            return;
        }

        uid = fbuser.getUid();
        refUser = refUsers.child(uid);
    }

    public static void initializeGoogleSignIn(Context context) {
        try {
            int webClientIdRes = context.getResources().getIdentifier(
                    "default_web_client_id",
                    "string",
                    context.getPackageName()
            );
            if (webClientIdRes == 0) {
                Log.e("FBRef", "default_web_client_id not found. Refresh google-services.json after enabling Google provider.");
                googleSignInClient = null;
                return;
            }

            String webClientId = context.getString(webClientIdRes);
            if (webClientId == null || webClientId.trim().isEmpty()) {
                Log.e("FBRef", "default_web_client_id is empty. Check google-services.json.");
                googleSignInClient = null;
                return;
            }

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(webClientId)
                    .requestEmail()
                    .build();

            googleSignInClient = GoogleSignIn.getClient(context, gso);
        } catch (Exception e) {
            Log.e("FBRef", "Failed to initialize Google Sign-In client", e);
            googleSignInClient = null;
        }
    }

    public static void signOutGoogle() {
        if (googleSignInClient != null) {
            googleSignInClient.signOut();
        }
    }
}
