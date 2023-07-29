package com.example.abdancell.Model;

public class OrderItem {
    private String Id,ProductId,ProductName,ProductKategori;
    int Qty;
    double HargaModal,HargaJual,SubtotalModal,SubtotalMargin,SubtotalSales;

    public OrderItem() {
    }

    public OrderItem(String id, String productId, String productName, String productKategori, int qty, double hargaModal, double hargaJual, double subtotalModal, double subtotalMargin, double subtotalSales) {
        Id = id;
        ProductId = productId;
        ProductName = productName;
        ProductKategori = productKategori;
        Qty = qty;
        HargaModal = hargaModal;
        HargaJual = hargaJual;
        SubtotalModal = subtotalModal;
        SubtotalMargin = subtotalMargin;
        SubtotalSales = subtotalSales;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductKategori() {
        return ProductKategori;
    }

    public void setProductKategori(String productKategori) {
        ProductKategori = productKategori;
    }

    public int getQty() {
        return Qty;
    }

    public void setQty(int qty) {
        Qty = qty;
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

    public double getSubtotalModal() {
        return SubtotalModal;
    }

    public void setSubtotalModal(double subtotalModal) {
        SubtotalModal = subtotalModal;
    }

    public double getSubtotalMargin() {
        return SubtotalMargin;
    }

    public void setSubtotalMargin(double subtotalMargin) {
        SubtotalMargin = subtotalMargin;
    }

    public double getSubtotalSales() {
        return SubtotalSales;
    }

    public void setSubtotalSales(double subtotalSales) {
        SubtotalSales = subtotalSales;
    }
}
