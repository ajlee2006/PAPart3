package sample.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sample.Main;
import sample.Model.*;

import java.io.*;
import java.util.*;

public class SampleController {
    @FXML
    private Label labelLoggedInAs;
    @FXML
    private Button buttonLogout;
    @FXML
    private Label labelAmountInWallet;
    @FXML
    private Label labelPossibleSubjectCodes;
    @FXML
    private Label labelRentalCart;
    @FXML
    private TextArea textareaRentalCart;
    @FXML
    private TextField textfieldRemoveFromCart;
    @FXML
    private Button buttonRemoveFromCart;
    @FXML
    private Button buttonCheckout;
    @FXML
    private Label labelEnterCode;
    @FXML
    private TextField textfieldEnterCode;
    @FXML
    private Button buttonView;
    @FXML
    private TextArea textareaMain;
    @FXML
    private Label labelChoose;
    @FXML
    private TextField textfieldItemNo;
    @FXML
    private Button buttonChoose;
    @FXML
    private TextField textfieldPostalCode;
    @FXML
    private TextField textfieldLockerNo;
    @FXML
    private PasswordField textfieldPassword;
    @FXML
    private Button buttonRetrieve;
    @FXML
    private Button buttonAboutProgrammer;
    @FXML
    private VBox vboxLeftSide;
    @FXML
    private VBox vboxLockerRetrieval;

    Student student;
    Account account;
    ArrayList<Book> rentalCart;
    ArrayList<Book> getBooksBySubjectCode;
    ArrayList<SelfCollectStn> getStationByPostalCode;

    @FXML
    public void logout(ActionEvent event) {
        try {
            alertMessage("Logging out " + student.getName() + " (" + account.getLoginID() + ")");

            Main.security.logout();

            Stage thisStage = (Stage) buttonLogout.getScene().getWindow();
            thisStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/View/login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("AirBooks - Login");
            stage.show();
        } catch (Exception e) {
            alertError(e);
        }
    }

    public void alertError(Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error!");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    public void alertWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText("Error!");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void alertMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Alert");
        alert.setHeaderText("Notification");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void getLoginInfo(String username, String password) {
        Main.security.login(username, password);
        student = Main.database.getStudent(Security.getCurrStudentLogin());
        account = Main.security.getAccount(Security.getCurrStudentLogin());
        rentalCart = new ArrayList<Book>();
        labelLoggedInAs.setText("Logged in as " + student.getName() + " (" + account.getLoginID() + ")   ");
        updateWallet();
        updatePossibleSubjectCodes();
    }

    public void updateWallet() {
        labelAmountInWallet.setText(String.format("Amount in wallet: $%.2f",account.getWallet()));
    }

    public void clearAllTheLists() {
        updateRentalCart();
        updatePossibleSubjectCodes();
        updateWallet();
        getBooksBySubjectCode = new ArrayList<>();
        getStationByPostalCode = new ArrayList<>();
        textareaMain.setText("");
        textfieldEnterCode.setText("");
        textfieldItemNo.setText("");
        textfieldRemoveFromCart.setText("");
        textfieldPostalCode.setText("");
        textfieldLockerNo.setText("");
        textfieldPassword.setText("");
    }

    public void updatePossibleSubjectCodes() {
        ArrayList<String> psc = Main.database.getPossibleSubjCodes();
        String f = "";
        for (String i: psc) {
            if (f.length() != 0) f += ", ";
            f += i;
        }
        labelPossibleSubjectCodes.setText(f);
    }

    public void updateRentalCart() {
        String f = "";
        for (int i = 0; i < rentalCart.size(); i++) {
            f += (i + 1) + ") " + rentalCart.get(i) + "\n";
        }
        textareaRentalCart.setText(f);
        if (rentalCart.size() == 1) labelRentalCart.setText("Rental cart (1 book):");
        else labelRentalCart.setText("Rental cart (" + rentalCart.size() + " books):");
    }

