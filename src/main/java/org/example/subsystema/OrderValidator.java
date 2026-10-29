package org.example.subsystema;

public class OrderValidator {
    public boolean validate(String itemSku, int quantity) {
        if (itemSku == null || itemSku.trim().isEmpty()) {
            return false;
        }
        return quantity > 0 && quantity <= 100;
    }
}
