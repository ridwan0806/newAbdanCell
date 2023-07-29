package com.example.abdancell.Model;

public class Product {
    String Category,Name,IsStockUnit;
    double HargaModal,HargaJual;

    public Product() {
    }

    public Product(String category, String name, String isStockUnit, double hargaModal, double hargaJual) {
        Category = category;
        Name = name;
        IsStockUnit = isStockUnit;
        HargaModal = hargaModal;
        HargaJual = hargaJual;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getIsStockUnit() {
        return IsStockUnit;
    }

    public void setIsStockUnit(String isStockUnit) {
        IsStockUnit = isStockUnit;
    }

    public double getHargaModal() {
        return HargaModal;
    }

    public void setHargaModal(double hargaModal) {
        HargaModal = hargaModal;
    }

    public double getHargaJual() {
        return HargaJual;
    }

    public void setHargaJual(double hargaJual) {
        HargaJual = hargaJual;
    }
}
