package com.bankapp.service;

import com.bankapp.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AccountService {

    private UserService userService;
    private final Double defaultAccountAmount;
    private final AtomicInteger idGenerator = new AtomicInteger(1);
    private final Map<Integer, Account> accounts = new HashMap<>();

    public AccountService(Double defaultAccountAmount) {
        this.defaultAccountAmount = defaultAccountAmount;
    }

    public Account createAccount(Integer userId) {
        Account account = new Account(idGenerator.getAndIncrement(), userId, defaultAccountAmount);
        accounts.put(account.getId(), account);
        return account;
    }

    public Set<Integer> getAllAccountIds() {
        return accounts.keySet();
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public Account getAccountById(int id) {
        return accounts.get(id);
    }

    public void deposit(Integer id, Double amount) {
        Double tempAmount = accounts.get(id).getAccountAmount() + amount;
        accounts.get(id).setAccountAmount(tempAmount);
    }

    public Boolean withdraw(Integer id, Double amount) {
        Double tempAmount = accounts.get(id).getAccountAmount();
        if (tempAmount >= amount) {
            accounts.get(id).setAccountAmount(tempAmount - amount);
            return true;
        } else {
            System.out.println("Error executing command ACCOUNT_WITHDRAW: error = no such money to withdraw" +
                    "/ from account: id=" + id + ", moneymount=0" + ", attempteWithdraw=" + amount);
            return false;
        }
    }

    public Boolean closeAccount(Integer id) {
        Account account = accounts.get(id);
        Integer userId = account.getUserId();
        int count = userService.getUserById(userId).getAccountList().size();
        if (count > 1) {
            Double amount = accounts.get(id).getAccountAmount();
            //Get first account amount
            Double firstAccountAmount = userService.getUserById(userId).getAccountList().stream().
                    filter(acc -> !acc.getId().equals(id)).findFirst().get().getAccountAmount();
            //Change first account amount
            userService.getUserById(userId).getAccountList().stream().filter(acc -> !acc.getId().equals(id)).findFirst().ifPresent(acc -> {
                acc.setAccountAmount(firstAccountAmount + amount);
            });
            //Delete account from Map accounts and List accountList in User
            accounts.remove(id);
            userService.getUserById(userId).getAccountList().removeIf(acc -> acc.getId().equals(id));
            System.out.println("Account with ID " + id + " has been closed.");
            return true;
        } else {
            System.out.println("User with id " + userId + " has only one account ! Operation declined!");
            return false;
        }
    }

    public void transferMoney(Integer fromId, Integer toId, Double amount) {
        Double amountFromId = accounts.get(fromId).getAccountAmount();
        Double amountToId = accounts.get(toId).getAccountAmount();
        if (amountFromId >= amount && fromId != toId) {
            accounts.get(fromId).setAccountAmount(amountFromId - amount);
            accounts.get(toId).setAccountAmount(amountToId + amount);
            System.out.println("Amount " + amount + " transferred from account ID " + fromId + " to account ID " + toId + ".");
        } else {
            System.out.println("Don't enough money in account ID " + fromId + " or User has only one account. Operation declined!");
        }
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
