package com.example.abdancell.Model;

import java.util.HashMap;

public class Order {
    String DateTrx,CreatedBy,CreatedDateTime,Notes;
    int SubtotalQty;
    double SubtotalHPP,SubtotalSales,SubtotalMargin;
    HashMap<String,OrderItem> orderItem;

    public Order() {
    }

    public Order(String dateTrx, String createdBy, String createdDateTime, String notes, int subtotalQty, double subtotalHPP, double subtotalSales, double subtotalMargin, HashMap<String, OrderItem> orderItem) {
        DateTrx = dateTrx;
        CreatedBy = createdBy;
        CreatedDateTime = createdDateTime;
        Notes = notes;
        SubtotalQty = subtotalQty;
        SubtotalHPP = subtotalHPP;
        SubtotalSales = subtotalSales;
        SubtotalMargin = subtotalMargin;
        this.orderItem = orderItem;
    }

    public String getDateTrx() {
        return DateTrx;
    }

    public void setDateTrx(String dateTrx) {
        DateTrx = dateTrx;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getCreatedDateTime() {
        return CreatedDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        CreatedDateTime = createdDateTime;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public int getSubtotalQty() {
        return SubtotalQty;
    }

    public void setSubtotalQty(int subtotalQty) {
        SubtotalQty = subtotalQty;
    }

    public double getSubtotalHPP() {
        return SubtotalHPP;
    }

    public void setSubtotalHPP(double subtotalHPP) {
        SubtotalHPP = subtotalHPP;
    }

    public double getSubtotalSales() {
        return SubtotalSales;
    }

    public void setSubtotalSales(double subtotalSales) {
        SubtotalSales = subtotalSales;
    }

    public double getSubtotalMargin() {
        return SubtotalMargin;
    }

    public void setSubtotalMargin(double subtotalMargin) {
        SubtotalMargin = subtotalMargin;
    }

    public HashMap<String, OrderItem> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(HashMap<String, OrderItem> orderItem) {
        this.orderItem = orderItem;
    }
}
