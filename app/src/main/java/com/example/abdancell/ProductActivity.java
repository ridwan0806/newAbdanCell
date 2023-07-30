package com.example.abdancell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdancell.Helper.MoneyTextWatcher;
import com.example.abdancell.Model.Product;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;

public class ProductActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private TextView category;
    private EditText name,hpp,price;
    int rdStockUnitId = 0;
    String rdStockUnit = "";
    String categoryProduct = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        tabLayout = findViewById(R.id.tabMenuProduct);
        ViewPager viewPager = findViewById(R.id.viewPagerMenuProduct);
        TextView addProduct = findViewById(R.id.btnAddProduct);
        TextView viewCart = findViewById(R.id.btnViewOrder);

        tabLayout.addTab(tabLayout.newTab().setText("Voucher"));
        tabLayout.addTab(tabLayout.newTab().setText("Pulsa"));
        tabLayout.addTab(tabLayout.newTab().setText("Perdana"));
        tabLayout.addTab(tabLayout.newTab().setText("E-Wallet"));
        tabLayout.addTab(tabLayout.newTab().setText("Aksesoris"));

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        Voucher voucher = new Voucher();
                        return voucher;
                    case  1:
                        Pulsa pulsa = new Pulsa();
                        return pulsa;
                    case 2:
                        Perdana perdana = new Perdana();
                        return perdana;
                    case 3:
                        Ewallet ewallet = new Ewallet();
                        return ewallet;
                    case 4:
                        Aksesoris aksesoris = new Aksesoris();
                        return aksesoris;
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return tabLayout.getTabCount();
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        addProduct.setOnClickListener(view -> {
            addProductDialog();
        });

        viewCart.setOnClickListener(view -> {
            Intent cart = new Intent(ProductActivity.this,CartActivity.class);
            startActivity(cart);
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){
            case R.id.rdStockUnitYes:
                if (checked){
                    rdStockUnitId = 1;
                    rdStockUnit = "Yes";
                }
                break;
            case R.id.rdStockUnitNo:
                if (checked){
                    rdStockUnitId = 2;
                    rdStockUnit = "No";
                }
                break;
        }
    }

    private void addProductDialog() {
        AlertDialog.Builder createNewProduct = new AlertDialog.Builder(this);
        createNewProduct.setCancelable(false);
        createNewProduct.setMessage("Input Produk Baru");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.dialog_create_new_product,null);
        createNewProduct.setView(layout);

        category = layout.findViewById(R.id.tvProductCategory);
        name = layout.findViewById(R.id.etProductName);
        hpp = layout.findViewById(R.id.etProductHPP);
        hpp.addTextChangedListener(new MoneyTextWatcher(hpp));
        price = layout.findViewById(R.id.etProductPrice);
        price.addTextChangedListener(new MoneyTextWatcher(price));

        category.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this,category);
            popupMenu.getMenuInflater().inflate(R.menu.menu_category_product,popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    int itemId = menuItem.getItemId();
                    if (itemId == R.id.menu_voucher){
                        categoryProduct = "Voucher";
                        category.setText("Voucher");
                    } else if (itemId == R.id.menu_pulsa){
                        categoryProduct = "Pulsa";
                        category.setText("Pulsa");
                    } else if (itemId == R.id.menu_perdana){
                        categoryProduct = "Perdana";
                        category.setText("Perdana");
                    } else if (itemId == R.id.menu_ewallet){
                        categoryProduct = "E-Wallet";
                        category.setText("Wallet");
                    } else if (itemId == R.id.menu_aksesoris){
                        categoryProduct = "Aksesoris";
                        category.setText("Aksesoris");
                    }
                    return true;
                }
            });
            popupMenu.show();
        });

        createNewProduct.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        createNewProduct.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (category.getText().equals("--PILIH KATEGORI--")){
                    AlertDialog.Builder failed = new AlertDialog.Builder(ProductActivity.this);
                    failed.setCancelable(false);
                    failed.setTitle("Error !");
                    failed.setMessage("Kategori belum dipilih");

                    failed.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            addProductDialog();
                        }
                    });
                    failed.show();
                } else if (name.getText().toString().length() == 0){
                    AlertDialog.Builder failed = new AlertDialog.Builder(ProductActivity.this);
                    failed.setCancelable(false);
                    failed.setTitle("Error !");
                    failed.setMessage("Nama produk belum diisi");

                    failed.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            addProductDialog();
                        }
                    });
                    failed.show();
                } else if (hpp.getText().toString().length() == 0 || hpp.getText().equals("Rp 0")){
                    AlertDialog.Builder failed = new AlertDialog.Builder(ProductActivity.this);
                    failed.setCancelable(false);
                    failed.setTitle("Error !");
                    failed.setMessage("Harga Beli belum disetting");

                    failed.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            addProductDialog();
                        }
                    });
                    failed.show();
                } else if (price.getText().toString().length() == 0 || price.getText().equals("Rp 0")){
                    AlertDialog.Builder failed = new AlertDialog.Builder(ProductActivity.this);
                    failed.setCancelable(false);
                    failed.setTitle("Error !");
                    failed.setMessage("Harga Beli belum disetting");

                    failed.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            addProductDialog();
                        }
                    });
                    failed.show();
                } else {
                    submitNewProduct();
                }
            }
        });

        createNewProduct.show();
    }

    private void submitNewProduct() {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbProduct = root.child("Product");

        BigDecimal valueofHargaModal = MoneyTextWatcher.parseCurrencyValue(hpp.getText().toString());
        BigDecimal valueofHargaJual = MoneyTextWatcher.parseCurrencyValue(price.getText().toString());
        String strHargaModal = String.valueOf(valueofHargaModal);
        String strHargaJual = String.valueOf(valueofHargaJual);

        Product product = new Product();
        product.setCategory(categoryProduct);
        product.setName(name.getText().toString());
        product.setHargaModal(Double.parseDouble(strHargaModal));
        product.setHargaJual(Double.parseDouble(strHargaJual));
        product.setIsStockUnit(rdStockUnit);

        dbProduct.child(categoryProduct).push().setValue(product);
        Toast.makeText(this, "Produk berhasil ditambah", Toast.LENGTH_SHORT).show();
    }
}