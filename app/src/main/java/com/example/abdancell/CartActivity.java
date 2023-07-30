package com.example.abdancell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdancell.Adapter.CartOrderAdapter;
import com.example.abdancell.Helper.DBHelper;
import com.example.abdancell.Model.Order;
import com.example.abdancell.Model.OrderItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    List<OrderItem> orderItem = new ArrayList<>();
    CartOrderAdapter adapter;
    
    DatabaseReference dbOrder;

    int setoran = 0;
    int subtotalQty = 0;
    int subtotalModal = 0;
    int marginBersih = 0;
    TextView tvSetoran,tvSubtotalQty;

    TextView tvSelectDate;
    EditText orderNotes;

    String userId, userName, orderDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();

        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        dbOrder = root.child("Orders");
        DatabaseReference dbUser = root.child("Users").child(userId);

        dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("username").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });

        recyclerView = findViewById(R.id.rvCartOder);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        
        TextView saveOrder = findViewById(R.id.btnCartOrderConfirm);

        tvSetoran = findViewById(R.id.txtCartSubtotalPrice);
        tvSubtotalQty = findViewById(R.id.txtCartSubtotalQty);

        getListCart();
        
        saveOrder.setOnClickListener(view -> {
            confirmOrder();
        });
    }

    private void confirmOrder() {
        AlertDialog.Builder confirm = new AlertDialog.Builder(CartActivity.this);
        confirm.setCancelable(false);
        confirm.setMessage("Simpan Data Penjualan ?");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.dialog_confirm_order,null);
        confirm.setView(layout);

        tvSelectDate = layout.findViewById(R.id.tvOrderDate);
        orderNotes = layout.findViewById(R.id.etOrderNotes);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        tvSelectDate.setOnClickListener(view -> {

            DatePickerDialog dateInput = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
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

                    tvSelectDate.setText(year+"-"+monthString+"-"+dayString);
                    orderDate = tvSelectDate.getText().toString();
                    Log.d("TAG",orderDate);
                }
            }, year, month, day);
            dateInput.show();

        });

        confirm.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        confirm.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (tvSelectDate.getText().equals("--PILIH TANGGAL--")){
                    AlertDialog.Builder failed = new AlertDialog.Builder(CartActivity.this);
                    failed.setCancelable(false);
                    failed.setTitle("Error !");
                    failed.setMessage("Tanggal belum dipilih");

                    failed.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            confirmOrder();
                        }
                    });
                    failed.show();
                }
                saveOrder(orderDate);
            }
        });

        confirm.show();
    }

    private void saveOrder(String tglTrx) {
        DatabaseReference checkDateTrx = dbOrder.child(orderDate);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.d("Tag", String.valueOf(snapshot));
                if (snapshot.exists()){
                    AlertDialog.Builder dateIsExists = new AlertDialog.Builder(CartActivity.this);
                    dateIsExists.setCancelable(false);
                    dateIsExists.setTitle("Error !");
                    dateIsExists.setMessage("Transaksi pada tanggal ini sudah diinput");
//                    Log.d("TAG",orderDate);

                    dateIsExists.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            confirmOrder();
                        }
                    });

                    dateIsExists.show();
                } else {
//                    Toast.makeText(CartActivity.this, "lanjut", Toast.LENGTH_SHORT).show();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long serverTimeOffset = snapshot.getValue(Long.class);
                            long estimateServerTime = System.currentTimeMillis()+serverTimeOffset;

                            SimpleDateFormat createdDateTime;
                            createdDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date dateNow = new Date(estimateServerTime);

                            Order order = new Order();
                            order.setDateTrx(tvSelectDate.getText().toString());
                            order.setCreatedBy(userName);
                            order.setCreatedDateTime(createdDateTime.format(dateNow));
                            order.setNotes(orderNotes.getText().toString());
                            order.setSubtotalQty(subtotalQty);
                            order.setSubtotalHPP(subtotalModal);
                            order.setSubtotalMargin(marginBersih);
                            order.setSubtotalSales(setoran);

                            dbOrder.child(tvSelectDate.getText().toString()).setValue(order);

                            HashMap<String,OrderItem> orderItems = new HashMap<>();
                            for (int i = 0; i < orderItem.size(); i++){
                                String id = orderItem.get(i).getId();
                                String productId = orderItem.get(i).getProductId();
                                String productName = orderItem.get(i).getProductName();
                                String productCategory = orderItem.get(i).getProductKategori();
                                int productQty = orderItem.get(i).getQty();
                                double productHargaModal = orderItem.get(i).getHargaModal();
                                double productHargaJual = orderItem.get(i).getHargaJual();
                                double subtotalModal = orderItem.get(i).getSubtotalModal();
                                double subtotalMargin = orderItem.get(i).getSubtotalMargin();
                                double subtotalSales = orderItem.get(i).getSubtotalSales();

                                OrderItem listItem = new OrderItem(
                                        id,
                                        productId,
                                        productName,
                                        productCategory,
                                        productQty,
                                        productHargaModal,
                                        productHargaJual,
                                        subtotalModal,
                                        subtotalMargin,
                                        subtotalSales
                                );

                                orderItems.put(dbOrder.child(orderDate).child("orderItem").push().getKey(),listItem);
                                order.setOrderItem(orderItems);
                                dbOrder.child(orderDate).child("orderItem").setValue(orderItems);
                            }

                            Toast.makeText(CartActivity.this, "Data penjualan berhasil disimpan", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            throw error.toException();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        };
        checkDateTrx.addListenerForSingleValueEvent(listener);
    }

    private void getListCart() {
        orderItem = new DBHelper(this).getAllOrderItems();
        adapter = new CartOrderAdapter(this,orderItem);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        NumberFormat formatRp = new DecimalFormat("#,###");

        for (OrderItem list:orderItem){
            subtotalQty += list.getQty();
            setoran += list.getSubtotalSales();

            subtotalModal += list.getSubtotalModal();
            marginBersih += list.getSubtotalMargin();
        }
        tvSubtotalQty.setText(String.valueOf(subtotalQty));
        tvSetoran.setText("Rp "+formatRp.format(setoran));
//        finish();
    }
}