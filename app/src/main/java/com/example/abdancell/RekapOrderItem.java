package com.example.abdancell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.abdancell.Model.Order;
import com.example.abdancell.Model.OrderItem;
import com.example.abdancell.ViewHolder.Order.VHListOrderItems;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class RekapOrderItem extends AppCompatActivity {
    String orderId = "";
    DatabaseReference dbOrder;
    Order currentOrder;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ProgressBar progressBar;
    FirebaseRecyclerAdapter<OrderItem, VHListOrderItems> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap_order_item);

        recyclerView = findViewById(R.id.rvRekapDetailItem);
        layoutManager = new LinearLayoutManager(RekapOrderItem.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        progressBar = findViewById(R.id.pbRekapDetailItem);

        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        dbOrder = root.child("Orders");

        if (getIntent() != null){
            orderId = getIntent().getStringExtra("orderId");
            getListItem(orderId);
        }
    }

    private void getListItem(String orderId) {
        dbOrder.child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentOrder = snapshot.getValue(Order.class);

                FirebaseRecyclerOptions<OrderItem> items =
                        new FirebaseRecyclerOptions.Builder<OrderItem>()
                                .setQuery(dbOrder.child(orderId).child("orderItem"),OrderItem.class)
                                .build();
                adapter = new FirebaseRecyclerAdapter<OrderItem, VHListOrderItems>(items) {
                    @Override
                    protected void onBindViewHolder(@NonNull VHListOrderItems holder, int position, @NonNull OrderItem model) {
                        int number = position + 1;
                        holder.prodNo.setText(number+".");

                        NumberFormat formatRp = new DecimalFormat("#,###");
                        double jual = model.getHargaModal();
                        double total = model.getSubtotalSales();

                        holder.prodName.setText(model.getProductName());
                        holder.prodQty.setText(String.valueOf(model.getQty()));
                        holder.prodPrice.setText(formatRp.format(jual));
                        holder.prodSubtotal.setText(formatRp.format(total));
                        holder.prodCategory.setText(model.getProductKategori());
                    }

                    @NonNull
                    @Override
                    public VHListOrderItems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_order_item, parent, false);
                        return new VHListOrderItems(view);
                    }

                    @Override
                    public void onDataChanged(){
                        if (progressBar!=null){
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                };
                adapter.startListening();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });
    }
}