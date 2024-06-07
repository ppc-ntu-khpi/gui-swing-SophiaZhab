package com.mybank.gui;


import com.mybank.domain.Bank;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import com.mybank.domain.SavingsAccount;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Графічний інтерфейс для відображення інформації про клієнтів банку.
 *
 * @автор Олександр 'Taurus' Бабич
 */
public class SWINGdemo {

    private final JEditorPane log;
    private final JButton show;
    private final JButton report;
    private final JComboBox clients;

    public SWINGdemo() {
        log = new JEditorPane("text/html", "");
        log.setPreferredSize(new Dimension(250, 550));
        show = new JButton("Show");
        report = new JButton("Report");
        clients = new JComboBox();
        for (int i = 0; i < Bank.getNumberOfCustomers(); i++) {
            clients.addItem(Bank.getCustomer(i).getLastName() + ", " + Bank.getCustomer(i).getFirstName());
        }
    }

    private void launchFrame() {
        JFrame frame = new JFrame("MyBank clients");
        frame.setLayout(new BorderLayout());
        JPanel cpane = new JPanel();
        cpane.setLayout(new GridLayout(1, 2));

        cpane.add(clients);
        cpane.add(show);
        cpane.add(report);
        frame.add(cpane, BorderLayout.NORTH);
        frame.add(log, BorderLayout.CENTER);

        show.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Customer current = Bank.getCustomer(clients.getSelectedIndex());
                StringBuilder custInfo = new StringBuilder("<br>&nbsp;<b><span style=\"font-size:2em;\">")
                        .append(current.getLastName()).append(", ")
                        .append(current.getFirstName()).append("</span><br><hr>");

                for (int i = 0; i < current.getNumberOfAccounts(); i++) {
                    String accType = current.getAccount(i) instanceof CheckingAccount ? "Checking" : "Savings";
                    custInfo.append("&nbsp;<b>Acc Type: </b>").append(accType)
                            .append("<br>&nbsp;<b>Balance: <span style=\"color:red;\">$")
                            .append(current.getAccount(i).getBalance()).append("</span></b><br>");
                }
                log.setText(custInfo.toString());
            }
        });

        report.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder reportInfo = new StringBuilder("<html><body><h1>Customers report</h1>");
                for (int i = 0; i < Bank.getNumberOfCustomers(); i++) {
                    Customer current = Bank.getCustomer(i);
                    reportInfo.append("<br>&nbsp;<b><span style=\"font-size:1.5em;\">")
                            .append(current.getLastName()).append(", ")
                            .append(current.getFirstName()).append("</span><br><hr>");
                    for (int j = 0; j < current.getNumberOfAccounts(); j++) {
                        String accType = current.getAccount(j) instanceof CheckingAccount ? "Checking" : "Savings";
                        reportInfo.append("&nbsp;<b>Acc Type: </b>").append(accType)
                                .append("<br>&nbsp;<b>Balance: <span style=\"color:red;\">$")
                                .append(current.getAccount(j).getBalance()).append("</span></b><br>");
                    }
                }
                reportInfo.append("</body></html>");
                log.setText(reportInfo.toString());
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private static void loadCustomerData(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 3) {
                    String firstName = parts[0];
                    String lastName = parts[1];
                    int numAccounts = Integer.parseInt(parts[2]);
                    Bank.addCustomer(firstName, lastName);
                    for (int i = 0; i < numAccounts; i++) {
                        line = reader.readLine();
                        parts = line.split("\t");
                        if (parts[0].equals("S")) {
                            double balance = Double.parseDouble(parts[1]);
                            double interestRate = parts.length >= 3 ? Double.parseDouble(parts[1]) : 0.0;
                            Bank.getCustomer(Bank.getNumberOfCustomers() - 1).addAccount(new SavingsAccount(balance, interestRate));
                        } else if (parts[0].equals("C")) {
                            double balance = Double.parseDouble(parts[1]);
                            double overdraftLimit = parts.length >= 3 ? Double.parseDouble(parts[2]) : 0.0; // Опціональний параметр для CheckingAccount
                            Bank.getCustomer(Bank.getNumberOfCustomers() - 1).addAccount(new CheckingAccount(balance, overdraftLimit));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Завантаження даних клієнтів з файлу
        loadCustomerData("data\\test.dat");

        SWINGdemo demo = new SWINGdemo();
        demo.launchFrame();
    }
}