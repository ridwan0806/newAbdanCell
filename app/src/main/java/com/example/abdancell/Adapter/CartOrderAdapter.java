package com.example.abdancell.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abdancell.Helper.DBHelper;
import com.example.abdancell.Model.OrderItem;
import com.example.abdancell.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartOrderAdapter extends RecyclerView.Adapter<CartOrderAdapter.ViewHolder> {

    private Context context;
    private List<OrderItem> orderItem;

    public CartOrderAdapter(Context context, List<OrderItem> orderItem) {
        this.context = context;
        this.orderItem = orderItem;
    }

    @NonNull
    @Override
    public CartOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_cart_order,parent,false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CartOrderAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        OrderItem list = orderItem.get(position);

        int number = position + 1;
        holder.numberCount.setText(number+".");

        NumberFormat formatRp = new DecimalFormat("#,###");
        double price = Math.round(list.getHargaJual());
        double subtotal = Math.round(list.getSubtotalSales());

        holder.productName.setText(list.getProductName());
        holder.qty.setText(String.valueOf(list.getQty()));
        holder.price.setText(formatRp.format(price));
        holder.subtotal.setText(formatRp.format(subtotal));

        holder.menu.setOnClickListener(view -> {
            AlertDialog.Builder confirmDelete = new AlertDialog.Builder(context);
            confirmDelete.setCancelable(false);
            confirmDelete.setMessage("Hapus "+list.getProductName().toUpperCase(Locale.ROOT)+" ?");

            confirmDelete.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            confirmDelete.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    DBHelper db = new DBHelper(context);
                    db.deleteItem(Integer.parseInt(list.getId()));
                    orderItem.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, ""+list.getProductName()+" berhasil dihapus", Toast.LENGTH_SHORT).show();
                }
            });
            confirmDelete.show();

        });
    }

    @Override
    public int getItemCount() {
        return orderItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView productName,qty,price,subtotal,numberCount;
        ImageView menu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.txtCartOrderName);
            qty = itemView.findViewById(R.id.txtCartOrderQty);
            price = itemView.findViewById(R.id.txtCartOrderPrice);
            subtotal = itemView.findViewById(R.id.txtCartOrderSubtotal);
            menu = itemView.findViewById(R.id.imgMenuCartOrder);
            numberCount = itemView.findViewById(R.id.txtCartOrderNoUrut);
        }
    }
}
