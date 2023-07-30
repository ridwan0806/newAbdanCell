package com.example.abdancell;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.abdancell.Adapter.CartOrderAdapter;
import com.example.abdancell.Helper.DBHelper;
import com.example.abdancell.Model.OrderItem;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    List<OrderItem> orderItem = new ArrayList<>();
    CartOrderAdapter adapter;

    int setoran = 0;
    int subtotalQty = 0;
    TextView tvSetoran,tvSubtotalQty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.rvCartOder);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        tvSetoran = findViewById(R.id.txtCartSubtotalPrice);
        tvSubtotalQty = findViewById(R.id.txtCartSubtotalQty);

        getListCart();
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
        }
        tvSubtotalQty.setText(String.valueOf(subtotalQty));
        tvSetoran.setText("Rp "+formatRp.format(setoran));
    }
}