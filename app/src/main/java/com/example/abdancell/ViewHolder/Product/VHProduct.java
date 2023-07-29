package com.example.abdancell.ViewHolder.Product;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abdancell.Interface.ItemClickListener;
import com.example.abdancell.R;

public class VHProduct extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView productName,productCategory,productHargaModal,productHargaJual;
    public ImageView productMenu;
    private ItemClickListener itemClickListener;

    public VHProduct(@NonNull View itemView) {
        super(itemView);
        productName = itemView.findViewById(R.id.txtRVProductName);
        productCategory = itemView.findViewById(R.id.txtRVProdukKategori);
        productHargaModal = itemView.findViewById(R.id.txtRVProdukHargaModal);
        productHargaJual = itemView.findViewById(R.id.txtRVProdukHargaJual);
        productMenu = itemView.findViewById(R.id.imgRVProductMenu);
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
