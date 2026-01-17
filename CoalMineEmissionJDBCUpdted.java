package com.coalemission.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CoalMineEmissionJDBCUpdted {
    private final Scanner sc;
    private Connection con;

    public CoalMineEmissionJDBCUpdted() {
        sc = new Scanner(System.in);
        try {
            con = Getconnection.getConnection();
        } catch (SQLException e) {
            handleError("Failed to establish database connection.", e);
        }
    }

    public static void main(String[] args) {
        CoalMineEmissionJDBCUpdted app = new CoalMineEmissionJDBCUpdted();
        app.run();
    }

    public void run() {
        int choice;
        do {
            printMenu();
            choice = getUserChoice();

            switch (choice) {
                case 1:
                    insertData();
                    break;
                case 2:
                    showData();
                    break;
                case 3:
                    updateData();
                    break;
                case 4:
                    delData();
                    break;
                case 5:
                    showEmissionReport();
                    break;
                case 6:
                    System.out.println("\nExiting the system. Goodbye!\n");
                    break;
                default:
                    System.out.println("\nInvalid choice. Please select a valid option (1-6).\n");
            }
        } while (choice != 6);
    }

    private void printMenu() {
        System.out.println("===========================================================");
        System.out.println("             Carbon Emission Analysis System");
        System.out.println("===========================================================");
        System.out.println("1. Insert Coal Data");
        System.out.println("2. Show All Emission Data");
        System.out.println("3. Update Emission Factor");
        System.out.println("4. Delete Record");
        System.out.println("5. Show Emission Report with Analysis");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    private int getUserChoice() {
        try {
            return sc.nextInt();
        } catch (InputMismatchException e) {
            sc.nextLine(); // clear the invalid input
            return -1; // return an invalid choice
        }
    }

    public void insertData() {
        printHeader("Insert New Emission Record");
        try {
            System.out.print("Enter Mine ID (e.g., MINEXX): ");
            String mid = sc.next();
            System.out.print("Enter Activity Type (Excavation, Transportation, Drilling, Hauling): ");
            String at = sc.next();
            System.out.print("Enter Fuel Consumed (in liters): ");
            double fuel = getPositiveDouble();
            System.out.print("Enter Emission Factor (EF): ");
            double ef = getPositiveDouble();
            sc.nextLine(); // Consume newline
            System.out.print("Enter Equipment Type: ");
            String equip = sc.nextLine();

            String sql = "INSERT INTO emissions(mineID, activityType, equipment, fuelConsumed, emissionFactor) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, mid);
                ps.setString(2, at);
                ps.setString(3, equip);
                ps.setDouble(4, fuel);
                ps.setDouble(5, ef);
                int i = ps.executeUpdate();
                if (i == 1) {
                    System.out.println("\n[SUCCESS] Record successfully added to the emissions database.\n");
                }
            }
        } catch (SQLException e) {
            handleError("Could not insert data into database.", e);
        }
    }

    public void updateData() {
        printHeader("Update Emission Factor");
        try {
            System.out.print("Enter the ID of the record to update: ");
            int m_id = getPositiveInt();
            System.out.print("Enter the new Emission Factor (EF): ");
            double e_f = getPositiveDouble();

            String sql = "UPDATE emissions SET emissionFactor = ? WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setDouble(1, e_f);
                ps.setInt(2, m_id);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("\n[SUCCESS] Emission factor updated for record ID: " + m_id + "\n");
                } else {
                    System.out.println("\n[NOTICE] No record found with ID: " + m_id + "\n");
                }
            }
        } catch (SQLException e) {
            handleError("Could not update emission factor.", e);
        }
    }

    public void delData() {
        printHeader("Delete Emission Record");
        try {
            System.out.print("Enter the ID of the record to delete: ");
            int m_id = getPositiveInt();

            String sql = "DELETE FROM emissions WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, m_id);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("\n[SUCCESS] Record with ID " + m_id + " deleted.\n");
                } else {
                    System.out.println("\n[NOTICE] No record found with ID: " + m_id + "\n");
                }
            }
        } catch (SQLException e) {
            handleError("Could not delete record.", e);
        }
    }

    public void showData() {
        printHeader("All Emission Records");
        System.out.println("----------------------------------------------------------------------------");
        System.out.printf("%-10s %-15s %-15s %-10s %-10s %-15s%n", "Mine ID", "Activity", "Equipment", "Fuel(L)", "EF", "Emission(kg)");
        System.out.println("----------------------------------------------------------------------------");

        double totalEmission = 0;
        String query = "SELECT * FROM emissions";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String mineID = rs.getString("mineID");
                String activityType = rs.getString("activityType");
                String equipment = rs.getString("equipment");
                double fuel = rs.getDouble("fuelConsumed");
                double ef = rs.getDouble("emissionFactor");
                double emission = fuel * ef;

                System.out.printf("%-10s %-15s %-15s %-10.2f %-10.2f %-15.2f%n", mineID, activityType, equipment, fuel, ef, emission);
                totalEmission += emission;
            }
            System.out.println("\nTotal Emissions from All Activities: " + totalEmission + " kg CO2\n");
        } catch (SQLException e) {
            handleError("Could not retrieve emission data.", e);
        }
    }

    public void showEmissionReport() {
        printHeader("Emission Report with Analysis");
        System.out.println("-----------------------------------------------------------");
        System.out.printf("%-10s %-15s %-10s %-10s %-15s%n", "Mine ID", "Equipment", "Fuel(L)", "EF", "Emission(kg)");
        System.out.println("-----------------------------------------------------------");

        String query = "SELECT * FROM emissions";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                String mineID = rs.getString("mineID");
                String equipment = rs.getString("equipment");
                double fuel = rs.getDouble("fuelConsumed");
                double ef = rs.getDouble("emissionFactor");
                double emission = fuel * ef;

                System.out.printf("%-10s %-15s %-10.2f %-10.2f %-15.2f%n", mineID, equipment, fuel, ef, emission);

                if (emission <= 100) {
                    System.out.println("Status: Safe");
                    System.out.println("-> No remedial action required. Emissions are within acceptable limits.");
                } else if (emission <= 300) {
                    System.out.println("Status: Moderate");
                    System.out.println("-> Recommended: Basic carbon sinks (e.g., tree planting).");
                } else if (emission <= 600) {
                    System.out.println("Status: High");
                    System.out.println("-> Recommended: Medium remedial actions (e.g., afforestation, soil carbon techniques).");
                } else {
                    System.out.println("Status: Critical");
                    System.out.println("-> Recommended: Aggressive measures (e.g., wetland restoration, large-scale reforestation).");
                }
                System.out.println("-----------------------------------------------------------");
            }
        } catch (SQLException e) {
            handleError("Could not generate emission report.", e);
        }
    }

    private double getPositiveDouble() {
        double value = -1;
        while (value < 0) {
            try {
                value = sc.nextDouble();
                if (value < 0) {
                    System.out.print("Please enter a non-negative value: ");
                }
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a number: ");
                sc.next(); // clear the invalid input
            }
        }
        return value;
    }

    private int getPositiveInt() {
        int value = -1;
        while (value < 0) {
            try {
                value = sc.nextInt();
                if (value < 0) {
                    System.out.print("Please enter a non-negative integer: ");
                }
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter an integer: ");
                sc.next(); // clear the invalid input
            }
        }
        return value;
    }

    private void printHeader(String title) {
        System.out.println("\n===========================================================");
        System.out.println("  " + title);
        System.out.println("===========================================================");
    }

    private void handleError(String message, Exception e) {
        System.err.println("ERROR: " + message);
        e.printStackTrace();
    }
}