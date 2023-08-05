package com.example.abdancell;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdancell.Helper.DBHelper;
import com.example.abdancell.Helper.MoneyTextWatcher;
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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Pulsa#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Pulsa extends Fragment {
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

    EditText etProductName,etProductHPP,etProductPrice;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Pulsa() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Pulsa.
     */
    // TODO: Rename and change types and number of parameters
    public static Pulsa newInstance(String param1, String param2) {
        Pulsa fragment = new Pulsa();
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
        View layout = inflater.inflate(R.layout.fragment_pulsa,container,false);
        recyclerView = layout.findViewById(R.id.rv_Pulsa);
        layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        progressBar = layout.findViewById(R.id.pb_Pulsa);

        root = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();
        dbUser = root.child("Users").child(userId);

        dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("username").getValue(String.class);
                dbProduct =root.child("Product").child("Pulsa");
                getListPulsa();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });

        return layout;
    }

    private void getListPulsa() {
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
                                AlertDialog.Builder confirm = new AlertDialog.Builder(getContext());
                                confirm.setCancelable(false);
                                confirm.setMessage("Ubah Nama Produk ?");

                                confirm.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                                confirm.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        editProductName(productId,strProductName);
                                    }
                                });

                                confirm.show();
                            } else if (itemId == R.id.editHPP){
                                AlertDialog.Builder confirm = new AlertDialog.Builder(getContext());
                                confirm.setCancelable(false);
                                confirm.setMessage("Ubah Harga Modal ?");

                                confirm.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                                confirm.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        editHargaModal(productId,strProductName,strHargaModal);
                                    }
                                });

                                confirm.show();
                            } else if (itemId == R.id.editHargaJual){
                                AlertDialog.Builder confirm = new AlertDialog.Builder(getContext());
                                confirm.setCancelable(false);
                                confirm.setMessage("Ubah Harga Jual ?");

                                confirm.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                                confirm.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        editHargaJual(productId,strProductName,strHargaJual);
                                    }
                                });

                                confirm.show();
                            } else if (itemId == R.id.editKategori){
                                AlertDialog.Builder confirm = new AlertDialog.Builder(getContext());
                                confirm.setCancelable(false);
                                confirm.setMessage("Hapus "+strProductName.toUpperCase(Locale.ROOT)+ " dari database ?");

                                confirm.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                                confirm.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        deleteProduct(productId,strProductName);
                                    }
                                });

                                confirm.show();
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

    private void editProductName(String productId, String strProductName) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setCancelable(false);
        dialog.setMessage("Ubah nama "+strProductName.toUpperCase(Locale.ROOT)+" ?");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View dialogEditName = layoutInflater.inflate(R.layout.dialog_edit_nama_produk,null);
        dialog.setView(dialogEditName);

        etProductName = dialogEditName.findViewById(R.id.etProductEditName);

        dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (etProductName.getText().toString().length() == 0){
                    AlertDialog.Builder failed = new AlertDialog.Builder(getContext());
                    failed.setCancelable(false);
                    failed.setTitle("Error !");
                    failed.setMessage("Nama produk belum diisi");

                    failed.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            editProductName(productId, strProductName);
                        }
                    });
                    failed.show();
                } else {
                    dbProduct.child(productId).child("name").setValue(etProductName.getText().toString());
                    Toast.makeText(getContext(), "nama produk berhasil diubah", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void editHargaModal(String productId, String strProductName, String strHargaModal) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setCancelable(false);
        dialog.setMessage("Ubah modal  "+strProductName.toUpperCase(Locale.ROOT)+" ?");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View dialogEditPrice = layoutInflater.inflate(R.layout.dialog_edit_modal,null);
        dialog.setView(dialogEditPrice);

        etProductHPP = dialogEditPrice.findViewById(R.id.etProductEditHPP);
        etProductHPP.addTextChangedListener(new MoneyTextWatcher(etProductHPP));

        dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BigDecimal value = MoneyTextWatcher.parseCurrencyValue(etProductHPP.getText().toString());
                String newModal = String.valueOf(value);

                if (Double.parseDouble(strHargaModal) == Double.parseDouble(newModal)){
                    AlertDialog.Builder failed = new AlertDialog.Builder(getContext());
                    failed.setCancelable(false);
                    failed.setTitle("Error !");
                    failed.setMessage("Harga modal masih sama");

                    failed.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            editHargaModal(productId, strProductName,strHargaModal);
                        }
                    });
                    failed.show();
                } else {
                    if (etProductHPP.getText().toString().length() == 0){
                        AlertDialog.Builder failed = new AlertDialog.Builder(getContext());
                        failed.setCancelable(false);
                        failed.setTitle("Error !");
                        failed.setMessage("Harga modal belum diisi");

                        failed.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                editHargaModal(productId, strProductName,strHargaModal);
                            }
                        });
                        failed.show();
                    } else {
                        dbProduct.child(productId).child("hargaModal").setValue(Double.parseDouble(newModal));
                        Toast.makeText(getContext(), "Harga modal berhasil diubah", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        dialog.show();
    }

    private void editHargaJual(String productId, String strProductName, String strHargaJual) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setCancelable(false);
        dialog.setMessage("Ubah Harga Jual  "+strProductName.toUpperCase(Locale.ROOT)+" ?");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View dialogEditPrice = layoutInflater.inflate(R.layout.dialog_edit_harga_jual,null);
        dialog.setView(dialogEditPrice);

        etProductPrice = dialogEditPrice.findViewById(R.id.etProductEditHargaJual);
        etProductPrice.addTextChangedListener(new MoneyTextWatcher(etProductPrice));

        dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BigDecimal value = MoneyTextWatcher.parseCurrencyValue(etProductPrice.getText().toString());
                String newPrice = String.valueOf(value);

                if (Double.parseDouble(strHargaJual) == Double.parseDouble(newPrice)){
                    AlertDialog.Builder failed = new AlertDialog.Builder(getContext());
                    failed.setCancelable(false);
                    failed.setTitle("Error !");
                    failed.setMessage("Harga jual masih sama");

                    failed.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            editHargaJual(productId, strProductName,strHargaJual);
                        }
                    });
                    failed.show();
                } else {
                    if (etProductPrice.getText().toString().length() == 0){
                        AlertDialog.Builder failed = new AlertDialog.Builder(getContext());
                        failed.setCancelable(false);
                        failed.setTitle("Error !");
                        failed.setMessage("Harga jual belum diisi");

                        failed.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                editHargaModal(productId, strProductName,strHargaJual);
                            }
                        });
                        failed.show();
                    } else {
                        dbProduct.child(productId).child("hargaJual").setValue(Double.parseDouble(newPrice));
                        Toast.makeText(getContext(), "Harga jual berhasil diubah", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        dialog.show();
    }

    private void deleteProduct(String productId, String strProductName) {
        dbProduct.child(productId).removeValue();
        Toast.makeText(getContext(), strProductName.toUpperCase(Locale.ROOT)+" berhasil dihapus", Toast.LENGTH_SHORT).show();
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
                    failed.setTitle("Info !");
                    failed.setMessage(strProductName.toUpperCase(Locale.ROOT)+" sudah ada di pesanan. Tetap lanjutkan pesanan ?");

                    failed.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Toast.makeText(getContext(), strProductName.toUpperCase(Locale.ROOT)+" dibatalkan", Toast.LENGTH_SHORT).show();
                        }
                    });

                    failed.setPositiveButton("Ya, Lanjutkan", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            submitItem(productId,strProductName,strKategori,strHargaModal,strHargaJual);
                        }
                    });
                    failed.show();
                } else {
                    submitItem(productId,strProductName,strKategori,strHargaModal,strHargaJual);
                }
            }
        });

        addQty.show();
    }

    private void submitItem(String productId, String strProductName, String strKategori, String strHargaModal, String strHargaJual) {
        Double totalModal = Double.parseDouble(strHargaModal) * numberOrder;
        Double totalSales = Double.parseDouble(strHargaJual) * numberOrder;
        Double margin = totalSales - totalModal;

        DBHelper dbHelper = new DBHelper(getContext());
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

        Intent cart = new Intent(getContext(),CartActivity.class);
        startActivity(cart);
    }
}