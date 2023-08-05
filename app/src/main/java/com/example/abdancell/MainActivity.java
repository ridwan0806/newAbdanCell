package com.example.abdancell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
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

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private EditText name,email,password;
    FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    private TextView username, tvStartDate,tvEndDate;

    FirebaseUser firebaseUser;
//    DatabaseReference root,dbUser;
    String userId;

    String startDate = "";
    String endDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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
        ConstraintLayout sales = findViewById(R.id.ic_pesanan);
        ConstraintLayout user = findViewById(R.id.ic_user);
        ConstraintLayout logout = findViewById(R.id.ic_logout);

        product.setOnClickListener(view -> {
            Intent productPage = new Intent(MainActivity.this,ProductActivity.class);
            startActivity(productPage);
        });

        sales.setOnClickListener(view -> {
            filterOrderDate();
        });

        user.setOnClickListener(view -> {
            createNewUser();
        });

        logout.setOnClickListener(view -> {
            AlertDialog.Builder confirm = new AlertDialog.Builder(this);
            confirm.setTitle("Logout ?");
            confirm.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            confirm.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    FirebaseAuth.getInstance().signOut();
                    Intent logout = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(logout);
                    finish();
                }
            });
            confirm.show();
        });

    }

    private void filterOrderDate() {
        AlertDialog.Builder filterDate = new AlertDialog.Builder(this);
        filterDate.setCancelable(false);
        filterDate.setMessage("Tentukan Tanggal Penjualan");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.dialog_filter_order_date,null);
        filterDate.setView(layout);

        tvStartDate = layout.findViewById(R.id.tvRekapStartDate);
        tvEndDate = layout.findViewById(R.id.tvRekapEndDate);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        tvStartDate.setOnClickListener(view -> {
            DatePickerDialog dialogDateStart = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    month = month + 1;

                    String monthString = "";
                    if (month < 10){
                        monthString = "0"+month;
                    } else {
                        monthString = String.valueOf(month);
                    }

                    String dayString = "";
                    if (day < 10){
                        dayString = "0"+day;
                    } else {
                        dayString = String.valueOf(day);
                    }

                    tvStartDate.setText(year+"-"+monthString+"-"+dayString);
                    startDate = tvStartDate.getText().toString();
//                    Log.d("TAG",startDate);
                }
            },year, month, day);
            dialogDateStart.show();
        });

        tvEndDate.setOnClickListener(view -> {
            DatePickerDialog dialogDateEnd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    month = month + 1;

                    String monthString = "";
                    if (month < 10){
                        monthString = "0"+month;
                    } else {
                        monthString = String.valueOf(month);
                    }

                    String dayString = "";
                    if (day < 10){
                        dayString = "0"+day;
                    } else {
                        dayString = String.valueOf(day);
                    }

                    tvEndDate.setText(year+"-"+monthString+"-"+dayString);
                    endDate = tvEndDate.getText().toString();
//                    Log.d("TAG",endDate);
                }
            },year, month, day);
            dialogDateEnd.show();
        });

        filterDate.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        filterDate.setPositiveButton("Cari", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (startDate == ""){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Error !");
                    builder.setMessage("Tanggal mulai belum dipilih");

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            filterOrderDate();
                        }
                    });
                    builder.show();
                } else if (endDate == ""){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Error !");
                    builder.setMessage("Tanggal akhir belum dipilih");

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            filterOrderDate();
                        }
                    });
                    builder.show();
                } else {
                    Intent rekapPenjualan = new Intent(MainActivity.this,RekapPenjualan.class);
                    rekapPenjualan.putExtra("startDate",startDate);
                    rekapPenjualan.putExtra("endDate",endDate);
                    startActivity(rekapPenjualan);
                }
            }
        });

        filterDate.show();
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