    @FXML
    public void removeFromRentalCart(ActionEvent event) {
        try {
            if (rentalCart.size() > 0) {
                int selectBook = Integer.parseInt(textfieldRemoveFromCart.getText());
                if (selectBook < 1 || selectBook > rentalCart.size()) {
                    alertWarning("Enter a valid integer from 1 to " + rentalCart.size());
                } else {
                    Book book = rentalCart.remove(selectBook - 1);
                    alertMessage(book.getTitle() + " by " + book.getAuthor() + " removed from cart");
                    clearAllTheLists();
                }
            } else {
                alertWarning("Rental cart is empty!");
            }
        } catch (Exception e) {
            alertError(e);
        }
    }

    @FXML
    public void viewBooks(ActionEvent event) {
        getBooksBySubjectCode = Main.database.getBooklistBySubjCode(textfieldEnterCode.getText(), rentalCart);
        if (getBooksBySubjectCode.size() == 0) {
            alertWarning("No such book exist or all such books have been rented.");
        } else {
            String f = "";
            for (int i = 0; i < getBooksBySubjectCode.size(); i++) {
                f += (i + 1) + ") " + getBooksBySubjectCode.get(i) + "\n";
            } textareaMain.setText(f);
        }
    }

    @FXML
    public void addToRentalCart(ActionEvent event) {
        try {
            int bookSelected = Integer.parseInt(textfieldItemNo.getText());
            Book book = getBooksBySubjectCode.get(bookSelected - 1);
            double cost = 0;
            for (Book i : rentalCart) {
                cost += i.getDeposit();
            }
            if (cost + book.getDeposit() > account.getWallet()) {
                alertWarning("Cannot add more books. Amount in cart exceeds wallet.");
            } else {
                rentalCart.add(book);
                alertMessage(book.getTitle() + " by " + book.getAuthor() + " added to cart");
                clearAllTheLists();
            }
        } catch (Exception e) {
            alertWarning("Please enter an option that exists");
        }
    }

    @FXML
    public void checkout(ActionEvent event) {
        clearAllTheLists();
        convertLabelsToLocker();
    }

    public void convertLabelsToLocker() {
        vboxLeftSide.setDisable(true);
        vboxLockerRetrieval.setDisable(true);
        labelEnterCode.setText("Enter postal code to view nearby self collection stations");
        textfieldEnterCode.setPromptText("Postal code");
        labelChoose.setText("Choose self collection station:");
        buttonChoose.setText("Choose");
        buttonView.setOnAction(e -> viewStations(e));
        buttonChoose.setOnAction(e -> finalCheckout(e));
    }

    @FXML
    public void viewStations(ActionEvent event) {
        getStationByPostalCode = Main.database.getNearbySelfCollection(textfieldEnterCode.getText());
        if (getStationByPostalCode == null || getStationByPostalCode.size() == 0) {
            alertWarning("No nearby self collection stations. Please try another postal code.");
        } else {
            String f = "";
            for (int i = 0; i < getStationByPostalCode.size(); i++) {
                f += (i + 1) + ") " + getStationByPostalCode.get(i) + "\n";
            } textareaMain.setText(f);
        }
    }

