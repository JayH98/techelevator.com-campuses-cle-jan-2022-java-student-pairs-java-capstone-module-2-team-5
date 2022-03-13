package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.UserCredentials;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    //Who do I want to send money to
    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    //How much money do I want to send
    public Double promptForDouble(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void printCurrentBalance(double balance) {
        System.out.println("Your current account balance is: " +
                NumberFormat.getCurrencyInstance().format(balance));
    }
    //todo send TE bucks display Method in ConsolService.java
    public void printUsersHeader() {
        System.out.println("-------------------------------------------\n" +
                "Users\n" +
                "ID          Name\n" +
                "-------------------------------------------");
    }

    public void printTransferHistoryHeader() {
        System.out.println("-------------------------------------------\n" +
                "Transfers\n" +
                "ID          From/To                 Amount\n" +
                "-------------------------------------------");
    }

    public void printTransferHeader() {
        System.out.println("--------------------------------------------\n" +
                "Transfer Details\n" +
                "--------------------------------------------");
    }

    public void printApprovalHeader() {
        System.out.println("1: Approve\n" +
                "2: Reject\n" +
                "0: Don't approve or reject\n" +
                "------------------------------------------\n");
    }

    public void printPendingRequestsHeader() {
        System.out.println("-------------------------------------------\n" +
                "Pending Transfers\n" +
                "ID          To                 Amount\n" +
                "-------------------------------------------");
    }


    public void printString(String string) {
        System.out.println(string);
    }

    //todo write the useTransfers display in ConsolService.java
    //todo write the transferId display in ConsolService.java
    //todo (optional) create the requesting TE bucks display in ConsolService.java
    //todo (optional) pending request

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }



    public void transferNotFoundMessage() {
        System.out.println("\nTransaction not found!!\n");
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

}
