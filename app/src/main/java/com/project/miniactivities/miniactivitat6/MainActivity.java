package com.project.miniactivities.miniactivitat6;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Firebase: ";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView textView;
    private EditText editText;
    private EditText ed_password;
    private EditText ed_email;
    private Button register;
    private Button login;
    private Button logout;
    private Button send;
    private Button reset;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        register = (Button) findViewById(R.id.btnRegister);
        login = (Button) findViewById(R.id.btnLogin);
        logout = (Button) findViewById(R.id.btnLogout);

        ed_password = (EditText) findViewById(R.id.txtPassword);
        ed_email = (EditText) findViewById(R.id.txtEmail);

        textView = (TextView) findViewById(R.id.viewMessage);
        editText = (EditText) findViewById(R.id.messageFirebase);
        send = (Button) findViewById(R.id.btnSend);
        reset = (Button) findViewById(R.id.btnReset);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("miniactivitat6")
                .child("message");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            value = dataSnapshot.getValue(String.class);
                            Log.d(TAG, "Value is: " + value);
                            textView.setText(value);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
                    updateUI(true);
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out:");
                    updateUI(false);
                }
            }
        };

        register.setOnClickListener(this);
        login.setOnClickListener(this);
        logout.setOnClickListener(this);
        send.setOnClickListener(this);
        reset.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(false);
    }


    private void signIn(String txtemail, String password) {
        mAuth.signInWithEmailAndPassword(txtemail, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithEmail:failed" + task.getException());
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    updateUI(true);
                }
            }
        });
    }

    private void createAccount(String txtemail, String password) {
        mAuth.createUserWithEmailAndPassword(txtemail, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("Auth", "create user with email: onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Log.d("Firebase auth", "onComplete: Failed=" + task.getException().getMessage());
                    Toast.makeText(MainActivity.this, R.string.regFail, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.regSuc, Toast.LENGTH_SHORT).show();
                    register.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        String txtemail, password;

        switch (v.getId()) {
            case R.id.btnRegister:
//                txtemail = ed_email.getText().toString();
//                password = ed_password.getText().toString();
//                createAccount(txtemail, password);
                createAccount("elmchai401@gmail.com", "chaimaeelmorabet");
                break;

            case R.id.btnLogin:
//                txtemail = ed_email.getText().toString();
//                password = ed_password.getText().toString();
//                signIn(txtemail, password);
                signIn("elmchai401@gmail.com", "chaimaeelmorabet");
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        value = dataSnapshot.getValue(String.class);
                        Log.d(TAG, "Value is: " + value);
                        textView.setText(value);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
                break;
            case R.id.btnLogout:
                signOut();
                break;
            case R.id.btnReset:
                myRef.setValue(getString(R.string.helloFirebase));
                break;
            case R.id.btnSend:
                String msg = editText.getText().toString();
                if (msg.isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.warningTxtEmpty, Toast.LENGTH_SHORT).show();
                } else {
                    myRef.setValue(msg);
                }
                break;
        }
    }

    private void updateUI(boolean b) {
        if (b) {
            ed_email.setVisibility(View.INVISIBLE);
            ed_password.setVisibility(View.INVISIBLE);
            login.setVisibility(View.INVISIBLE);
            register.setVisibility(View.INVISIBLE);
            logout.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            editText.setVisibility(View.VISIBLE);
            send.setVisibility(View.VISIBLE);
            reset.setVisibility(View.VISIBLE);
        } else {
            ed_email.setVisibility(View.VISIBLE);
            ed_password.setVisibility(View.VISIBLE);
            login.setVisibility(View.VISIBLE);
            register.setVisibility(View.VISIBLE);
            logout.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
            editText.setVisibility(View.INVISIBLE);
            send.setVisibility(View.INVISIBLE);
            reset.setVisibility(View.INVISIBLE);
        }
    }


}
