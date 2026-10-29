package org.example;

import org.example.facade.AdminFacade;
import org.example.facade.FulfillmentFacade;
import org.example.subsystema.*;
import org.example.subsystemb.*;
import org.example.subsystemc.*;
import org.example.workflow.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String dbUrl = "jdbc:sqlite:fulfillment.db";

        InventoryRepository invRepo = new InventoryRepository(dbUrl);
        OrderValidator validator = new OrderValidator();
        InventoryManager invManager = new InventoryManager(invRepo, validator);

        PackingRepository packRepo = new PackingRepository(dbUrl);
        WarehousePicker picker = new WarehousePicker();
        PackingStation packStation = new PackingStation(packRepo, picker);

        ShippingRepository shipRepo = new ShippingRepository(dbUrl);
        PaymentGateway paymentGateway = new PaymentGateway();
        ShippingCarrierAPI carrierAPI = new ShippingCarrierAPI(shipRepo, paymentGateway);

        ChainProgressRepository progressRepo = new ChainProgressRepository(dbUrl);

        FulfillmentFacade fulfillmentFacade = new FulfillmentFacade(invManager, packStation, carrierAPI, progressRepo);
        AdminFacade adminFacade = new AdminFacade(invRepo, packRepo, shipRepo, progressRepo, fulfillmentFacade);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- E-COMMERCE FULFILLMENT CLI ---");
            System.out.println("1. Run Happy Path (A -> B -> C)");
            System.out.println("2. Simulate Failure at Step B (Fails B -> Rollback A)");
            System.out.println("3. Simulate Failure at Step C (Fails C -> Rollback B -> Rollback A)");
            System.out.println("4. Inspect Order Status");
            System.out.println("5. Trigger Manual Rollback (Admin)");
            System.out.println("6. Exit");
            System.out.print("Select option: ");

            String opt = scanner.nextLine();
            FailureSimulator.reset();

            if ("6".equals(opt)) break;

            switch (opt) {
                case "1":
                    System.out.print("Enter Order ID: ");
                    String id1 = scanner.nextLine();
                    boolean res1 = fulfillmentFacade.processFulfillment(id1, "ITEM-101", 3, 150.0);
                    System.out.println("Result: " + (res1 ? "SUCCESS" : "FAILED"));
                    break;
                case "2":
                    System.out.print("Enter Order ID: ");
                    String id2 = scanner.nextLine();
                    FailureSimulator.failAtB = true;
                    boolean res2 = fulfillmentFacade.processFulfillment(id2, "ITEM-101", 3, 150.0);
                    System.out.println("Result: " + (res2 ? "SUCCESS" : "FAILED (RollbackExecuted)"));
                    break;
                case "3":
                    System.out.print("Enter Order ID: ");
                    String id3 = scanner.nextLine();
                    FailureSimulator.failAtC = true;
                    boolean res3 = fulfillmentFacade.processFulfillment(id3, "ITEM-101", 3, 150.0);
                    System.out.println("Result: " + (res3 ? "SUCCESS" : "FAILED (RollbackExecuted)"));
                    break;
                case "4":
                    System.out.print("Enter Order ID: ");
                    String id4 = scanner.nextLine();
                    adminFacade.printOrderStatus(id4);
                    break;
                case "5":
                    System.out.print("Enter Order ID for manual rollback: ");
                    String id5 = scanner.nextLine();
                    adminFacade.manualCompensation(id5);
                    System.out.println("Manual compensation invoked.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
