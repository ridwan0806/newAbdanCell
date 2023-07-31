package com.example.abdancell.ViewHolder.Order;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abdancell.Interface.ItemClickListener;
import com.example.abdancell.R;

public class VHListOrder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView orderDate,orderSubtotalHPP,orderSubtotalMargin,orderSubtotalSetoran;
    private ItemClickListener itemClickListener;

    public VHListOrder(@NonNull View itemView) {
        super(itemView);
        orderDate = itemView.findViewById(R.id.txtRVOrderDate);
        orderSubtotalHPP = itemView.findViewById(R.id.txtRVOrderSubtotalHPP);
        orderSubtotalMargin = itemView.findViewById(R.id.txtRVOrderSubtotalMargin);
        orderSubtotalSetoran = itemView.findViewById(R.id.txtRVOrderSubtotalSetoran);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getBindingAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
}
