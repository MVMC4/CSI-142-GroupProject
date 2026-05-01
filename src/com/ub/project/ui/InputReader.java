package com.ub.project.ui;

import java.util.Scanner;

public class InputReader {
    private Scanner scanner = new Scanner(System.in);

    // Ensures we get a valid number within a specific range (e.g., 1 to 5)
    public int getIntInput(String prompt, int min, int max) {
        int choice = -1;
        while (true) {
            System.out.print(prompt + " (" + min + "-" + max + "): ");
            String input = scanner.nextLine();
            //
            try {
                choice = Integer.parseInt(input);
                if (choice >= min && choice <= max)
                    return choice;
                System.out.println("Error: Please choose a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Error: That is not a valid number.");
            }
        }
    }

    public String getStringInput(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    public void waitForEnter() {
        System.out.print("\nPress [ENTER] to return to menu...");
        scanner.nextLine();
    }
}