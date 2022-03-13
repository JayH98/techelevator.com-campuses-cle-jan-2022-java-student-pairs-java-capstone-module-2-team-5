package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TenmoService;

import java.text.NumberFormat;

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

                // TODO refactor into separate private helper method
                int transferId = consoleService.promptForInt("\nPlease enter transfer ID to view details (0 to cancel): ");
                if (transferId == 0)
                    break;
                boolean found = false;
                for (Transfer transfer : history) {
                    if (transfer.getTransferId() == transferId) {
                        consoleService.printTransferHeader();
                        consoleService.printString(transfer.toString());
                        found = true;
                    }
                }
                if (!found) {
                    consoleService.transferNotFoundMessage();
                }
            }
        }
    }

//    private void printSingleTransfer(Transfer transfer) {
//
//    }

    private String transferDisplayString(Transfer transfer) {
        // Create empty display string
        String display = "";

        // Add id to string
        display += transfer.getTransferId() + "\t";

        String currentUserName = currentUser.getUser().getUsername();
        String fromName = transfer.getAccountFromUsername();
        String toName = transfer.getAccountToUsername();

        if (currentUserName.equals(fromName)) {
            display += "TO: " + toName + "\t";
        } else {
            display += "FROM: " + fromName + "\t";
        }

        double amount = transfer.getAmount();
        display += NumberFormat.getCurrencyInstance().format(amount);

        return display;
    }

    private void displayUsers(User[] users){
        for(User user : users){
            consoleService.printString(user.toString());
        }
    }


    private void viewPendingRequests() {
        // TODO Auto-generated method stub

    }

    private void sendBucks() {
        // TODO have tenmoService call with transfer( fromUserId, toUserId, amount)
        /*todo have transfer() return a boolean on success or failure and send
        boolean to consoleService to print message
         */

        consoleService.printUsersHeader();
        User[] users = tenmoService.listAllUsers();
        displayUsers(users);
        // make a empty transferObject
        Transfer transfer = new Transfer();
        //  request an int that is  a UserId
        int toId = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel): ");
        // query the user for the amount of $ to send
        double moneyToTransfer = consoleService.promptForDouble("Enter amount: ");
        // getting the balance
        double currentUserBalance = tenmoService.getUserBalance(currentUser.getUser().getId());
        // check the amount must be greater than $0.00
        if(moneyToTransfer <= 0.00){
            consoleService.printString("This amount is not valid.");
            return;
        }
        // check the amount cannot be more than the balance
        if(moneyToTransfer > currentUserBalance){
            consoleService.printString("This amount is not valid.");
            return;
        }
        transfer.setAmount(moneyToTransfer);
        // get the current userId from currentUser
        // set the requested Id in the transferObject
        transfer.setFromUserId(currentUser.getUser().getId());
        // make sure toId is valid

        boolean isValid = false;
        for(User user : users){
            if(user.getId() == toId){
                isValid = true;
                break;
            }
        }
        boolean transferSuccessful = false;
        // make checks id cannot be the same
        if(isValid && currentUser.getUser().getId() != toId){
            //set the toId in the transferObject
            transfer.setToUserId(toId);
            transferSuccessful = tenmoService.sendMoney(transfer);
        }else {
            consoleService.printString("Invalid Id selection");
        }

        if (!transferSuccessful) {
            System.out.println("We're sorry. An error occurred during the process. Returning to menu.");
        }
    }

    private void requestBucks() {
        // TODO Auto-generated method stub
        Transfer transfer = new Transfer();

        consoleService.printUsersHeader();
        User[] users = tenmoService.listAllUsers();
        displayUsers(users);

        int toId = consoleService.promptForInt("Enter ID of user you are requesting from (0 to cancel): ");

        double moneyToRequest = consoleService.promptForDouble("Enter amount: ");

        if (moneyToRequest <= 0) {
            System.out.println("Error. Invalid cash amount");
            return;
        }


        boolean isValid = false;
        for(User user : users){
            if(user.getId() == toId){
                isValid = true;
                break;
            }
        }
        if(isValid && currentUser.getUser().getId() != toId){
            //set the toId in the transferObject
            transfer.setFromUserId(currentUser.getUser().getId());
            transfer.setToUserId(toId);                             // In this instance, the toId is used as the person the request is being sent to
            transfer.setTransferTypeId(1);
            transfer.setTransferStatusId(1);
            transfer.setAmount(moneyToRequest);
            Transfer successTransfer = tenmoService.requestMoney(transfer);
            if (successTransfer == null) {
                System.out.println("We're sorry. An error occurred during the transfer process. Returning to menu.");
            }
        }else {
            consoleService.printString("Invalid Id");
        }

    }

}
