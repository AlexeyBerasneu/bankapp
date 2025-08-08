package com.bankapp.service;

import com.bankapp.model.Account;
import com.bankapp.model.User;
import com.bankapp.util.OperationType;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class OperationsConsoleListener {

    private final UserService userService;
    private final AccountService accountService;

    public OperationsConsoleListener(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    public void listen() {
        Scanner scanner = new Scanner(System.in);
        printMenu();
        while (true) {
            String operation = scanner.nextLine().trim().toUpperCase();
            if (operation == null || operation.isEmpty()) {
                System.out.println("Please enter a valid operation");
                printMenu();
                continue;
            }
            switch (operation) {
                case "USER_CREATE" -> {
                    System.out.println(operation + "\nEnter login for new user:");
                    String login = scanner.nextLine().trim();
                    if (validLogin(login)) {
                        System.out.println("Login already exists or wrong!!!");
                        printMenu();
                        break;
                    }
                    System.out.println("User created: " + userService.creteUser(login));
                    printMenu();
                }
                case "SHOW_ALL_USERS" -> {
                    System.out.println("List of all users:");
                    if(userService.getAll().isEmpty()) {
                        System.out.println("No users found");
                        printMenu();
                        break;
                    }
                    for (User user : userService.getAll()) {
                        System.out.println(user);
                    }
                    printMenu();
                }
                case "ACCOUNT_CREATE" -> {
                    if (userService.getAll().isEmpty()) {
                        System.out.println("List of account is empty !!!");
                        printMenu();
                        break;
                    }
                    System.out.print("Enter the user id for which to create an account.\n" +
                            "List of User ids: ");
                    Integer id = validUserId(scanner);
                    if (id == null) {
                        break;
                    }
                    Account account = userService.addAccount(id);
                    System.out.println("New account created with ID: "
                            + account.getId() + " for user: "
                            + userService.getUserById(id).getLogin());
                    printMenu();
                }
                case "ACCOUNT_DEPOSIT" -> {
                    System.out.print("Enter account ID from list: ");
                    Integer id = validAccountId(scanner);
                    if (id == null) {
                        break;
                    }
                    System.out.print("Enter amount to deposit: ");
                    Double amount = validAmount(scanner);
                    if (amount == null) {
                        break;
                    }
                    accountService.deposit(id,amount);
                    System.out.println("Amount " + amount + " deposited to account ID: " + id);
                    printMenu();
                }
                case "ACCOUNT_WITHDRAW" -> {
                    System.out.print("Enter account ID to withdraw from list: ");
                    Integer id = validAccountId(scanner);
                    if (id == null) {
                        break;
                    }
                    System.out.print("Enter amount to withdraw: ");
                    Double amount = validAmount(scanner);
                    if (amount == null) {
                        break;
                    }
                    if (!accountService.withdraw(id,amount)){
                        printMenu();
                        break;
                    }
                    System.out.println("Withdraw successful !!!");
                    printMenu();
                }

                case "ACCOUNT_TRANSFER"->{
                    //System.out.println("List of account ids:" + accountService.getAllAccountIds());
                    System.out.print("Enter source account ID from list: ");
                    Integer idSource = validAccountId(scanner);
                    if (idSource == null) {
                        break;
                    }
                    System.out.print("Enter target account ID:");
                    Integer idTarget = validAccountId(scanner);
                    if (idTarget == null) {
                        break;
                    }
                    System.out.print("Enter amount to transfer:");
                    Double amount = validAmount(scanner);
                    if (amount == null) {
                        break;
                    }
                    accountService.transferMoney(idSource,idTarget,amount);
                    printMenu();
                }

                case "ACCOUNT_CLOSE" -> {
                    System.out.print("Enter account ID to close from list: ");
                    Integer id = validAccountId(scanner);
                    if (id == null) {
                        break;
                    }
                    if (!accountService.closeAccount(id)){
                        printMenu();
                        break;
                    }
                    printMenu();
                }
                case "EXIT" -> {
                    return;
                }
                default -> {
                    System.out.println("Invalid operation: " + operation);
                    printMenu();
                }
            }
        }
    }

    private void printMenu() {
        System.out.println("\nPlease enter one of operation type:");
        for (OperationType operationType : OperationType.values()) {
            System.out.println("-" + operationType.name());
        }
    }

    private boolean validLogin(String login) {
        return login.isBlank() || userService.getUserByLogin(login);
    }

    private Integer validUserId(Scanner scanner) {
        userService.getAll().stream().forEach(user -> {
            System.out.print(user.getId() + " ");
        });
        System.out.println();
        try {
            Integer id = Integer.parseInt(scanner.nextLine().trim());
            if (userService.getUserById(id) == null) {
                System.out.println("User not found");
                printMenu();
                return null;
            }
            return id;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Try again.");
            printMenu();
            return null;
        }
    }

    private Double validAmount(Scanner scanner) {
        System.out.println();
        try {
            Double amount = Double.parseDouble(scanner.nextLine().trim());
            if (amount < 0) {
                System.out.println("Invalid amount entered. Try again.");
                printMenu();
                return null;
            }
            return amount;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Try again.");
            printMenu();
            return null;
        }
    }

    private Integer validAccountId(Scanner scanner) {
        accountService.getAllAccounts().stream().forEach(a -> {
            System.out.print(a.getId() + " ");
        });
        System.out.println();
        try {
            Integer id = Integer.parseInt(scanner.nextLine().trim());
            if (accountService.getAccountById(id) == null) {
                System.out.println("Account not found");
                printMenu();
                return null;
            }
            return id;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Try again.");
            printMenu();
            return null;
        }
    }
}
