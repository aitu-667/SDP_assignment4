package org.example.subsystemb;

public class WarehousePicker {
    public String calculateBoxSize(int quantity) {
        if (quantity <= 2) return "SMALL";
        if (quantity <= 10) return "MEDIUM";
        return "LARGE";
    }
}
