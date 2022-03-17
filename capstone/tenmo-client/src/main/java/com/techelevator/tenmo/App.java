package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TenmoService;

import java.text.NumberFormat;
import java.util.Scanner;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final TenmoService tenmoService = new TenmoService(API_BASE_URL);
    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    //String username = consoleService.promptForString("Username: ");
    //        String password = consoleService.promptForString("Password: ");
    //        String token = authenticationService.login(username, password);
    //        if (token != null) {
    //            auctionService.setAuthToken(token);
    //        } else {
    //            consoleService.printErrorMessage();
    //        }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);

        if (currentUser == null) {
            consoleService.printErrorMessage();
        } else {
            String token = currentUser.getToken();
            if (token != null) {
                tenmoService.setAuthToken(currentUser.getToken());
            } else {
                consoleService.printErrorMessage();
            }
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void viewCurrentBalance() {
        double balance = tenmoService.getUserBalance(currentUser.getUser().getId());
        consoleService.printCurrentBalance(balance);
        // TODO only view our current balance as double to change to Big Decimal
    }

    private void viewTransferHistory() {
        Transfer[] history = tenmoService.getTransferHistory(currentUser.getUser().getId());
        // Print the transfer history header even if there are no transfers
        if (history != null) {
            while (true) {
                consoleService.printTransferHistoryHeader();
                for (Transfer transfer : history) {
                    String display = transferDisplayString(transfer);
                    consoleService.printString(display);
                }

                // TODO refactor into separate private helper method???
                int transferId = consoleService.promptForInt("\nPlease enter transfer ID to view details (0 to cancel): ");
                if (transferId == 0)
                    break;
                boolean found = false;
                for (Transfer transfer : history) {
                    if (transfer.getTransferId() == transferId) {
                        consoleService.printTransferHeader();
                        consoleService.printString(transfer.toString());
                        consoleService.pause();
                        found = true;
                    }
                }
                if (!found) {
                    consoleService.transferNotFoundMessage();
                }
            }
        }
        else
            System.out.println("You currently have no past transfers.");
    }

    private void viewPendingRequests() {
        // TODO Auto-generated method stub
        boolean isValid;
        Transfer pendingTransfer = null;
        boolean requestDealtWith = false;
        double currentUserBalance = tenmoService.getUserBalance(currentUser.getUser().getId());
        do {
            isValid = false;
            consoleService.printPendingRequestsHeader();
            Transfer[] pendingTransfers = tenmoService.viewPendingTransfers(currentUser.getUser().getId());

            if (pendingTransfers != null) {
                for (Transfer transfer : pendingTransfers) {
                    System.out.println(transfer.getTransferId() + "\t\t" + transfer.getAccountToUsername() + "\t\t\t\t" + transfer.getAmount());
                }
                int transferToManage = consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel): ");
                if (transferToManage == 0) {
                    return;
                }
                for (Transfer transfer : pendingTransfers) {
                    if (transferToManage == transfer.getTransferId()) {
                        pendingTransfer = transfer;
                        isValid = true;
                        break;
                    }
                }
                if (pendingTransfer == null) {
                    consoleService.printString("\nInvalid transfer selection. Please try again.\n");
                }
            } else {
                consoleService.printString("\nYou do not have any pending transfers.\n");
                return;
            }
        } while (!isValid);

        consoleService.printApprovalHeader();
        int approval = consoleService.promptForInt("Please choose an option: ");
        switch (approval) {
            case 0:
                System.out.println("Canceling approval. Returning to main menu.");
                break;
            case 1:
                if (currentUserBalance < pendingTransfer.getAmount()) {
                    System.out.println("Sorry. You don't have enough money to approve this request. Returning to main menu");
                    break;
                }
                pendingTransfer.setTransferStatusId(2);
                requestDealtWith = tenmoService.acceptOrRejectRequest(pendingTransfer);
                if (requestDealtWith) {
                    consoleService.printString("Request successfully approved! Money will be taken from your account shortly.");
                    break;
                } else {
                    consoleService.printString("Error. Something went wrong during the process. Please contact the creator of this app.");
                    break;
                }
            case 2:
                pendingTransfer.setTransferStatusId(3);
                requestDealtWith = tenmoService.acceptOrRejectRequest(pendingTransfer);
                if (requestDealtWith) {
                    consoleService.printString("Request successfully rejected! I'm sure they didn't really need it.");
                    break;
                } else {
                    consoleService.printString("Error. Something went wrong during the process. Please contact the creator of this app.");
                    break;
                }
            default:
                System.out.println("Invalid selection. Returning to main menu");
                break;
        }



    }

    private void sendBucks() {
        boolean transferSuccessful = true;
        boolean onSendMoneyMenu = true;
        Transfer transfer = new Transfer();     // make a empty transferObject

        consoleService.printUsersHeader();
        User[] users = tenmoService.listAllUsers();
        displayUsers(users);

        while (onSendMoneyMenu) {
            int toId = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel): ");  //  request an int that is a UserId
            if (toId == 0) {
                consoleService.printString("\nExiting menu\n");
                return;
            }
            double moneyToTransfer = consoleService.promptForDouble("Enter amount: ");                  // query the user for the amount of $ to send
            double currentUserBalance = tenmoService.getUserBalance(currentUser.getUser().getId());         // getting the balance

            if (moneyToTransfer <= 0.00) {                   // check the amount is greater than $0.00
                consoleService.printString("This amount is not valid.");
                continue;
            }
            if (moneyToTransfer > currentUserBalance) {
                consoleService.printString("Sorry. You do not have enough money to send $" + moneyToTransfer);      // check the amount cannot be more than the balance
                continue;
            }
            transfer.setAmount(moneyToTransfer);
            transfer.setFromUserId(currentUser.getUser().getId());          // get the current userId from currentUser and set it to fromId in transfer object

            boolean isValid = false;
            for (User user : users) {           // make sure toId is valid
                if (user.getId() == toId) {
                    isValid = true;
                    break;
                }
            }
            if (isValid) {
                if (currentUser.getUser().getId() != toId) {            // checks ids are not the same
                    transfer.setToUserId(toId);                                        //set the toId in the transferObject
                    transferSuccessful = tenmoService.sendMoney(transfer);
                    onSendMoneyMenu = false;
                } else {
                    consoleService.printString("Nice try, but you cannot send money to yourself! Why don't you try giving to someone else...");
                }
            } else {
                consoleService.printString("Invalid Id selection. This user does not exist.");
            }
        }
        if (!transferSuccessful) {
            consoleService.printString("We're sorry. An error occurred during the process. Returning to main menu.");
        } else
            consoleService.printString("Money sent successfully! Returning to main menu");
    }

    private void requestBucks() {
        // TODO Auto-generated method stub
        Transfer transfer = new Transfer();

        consoleService.printUsersHeader();
        User[] users = tenmoService.listAllUsers();
        displayUsers(users);

        int moneyFromId = consoleService.promptForInt("Enter ID of user you are requesting from (0 to cancel): ");
        double moneyToRequest = consoleService.promptForDouble("Enter amount: ");

        if (moneyToRequest <= 0) {
            System.out.println("Error. Invalid cash amount");
            return;
        }

        boolean isValid = false;
        for(User user : users){
            if(user.getId() == moneyFromId){
                isValid = true;
                break;
            }
        }
        if(isValid && currentUser.getUser().getId() != moneyFromId){
            //set the moneyFromId in the transferObject
            transfer.setFromUserId(moneyFromId);
            transfer.setToUserId(currentUser.getUser().getId());
            transfer.setTransferTypeId(1);
            transfer.setTransferStatusId(1);
            transfer.setAmount(moneyToRequest);
            Transfer successTransfer = tenmoService.requestMoney(transfer);
            if (successTransfer == null) {
                System.out.println("We're sorry. An error occurred during the transfer process. Returning to menu.");
            }
            else System.out.println("Request sent successfully. Returning to menu");
        }else {
            consoleService.printString("Invalid Id");
        }

    }
    private String transferDisplayString(Transfer transfer) {
        // Create empty display string
        String display = "";

        // Add id to string
        display += transfer.getTransferId() + "\t";

        String currentUserName = currentUser.getUser().getUsername();
        String fromName = transfer.getAccountFromUsername();
        String toName = transfer.getAccountToUsername();

        if (currentUserName.equals(fromName)) {
            display += "TO: " + toName + "\t\t\t";
        } else {
            display += "FROM: " + fromName + "\t\t\t";
        }

        double amount = transfer.getAmount();
        display += NumberFormat.getCurrencyInstance().format(amount);

        return display;
    }
    private void displayUsers(User[] users) {
        for (User user : users) {
            consoleService.printString(user.toString());
        }
    }

}