    @FXML
    public void finalCheckout(ActionEvent event) {
        try {
            int stationSelected = Integer.parseInt(textfieldItemNo.getText());
            SelfCollectStn station = getStationByPostalCode.get(stationSelected - 1);
            int lockerNum = station.getEmptyLockerNum();
            Locker locker = station.getLocker(lockerNum);
            double deduction = 0;
            String message = "";
            for (Book i : rentalCart) {
                account.rentBook(i);
                message += "Book \"" + i.getTitle() + "\" successfully rented.\n";
                deduction += i.getDeposit();
                writeTransaction(System.getProperty("user.dir") + "/Transaction.csv", i.getISBN(), account.getStudentID(), i.getRentalPeriod(), i.getRentalDate(), station.getPostalCode(), lockerNum);
            }
            locker.placeItem(account.getStudentID(), new ArrayList<>(rentalCart));
            rentalCart.clear();
            account.deductFromWallet(deduction);
            message += String.format("$%.2f was deducted from ", deduction);
            message += account.getStudentID() + " wallet.\n";
            message += String.format("Amount left in wallet: $%.2f\n\n", account.getWallet());
            message += "Books placed in " + station + "\n";
            message += locker + "\n";
            System.out.println(station);
            System.out.println(locker);
            message += "Please collect your books soon! Thank you for using AirBooks!\n";
            Main.database.writeBook(System.getProperty("user.dir") + "/Books.csv");
            Main.security.writeAccount(System.getProperty("user.dir") + "/Secure.csv");
            clearAllTheLists();
            revertLabelsToBook();
            alertMessage(message);
        } catch (Exception e) {
            alertError(e);
        }
    }

    public void writeTransaction(String filename, String ISBN, String studentID, int days, String date, String postalCode, int lockerNum) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(filename, true));
            pw.println(ISBN + "," + studentID + "," + days + "," + date + "," + postalCode + "," + lockerNum);
            pw.close();
        } catch (Exception e) {
            alertError(e);
        }
    }

    public void revertLabelsToBook() {
        vboxLeftSide.setDisable(false);
        vboxLockerRetrieval.setDisable(false);
        labelEnterCode.setText("Enter subject code to view books");
        textfieldEnterCode.setPromptText("Code");
        labelChoose.setText("Choose book:");
        buttonChoose.setText("Add to rental cart");
        buttonView.setOnAction(e -> viewBooks(e));
        buttonChoose.setOnAction(e -> addToRentalCart(e));
    }

    @FXML
    public void retrieveLocker(ActionEvent event) {
        try {
            SelfCollectStn station = Main.database.getSelfCollection(textfieldPostalCode.getText());
            if (station == null) alertWarning("Invalid postal code!");
            else {
                int lockerNum = Integer.parseInt(textfieldLockerNo.getText());
                Locker locker = station.getLocker(lockerNum);
                if (locker.isEmpty()) alertWarning("The locker is empty.");
                else {
                    if (!locker.getStudentID().equals(student.getStudentID())) {
                        alertWarning("Access to locker denied. Invalid student accessing locker.");
                    } else {
                        alertMessage(locker.unlockLocker(textfieldPassword.getText()));
                    }
                }
            } clearAllTheLists();
        } catch (Exception e) {
            alertError(e);
        }
    }

    @FXML
    public void aboutProgrammer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/View/about.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("AirBooks - About the Programmer");
            stage.showAndWait();
        } catch (Exception e) {
            alertError(e);
        }
    }
}

