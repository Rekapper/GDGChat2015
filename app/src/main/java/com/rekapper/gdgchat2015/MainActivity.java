package com.rekapper.gdgchat2015;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.app.ListActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.FirebaseListAdapter;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends ListActivity {
    private Firebase mFirebaseRef;
    FirebaseListAdapter<ChatMessage> mListAdapter;
    String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase("https://gdgchat2015.firebaseio.com");
        final EditText textEdit = (EditText) this.findViewById(R.id.text_edit);
        Button sendButton = (Button) this.findViewById(R.id.send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textEdit.getText().toString();
                mFirebaseRef.push().setValue(new ChatMessage(MainActivity.this.mUsername, text));
                textEdit.setText("");
            }
        });
        mListAdapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                android.R.layout.two_line_list_item, mFirebaseRef) {
            @Override
            protected void populateView(View v, ChatMessage model) {
                ((TextView)v.findViewById(android.R.id.text1)).setText(model.getName());
                ((TextView)v.findViewById(android.R.id.text2)).setText(model.getText());
            }
        };
        setListAdapter(mListAdapter);
        Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button resetButton=(Button)findViewById(R.id.logout);
                resetButton.setVisibility(View.VISIBLE);
                Button loginButton1 = (Button) findViewById(R.id.login);
                loginButton1.setVisibility(View.GONE);
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Enter your email address and password")
                        .setTitle("Log in")
                        .setView(MainActivity.this.getLayoutInflater().inflate(R.layout.dialog_signin, null))
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AlertDialog dlg = (AlertDialog) dialog;
                                final String email = ((TextView) dlg.findViewById(R.id.email)).getText().toString();
                                final String password = ((TextView) dlg.findViewById(R.id.password)).getText().toString();

                                mFirebaseRef.createUser(email, password, new Firebase.ResultHandler() {
                                    @Override
                                    public void onSuccess() {
                                        mFirebaseRef.authWithPassword(email, password, null);
                                    }
                                    @Override
                                    public void onError(FirebaseError firebaseError) {
                                        mFirebaseRef.resetPassword(email,null);
                                        mFirebaseRef.authWithPassword(email, password, null);
                                    }
                                });
                            }
                        })
                        .create()
                        .show();
            }
        });
        mFirebaseRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    mUsername = ((String) authData.getProviderData().get("email"));
                    findViewById(R.id.login).setVisibility(View.INVISIBLE);
                } else {
                    mUsername = null;
                    findViewById(R.id.login).setVisibility(View.VISIBLE);
                }
            }
        });
        Button logoutButton = (Button) findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button resetButton=(Button)findViewById(R.id.login);
                resetButton.setVisibility(View.VISIBLE);
                Button logoutButton1 = (Button) findViewById(R.id.logout);
                logoutButton1.setVisibility(View.GONE);
                mFirebaseRef.unauth();}});
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListAdapter.cleanup();
        mFirebaseRef.unauth();
    }
}