package com.example.abdancell.ViewHolder.Order;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abdancell.R;

public class VHListOrderItems extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView prodName,prodQty,prodPrice,prodSubtotal,prodNo,prodCategory;

    public VHListOrderItems(@NonNull View itemView) {
        super(itemView);
        prodName = itemView.findViewById(R.id.txtOrderItemName);
        prodQty = itemView.findViewById(R.id.txtOrderItemQty);
        prodPrice = itemView.findViewById(R.id.txtOrderItemPrice);
        prodSubtotal = itemView.findViewById(R.id.txtOrderItemSubtotal);
        prodNo = itemView.findViewById(R.id.txtOrderItemNoUrut);
        prodCategory = itemView.findViewById(R.id.txtOrderItemKategori);
    }

    @Override
    public void onClick(View view) {

    }
}
