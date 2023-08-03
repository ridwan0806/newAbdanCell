package com.example.abdancell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.abdancell.Interface.ItemClickListener;
import com.example.abdancell.Model.Order;
import com.example.abdancell.ViewHolder.Order.VHListOrder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class RekapPenjualan extends AppCompatActivity {
    String startDate = "";
    String endDate = "";
    DatabaseReference dbOrder;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ProgressBar progressBar;
    FirebaseRecyclerAdapter<Order, VHListOrder> adapter;

    TextView tvDateStart,tvDateEnd,tvTotalHPP,tvTotalMargin,tvTotalSetoran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap_penjualan);

        recyclerView = findViewById(R.id.rvRekapOrder);
        layoutManager = new LinearLayoutManager(RekapPenjualan.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        progressBar = findViewById(R.id.pbRekapOrder);

        tvDateStart = findViewById(R.id.txtRekapDateStart);
        tvDateEnd = findViewById(R.id.txtRekapDateEnd);
        tvTotalHPP = findViewById(R.id.txtRekapTotalHPP);
        tvTotalMargin = findViewById(R.id.txtRekapTotalMargin);
        tvTotalSetoran = findViewById(R.id.txtRekapTotalSales);

        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        dbOrder = root.child("Orders");

        if (getIntent() != null){
            startDate = getIntent().getStringExtra("startDate");
            endDate = getIntent().getStringExtra("endDate");
            getRekapPenjualan(startDate,endDate);
            calculateAll(startDate,endDate);
            tvDateStart.setText(startDate);
            tvDateEnd.setText((endDate));
        }
    }

    private void getRekapPenjualan(String startDate, String endDate) {
        FirebaseRecyclerOptions<Order> list =
                new FirebaseRecyclerOptions.Builder<Order>()
                        .setQuery(dbOrder.orderByKey().startAt(startDate).endAt(endDate),Order.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Order, VHListOrder>(list) {
            @Override
            protected void onBindViewHolder(@NonNull VHListOrder holder, int position, @NonNull Order model) {
                String orderId = adapter.getRef(position).getKey();

                NumberFormat formatRp = new DecimalFormat("#,###");
                double totHPP = model.getSubtotalHPP();
                double totMargin = model.getSubtotalMargin();
                double totSetoran = model.getSubtotalSales();

                holder.orderDate.setText(model.getDateTrx());
                holder.orderSubtotalHPP.setText("Rp "+formatRp.format(totHPP));
                holder.orderSubtotalMargin.setText("Rp "+formatRp.format(totMargin));
                holder.orderSubtotalSetoran.setText("Rp "+formatRp.format(totSetoran));

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent detail = new Intent(RekapPenjualan.this,RekapOrderItem.class);
                        detail.putExtra("orderId",orderId);
                        startActivity(detail);
                    }
                });
            }

            @NonNull
            @Override
            public VHListOrder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_list_penjualan,parent,false);
                return new VHListOrder(view);
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

    private void calculateAll(String startDate, String endDate) {
        Query db = dbOrder.orderByKey().startAt(startDate).endAt(endDate);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NumberFormat formatRp = new DecimalFormat("#,###");
                double totHPP = 0;
                double totMargin = 0;
                double totSetoran = 0;

                for (DataSnapshot ds:snapshot.getChildren()){
                    double hpp = Double.valueOf(ds.child("subtotalHPP").getValue(long.class));
                    double margin = Double.valueOf(ds.child("subtotalMargin").getValue(long.class));
                    double setoran = Double.valueOf(ds.child("subtotalSales").getValue(long.class));

                    totHPP = totHPP + hpp;
                    totMargin = totMargin + margin;
                    totSetoran = totSetoran + setoran;
                }
                tvTotalHPP.setText(formatRp.format(totHPP));
                tvTotalMargin.setText(formatRp.format(totMargin));
                tvTotalSetoran.setText(formatRp.format(totSetoran));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        };
        db.addListenerForSingleValueEvent(listener);
    }
}