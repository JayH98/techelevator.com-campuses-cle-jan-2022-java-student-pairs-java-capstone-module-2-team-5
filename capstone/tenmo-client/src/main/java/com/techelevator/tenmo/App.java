package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
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

                // TODO refactor into separate private helper method???
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

    private void viewPendingRequests() {
        // TODO Auto-generated method stub
    }

    private void sendBucks() {
        // TODO have tenmoService call with transfer( fromAccountId, toAccountId, amount)
        /*todo have transfer() return a boolean on success or failure and send
        boolean to consoleService to print message
         */
        consoleService.printSendTeBucksHeader();
    }

    private void requestBucks() {
        // TODO Auto-generated method stub

    }

}
