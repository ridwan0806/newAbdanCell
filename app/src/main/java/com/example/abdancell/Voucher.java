package com.example.abdancell;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdancell.Helper.DBHelper;
import com.example.abdancell.Interface.ItemClickListener;
import com.example.abdancell.Model.OrderItem;
import com.example.abdancell.Model.Product;
import com.example.abdancell.ViewHolder.Product.VHProduct;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Voucher#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Voucher extends Fragment {
    FirebaseUser firebaseUser;
    DatabaseReference root,dbProduct,dbUser;
    String userId,userName;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ProgressBar progressBar;
    FirebaseRecyclerAdapter<Product, VHProduct> adapter;

    ImageView plusBtn,minusBtn;
    TextView qty;
    int numberOrder = 1;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Voucher() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Voucher.
     */
    // TODO: Rename and change types and number of parameters
    public static Voucher newInstance(String param1, String param2) {
        Voucher fragment = new Voucher();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_voucher,container,false);
        recyclerView = layout.findViewById(R.id.rv_voucher);
        layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        progressBar = layout.findViewById(R.id.pb_voucher);

        root = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();
        dbUser = root.child("Users").child(userId);

        dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("username").getValue(String.class);
                dbProduct =root.child("Product").child("Voucher");
                getListVoucher();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });

        return layout;
    }

    private void getListVoucher() {
        FirebaseRecyclerOptions<Product> list =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(dbProduct.orderByChild("name"),Product.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Product, VHProduct>(list) {
            @Override
            protected void onBindViewHolder(@NonNull VHProduct holder, int position, @NonNull Product model) {
                String productId = adapter.getRef(position).getKey();
                String strProductName = adapter.getItem(position).getName();
                String strHargaModal = String.valueOf(adapter.getItem(position).getHargaModal());
                String strHargaJual = String.valueOf(adapter.getItem(position).getHargaJual());
                String strKategori = adapter.getItem(position).getCategory();

                NumberFormat formatRp = new DecimalFormat("#,###");
                double hargaModal = model.getHargaModal();
                double hargaJual = model.getHargaJual();

                holder.productName.setText(model.getName());
                holder.productHargaModal.setText(formatRp.format(hargaModal));
                holder.productHargaJual.setText(formatRp.format(hargaJual));
                holder.productCategory.setText(model.getCategory());

                holder.productMenu.setOnClickListener(view -> {
                    PopupMenu popupMenu = new PopupMenu(getContext(),holder.productMenu);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_product_options,popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int itemId = menuItem.getItemId();
                            if (itemId == R.id.editNama){
                                Toast.makeText(getContext(), "editNama", Toast.LENGTH_SHORT).show();
                            } else if (itemId == R.id.editHPP){
                                Toast.makeText(getContext(), "editNama", Toast.LENGTH_SHORT).show();
                            } else if (itemId == R.id.editHargaJual){
                                Toast.makeText(getContext(), "editNama", Toast.LENGTH_SHORT).show();
                            } else if (itemId == R.id.editKategori){
                                Toast.makeText(getContext(), "editNama", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                });

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        addQty(productId,strProductName,strHargaModal,strHargaJual,strKategori);
                    }
                });
            }

            @NonNull
            @Override
            public VHProduct onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_list_produk,parent,false);
                return new VHProduct(view);
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

    private void addQty(String productId, String strProductName, String strHargaModal, String strHargaJual, String strKategori) {
        AlertDialog.Builder addQty = new AlertDialog.Builder(getContext());
        addQty.setCancelable(false);
        addQty.setMessage(strProductName.toUpperCase(Locale.ROOT));

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View dialogQty = layoutInflater.inflate(R.layout.dialog_add_qty,null);
        addQty.setView(dialogQty);

        minusBtn = dialogQty.findViewById(R.id.btnMinus);
        plusBtn = dialogQty.findViewById(R.id.btnPlus);
        qty = dialogQty.findViewById(R.id.txtQty);

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberOrder = numberOrder + 1;
                qty.setText(String.valueOf(numberOrder));
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberOrder > 1){
                    int newNumberOrder = Integer.parseInt(qty.getText().toString());
                    numberOrder = newNumberOrder - 1;
                    qty.setText(String.valueOf(numberOrder));
                }
            }
        });

        addQty.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        addQty.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DBHelper dbHelper = new DBHelper(getContext());
                int result = dbHelper.checkItemExist(productId);
                if (result == 0){
                    AlertDialog.Builder failed = new AlertDialog.Builder(getContext());
                    failed.setCancelable(false);
                    failed.setTitle("Error !");
                    failed.setMessage(strProductName.toUpperCase(Locale.ROOT)+" sudah ada di pesanan");

                    failed.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    failed.show();
                } else {
                    Double totalModal = Double.parseDouble(strHargaModal) * numberOrder;
                    Double totalSales = Double.parseDouble(strHargaJual) * numberOrder;
                    Double margin = totalSales - totalModal;

                    dbHelper.addToCart(new OrderItem(
                        "",
                        productId,
                        strProductName,
                        strKategori,
                        numberOrder,
                        Double.parseDouble(strHargaModal),
                        Double.parseDouble(strHargaJual),
                        totalModal,
                        margin,
                        totalSales
                    ));
                    Toast.makeText(getContext(), strProductName.toUpperCase(Locale.ROOT)+" berhasil diinput", Toast.LENGTH_SHORT).show();
                    numberOrder = 1;
                }
            }
        });

        addQty.show();
    }
}