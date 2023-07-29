package com.example.abdancell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdancell.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private EditText name,email,password;
    FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    private TextView username;

    FirebaseUser firebaseUser;
//    DatabaseReference root,dbUser;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        root = FirebaseDatabase.getInstance().getReference();

        username = findViewById(R.id.txtUsername);

        if (firebaseUser != null){
            userId = firebaseUser.getUid();
            username.setText(firebaseUser.getDisplayName());
        } else {
            Intent login = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(login);
            finish();
        }

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("tunggu sebentar..");
        progressDialog.setCancelable(false);

        ConstraintLayout product = findViewById(R.id.ic_product);
        ConstraintLayout user = findViewById(R.id.ic_user);
        ConstraintLayout logout = findViewById(R.id.ic_logout);

        product.setOnClickListener(view -> {
            Intent productPage = new Intent(MainActivity.this,ProductActivity.class);
            startActivity(productPage);
        });

        user.setOnClickListener(view -> {
            createNewUser();
        });

        logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void createNewUser() {
        AlertDialog.Builder createUser = new AlertDialog.Builder(this);
        createUser.setCancelable(false);
        createUser.setMessage("Buat User");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.dialog_create_new_user,null);
        createUser.setView(layout);

        name = layout.findViewById(R.id.etUserName);
        email = layout.findViewById(R.id.etUserEmail);
        password = layout.findViewById(R.id.etUserPassword);

        createUser.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        createUser.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addUser(name.getText().toString(),email.getText().toString(),password.getText().toString());
            }
        });

        createUser.show();
    }

    private void addUser(String nameUser, String emailUser, String passwordUser) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(emailUser,passwordUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    if (firebaseUser != null){
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(nameUser).build();
                        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String userKey = firebaseUser.getUid();
                                DatabaseReference dbUser = FirebaseDatabase.getInstance().getReference();
                                User user = new User();
                                user.setEmail(emailUser);
                                user.setPassword(passwordUser);
                                user.setUsername(nameUser);
                                dbUser.child("Users").child(userKey).setValue(user);

                                Toast.makeText(MainActivity.this, "User baru berhasil ditambah", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }
}