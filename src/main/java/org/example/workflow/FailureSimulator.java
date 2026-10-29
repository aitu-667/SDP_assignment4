package org.example.workflow;

public class FailureSimulator {
    public static boolean failAtB = false;
    public static boolean failAtC = false;

    public static void reset() {
        failAtB = false;
        failAtC = false;
    }
}
