import java.io.*;
import java.util.*;

class BankAccount implements Serializable {
    private String accountNumber;
    private String accountHolderName;
    private double balance;
    private String password;

    public BankAccount(String accountNumber, String accountHolderName, double initialBalance, String password) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialBalance;
        this.password = password;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public double getBalance() {
        return balance;
    }

    public boolean verifyPassword(String inputPassword) {
        return password.equals(inputPassword);
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("₹" + amount + " deposited successfully.");
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("₹" + amount + " withdrawn successfully.");
        } else {
            System.out.println("Insufficient balance or invalid amount.");
        }
    }

    @Override
    public String toString() {
        return "Account Number: " + accountNumber + ", Account Holder: " + accountHolderName + ", Balance: ₹" + balance;
    }
}

public class BankApplication {
    private static final String DATA_FILE = "accounts.dat";
    private static HashMap<String, BankAccount> accounts = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadAccounts();

        while (true) {
            System.out.println("\n--- Bank Application Menu ---");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Check Balance");
            System.out.println("5. Transfer Funds");
            System.out.println("6. Search by Account Holder Name");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = getValidInteger();

            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    deposit();
                    break;
                case 3:
                    withdraw();
                    break;
                case 4:
                    checkBalance();
                    break;
                case 5:
                    transferFunds();
                    break;
                case 6:
                    searchByName();
                    break;
                case 7:
                    saveAccounts();
                    System.out.println("Thank you for using the Bank Application!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void createAccount() {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.next();
        System.out.print("Enter account holder's name: ");
        String accountHolderName = scanner.next();
        System.out.print("Enter initial balance: ");
        double initialBalance = getValidDouble();
        System.out.print("Set a password for your account: ");
        String password = scanner.next();

        if (accounts.containsKey(accountNumber)) {
            System.out.println("Account with this number already exists.");
        } else {
            BankAccount account = new BankAccount(accountNumber, accountHolderName, initialBalance, password);
            accounts.put(accountNumber, account);
            System.out.println("Account created successfully!");
        }
    }

    private static void deposit() {
        BankAccount account = authenticate();
        if (account != null) {
            System.out.print("Enter deposit amount: ");
            double amount = getValidDouble();
            account.deposit(amount);
        }
    }

    private static void withdraw() {
        BankAccount account = authenticate();
        if (account != null) {
            System.out.print("Enter withdrawal amount: ");
            double amount = getValidDouble();
            account.withdraw(amount);
        }
    }

    private static void checkBalance() {
        BankAccount account = authenticate();
        if (account != null) {
            System.out
                    .println("The balance for account " + account.getAccountNumber() + " is ₹" + account.getBalance());
        }
    }

    private static void transferFunds() {
        System.out.print("Enter your account number: ");
        BankAccount sender = authenticate();
        if (sender != null) {
            System.out.print("Enter recipient account number: ");
            String recipientAccountNumber = scanner.next();
            if (!accounts.containsKey(recipientAccountNumber)) {
                System.out.println("Recipient account not found.");
                return;
            }
            BankAccount recipient = accounts.get(recipientAccountNumber);

            System.out.print("Enter amount to transfer: ");
            double amount = getValidDouble();

            if (amount > 0 && sender.getBalance() >= amount) {
                sender.withdraw(amount);
                recipient.deposit(amount);
                System.out.println("₹" + amount + " transferred successfully to " + recipient.getAccountHolderName());
            } else {
                System.out.println("Insufficient balance or invalid amount.");
            }
        }
    }

    private static void searchByName() {
        System.out.print("Enter account holder's name: ");
        String name = scanner.next();
        boolean found = false;

        for (BankAccount account : accounts.values()) {
            if (account.getAccountHolderName().equalsIgnoreCase(name)) {
                System.out.println(account);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No account found for the given name.");
        }
    }

    private static BankAccount authenticate() {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.next();

        if (accounts.containsKey(accountNumber)) {
            System.out.print("Enter password: ");
            String password = scanner.next();
            BankAccount account = accounts.get(accountNumber);

            if (account.verifyPassword(password)) {
                return account;
            } else {
                System.out.println("Incorrect password.");
            }
        } else {
            System.out.println("Account not found.");
        }
        return null;
    }

    private static void loadAccounts() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            accounts = (HashMap<String, BankAccount>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No saved accounts found. Starting fresh.");
        } catch (Exception e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }
    }

    private static void saveAccounts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(accounts);
        } catch (Exception e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    private static int getValidInteger() {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static double getValidDouble() {
        while (!scanner.hasNextDouble()) {
            System.out.println("Invalid input. Please enter a valid amount.");
            scanner.next();
        }
        return scanner.nextDouble();
    }
}