/*
// Place any necessary imports here

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Place the necessary codes below this comment
        Database database = new Database("Student.csv", "Books.csv", "SelfCollectStn.csv", "DistrictAreas.csv");
        Security security = new Security("Secure.csv");
        int inputFirst = -1;
        logo();
        while (inputFirst != 2) {
            firstMenu();
            inputFirst = prompt(2);
            if (inputFirst == 1) {
                if (!loginReturnsIsAdmin(security)) {
                    ArrayList<Book> rentalCart = new ArrayList<Book>();
                    Student student = database.getStudent(Security.getCurrStudentLogin());
                    Account account = security.getAccount(Security.getCurrStudentLogin());
                    int inputStudent = -1;
                    while (inputStudent != 5) {
                        welcome(student.getName(), account.getWallet(), rentalCart.size());
                        studentMainMenu();
                        inputStudent = prompt(5);
                        switch (inputStudent) {
                            case 1:
                                viewSubjectCodes(database);
                                break;
                            case 2:
                                viewSubjectBooks(database, account, rentalCart);
                                break;
                            case 3:
                                viewCart(rentalCart, account, database, security);
                                break;
                            case 4:
                                lockerCollection(database, Security.getCurrStudentLogin());
                                break;
                            case 5:
                                System.out.println("Bye " + student.getName() + "\n");
                                security.logout();
                                break;
                        }
                    }
                } else {
                    int inputAdmin = -1;
                    while (inputAdmin != 2) {
                        System.out.println("Welcome admin!");
                        adminMainMenu();
                        inputAdmin = prompt(2);
                        switch (inputAdmin) {
                            case 1:
                                returnBook(database, security);
                                break;
                            case 2:
                                System.out.println("Bye admin.\n");
                                break;
                        }
                    }
                }
            }
        }
        System.out.println("Thank you for using AirBooks!");
    }

    // Please modularise your code (Code the features of your program based on the methods and their interaction)
    // Marks will be deducted if your code is not sufficiently modularised

    public static void returnBook(Database database, Security security) {
        Scanner s = new Scanner(System.in);
        System.out.print("Enter ISBN: ");
        String ISBN = s.nextLine();
        Book book = null;
        try {
            book = database.getBook(ISBN);
        } catch (Exception e) {
            System.out.println("No such book exists. admin wake up");
        }
        if (book == null) System.out.println("No such book exists. admin wake up\n");
        else {
            if (book.getIsRented()) {
                Account account = security.getAccount(book.getStudentID());
                account.returnBook(book);
                System.out.println("Book \"" + book.getTitle() + "\" successfully returned.");
                account.addToWallet(book.getDeposit());
                System.out.printf("$%.2f was added to ", book.getDeposit());
                System.out.println(account.getStudentID() + " wallet.\n");
                database.writeBook("Books.csv");
                security.writeAccount("Secure.csv");
            } else System.out.println("Cannot return a book which is not rented.\n");
        }
    }

    public static int prompt(int max) {
        Scanner s = new Scanner(System.in);
        int input = -1;
        while (input < 1 || input > max) {
            System.out.print("Enter a number above: ");
            String sinput = s.nextLine();
            try {
                input = Integer.parseInt(sinput);
            } catch (Exception e) {
            }
            System.out.println();
        }
        return input;
    }

    public static boolean loginReturnsIsAdmin(Security security) {
        Scanner s = new Scanner(System.in);
        String loginID, password;
        boolean loggedin = false;
        boolean admin = false;
        while (!loggedin) {
            System.out.print("Login ID: ");
            loginID = s.nextLine();
            System.out.print("Password: ");
            password = s.nextLine();
            loggedin = security.login(loginID, password);
            if (loginID.equals("admin") && password.equals("#AB#admin123")) {
                loggedin = true;
                admin = true;
            }
        }
        return admin;
    }

    public static void welcome(String name, double wallet, int rentalCart) {
        System.out.println("\nWelcome " + name + "!");
        System.out.printf("You have $%.2f in your wallet.\n", wallet);
        System.out.println("You have " + rentalCart + " items in your rental cart.");
    }

    public static void viewSubjectCodes(Database database) {
        System.out.println("Viewing all possible subject codes:");
        for (String i : database.getPossibleSubjCodes()) System.out.println(i);
    }

    public static void viewSubjectBooks(Database database, Account account, ArrayList<Book> rentalCart) {
        Scanner s = new Scanner(System.in);
        System.out.print("Enter Subject Code: ");
        String subjCode = s.nextLine();
        ArrayList<Book> books = database.getBooklistBySubjCode(subjCode, rentalCart);
        if (books.size() == 0) {
            System.out.println("No such book exist or all such books have been rented.");
        } else {
            System.out.println("Viewing all " + subjCode + " books:");
            printBooks(books);
            line();
            int bookSelected = selectBook(books.size());
            Book book = books.get(bookSelected - 1);
            if (booksCost(rentalCart) + book.getDeposit() > account.getWallet()) {
                System.out.println("Cannot add more books. Amount in cart exceeds wallet.");
            } else {
                rentalCart.add(book);
                System.out.println(book.getTitle() + " by " + book.getAuthor() + " added to cart");
            }
        }
    }

    public static void printBooks(ArrayList<Book> books) {
        for (int i = 0; i < books.size(); i++) {
            System.out.println((i + 1) + ") " + books.get(i));
        }
    }

    public static void line() {
        System.out.println("-------------------------------------");
    }

    public static int selectBook(int max) {
        Scanner s = new Scanner(System.in);
        int bookSelected = -1;
        while (bookSelected < 1 || bookSelected > max) {
            System.out.print("Select Book: ");
            String sinput = s.nextLine();
            try {
                bookSelected = Integer.parseInt(sinput);
            } catch (Exception e) {
            }
            if (bookSelected < 1 || bookSelected > max) System.out.println("Enter a valid integer from 1 to " + max);
        }
        return bookSelected;
    }

    public static double booksCost(ArrayList<Book> books) {
        double cost = 0;
        for (Book i : books) {
            cost += i.getDeposit();
        }
        return cost;
    }

    public static void viewCart(ArrayList<Book> rentalCart, Account account, Database database, Security security) {
        Scanner s = new Scanner(System.in);
        System.out.println("Your rental cart items:");
        printBooks(rentalCart);
        line();
        System.out.printf("Total deposit cost: $%.2f\n", booksCost(rentalCart));
        System.out.printf("Amount in wallet: $%.2f\n", account.getWallet());
        line();
        System.out.println();
        String prompt = "";
        while (!prompt.equalsIgnoreCase("y") && !prompt.equalsIgnoreCase("n")) {
            System.out.print("Do you wish to remove any items? (Y/N): ");
            prompt = s.nextLine();
        }
        if (prompt.equalsIgnoreCase("y")) {
            if (rentalCart.size() > 0) {
                int selectBook = selectBook(rentalCart.size());
                Book book = rentalCart.get(selectBook - 1);
                System.out.println(book.getTitle() + " by " + book.getAuthor() + " removed from cart");
                rentalCart.remove(selectBook - 1);
            } else {
                System.out.println("Rental cart is empty!");
            }
        } else {
            prompt = "";
            while (!prompt.equalsIgnoreCase("y") && !prompt.equalsIgnoreCase("n")) {
                System.out.print("Do you wish to checkout your cart? (Y/N): ");
                prompt = s.nextLine();
            }
            if (prompt.equalsIgnoreCase("y")) {
                if (rentalCart.size() > 0) {
                    checkout(rentalCart, database, account, security);
                } else {
                    System.out.println("Rental cart is empty!");
                }
            }
        }
    }

    public static void checkout(ArrayList<Book> rentalCart, Database database, Account account, Security security) {
        Scanner s = new Scanner(System.in);
        String postalCode;
        ArrayList<SelfCollectStn> stations = null;
        while (stations == null || stations.size() == 0) {
            System.out.println("Enter your postal code: ");
            postalCode = s.nextLine();
            stations = database.getNearbySelfCollection(postalCode);
            if (stations == null || stations.size() == 0)
                System.out.println("No nearby self collection stations. Please try another postal code.");
        }
        System.out.println("Choose a self-collection station from the list below:");
        printStations(stations);
        SelfCollectStn station = stations.get(prompt(stations.size()) - 1);
        int lockerNum = station.getEmptyLockerNum();
        Locker locker = station.getLocker(lockerNum);
        double deduction = 0;
        for (Book i : rentalCart) {
            account.rentBook(i);
            System.out.println("Book \"" + i.getTitle() + "\" successfully rented.");
            deduction += i.getDeposit();
            writeTransaction("Transaction.csv", i.getISBN(), account.getStudentID(), i.getRentalPeriod(), i.getRentalDate(), station.getPostalCode(), lockerNum);
        }
        locker.placeItem(account.getStudentID(), rentalCart);
        rentalCart = new ArrayList<Book>();
        account.deductFromWallet(deduction);
        System.out.printf("$%.2f was deducted from ", deduction);
        System.out.println(account.getStudentID() + " wallet.");
        System.out.printf("Amount left in wallet: $%.2f\n\n", account.getWallet());
        System.out.println("Books placed in " + station);
        System.out.println(locker);
        System.out.println("Please collect your books soon! Thank you for using AirBooks!");
        database.writeBook("Books.csv");
        security.writeAccount("Secure.csv");
    }

    public static void printStations(ArrayList<SelfCollectStn> stations) {
        for (int i = 0; i < stations.size(); i++) {
            System.out.println((i + 1) + ") " + stations.get(i));
        }
    }

    public static void writeTransaction(String filename, String ISBN, String studentID, int days, String date, String postalCode, int lockerNum) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(filename, true));
            pw.println(ISBN + "," + studentID + "," + days + "," + date + "," + postalCode + "," + lockerNum);
            pw.close();
        } catch (Exception e) {
        }
    }

    public static void lockerCollection(Database database, String studentID) {
        Scanner s = new Scanner(System.in);
        SelfCollectStn station = null;
        while (station == null) {
            System.out.print("Enter Self Collection Station Postal: ");
            String postal = s.nextLine();
            station = database.getSelfCollection(postal);
            if (station == null) System.out.println("Invalid postal code!");
        }
        System.out.println(station);
        System.out.print("Enter Locker Number: ");
        String slockerNum = s.nextLine();
        int lockerNum = 0;
        try {
            lockerNum = Integer.parseInt(slockerNum);
        } catch (Exception e) {
            System.out.println("pls stop trying to break my code");
        }
        Locker locker = new Locker(0);
        try {
            locker = station.getLocker(lockerNum-1);
        } catch (Exception e) {
            System.out.println("pls stop trying to break my code");
        }
        if (locker.isEmpty()) System.out.println("The locker is empty.");
        else {
            if (!locker.getStudentID().equals(studentID)) {
                System.out.println("Access to locker denied. Invalid student accessing locker.");
            } else {
                System.out.print("Enter Password: ");
                String password = s.nextLine();
                System.out.println(locker.unlockLocker(password));
            }
        }
    }

    //DO NOT make edits to the methods from this point on.
    public static void logo() {
        System.out.println("    --   --  --  --  --  --  --  --  --  --");
        System.out.println("  --   -- ||||    | |---|   |---|  |-----| |-----| |  /   -----");
        System.out.println("--   --  |    |   | |    |  |   |  |     | |     | | /    |");
        System.out.println("--   --  |----|   | |---|   |---|| |     | |     | |/__    -----");
        System.out.println(" --    -|      |  | |    |  |    | |     | |     | |   |        |");
        System.out.println("   --  |        | | |     | |____| |_____| |_____| |    |_ _____|");
        System.out.println("     --  --  --  --  --  --  --  --  --  --");
        System.out.println("Welcome to AirBooks!");
    }

    public static void firstMenu() {
        System.out.println("What would you like to do?");
        System.out.println("1. Login to AirBooks (Students / Admin)");
        System.out.println("2. Exit System");
    }

    public static void adminMainMenu() {
        System.out.println();
        System.out.println("Select an option:");
        System.out.println("1. Return a book by ISBN.");
        System.out.println("2. Logout.");
    }

    public static void studentMainMenu() {
        System.out.println();
        System.out.println("Select an option:");
        System.out.println("1. See all possible subject codes.");
        System.out.println("2. Enter a subject code to view books + Add books to cart.");
        System.out.println("3. View rental cart.");
        System.out.println("4. Locker Collection.");
        System.out.println("5. Logout.");
    }
}

 */