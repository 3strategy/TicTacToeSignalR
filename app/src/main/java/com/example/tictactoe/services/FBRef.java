package com.example.tictactoe.services;

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
}
