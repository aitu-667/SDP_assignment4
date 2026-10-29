package org.example.subsystemb;

public class PackingStation {
    private final PackingRepository repository;
    private final WarehousePicker picker;

    public PackingStation(PackingRepository repository, WarehousePicker picker) {
        this.repository = repository;
        this.picker = picker;
    }

    public void packOrder(String orderId, int quantity) {
        String box = picker.calculateBoxSize(quantity);
        PackingTask task = new PackingTask(orderId, box, PackingTask.Status.NEW);
        task.setStatus(PackingTask.Status.PACKED);
        repository.save(task);
    }

    public void unpackOrder(String orderId) {
        PackingTask task = repository.findByOrderId(orderId);
        if (task != null) {
            task.setStatus(PackingTask.Status.CANCELLED);
            repository.save(task);
        }
    }
}
