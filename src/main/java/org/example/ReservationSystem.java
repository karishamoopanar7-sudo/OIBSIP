package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Random;

public class ReservationSystem {
    static Connection conn;
    static HashMap<String, String> trainMap = new HashMap<>();

    public static void main(String[] args) {
        // Sample train data for auto-fill
        trainMap.put("12345", "Rajdhani Express");
        trainMap.put("12346", "Shatabdi Express");
        trainMap.put("12951", "Mumbai Rajdhani");
        trainMap.put("12002", "Bhopal Shatabdi");
        trainMap.put("12138", "Punjab Mail");

        initDB();
        showLogin();
    }

    static void initDB() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:reservations.db");
            Statement st = conn.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS reservations (pnr TEXT PRIMARY KEY, name TEXT, trainNo TEXT, trainName TEXT, classType TEXT, date TEXT, source TEXT, dest TEXT)");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "DB Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static void showLogin() {
        JFrame f = new JFrame("Login - Online Reservation System");
        f.setSize(380, 200);
        f.setLayout(new GridLayout(3, 2, 10, 10));
        f.setLocationRelativeTo(null);

        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();
        f.add(new JLabel(" Username:"));
        f.add(user);
        f.add(new JLabel(" Password:"));
        f.add(pass);
        JButton login = new JButton("Login");
        f.add(new JLabel(""));
        f.add(login);

        login.addActionListener(e -> {
            if (user.getText().equals("admin") && new String(pass.getPassword()).equals("1234")) {
                f.dispose();
                showMain();
            } else {
                JOptionPane.showMessageDialog(f, "Access Denied: Invalid Username or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    static void showMain() {
        JFrame f = new JFrame("OIBSIP - Online Reservation System");
        f.setSize(680, 520);
        f.setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        // ===== Reservation Tab =====
        JPanel resPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        resPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JTextField nameF = new JTextField();
        JTextField trainNoF = new JTextField();
        JTextField trainNameF = new JTextField();
        trainNameF.setEditable(false);
        trainNameF.setBackground(Color.LIGHT_GRAY);
        JComboBox<String> classBox = new JComboBox<>(new String[]{"Sleeper", "3AC", "2AC", "1AC", "General"});
        JTextField dateF = new JTextField();
        dateF.setText("26-10-2026");
        JTextField sourceF = new JTextField();
        JTextField destF = new JTextField();

        // Auto-fill train name + validation for numeric only
        trainNoF.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String tn = trainNoF.getText().trim();
                if (trainMap.containsKey(tn)) {
                    trainNameF.setText(trainMap.get(tn));
                } else {
                    if (!tn.matches("\\d*")) {
                        trainNoF.setText(tn.replaceAll("[^\\d]", ""));
                    } else {
                        trainNameF.setText(tn.isEmpty() ? "" : "Express Train");
                    }
                }
            }
        });

        resPanel.add(new JLabel(" Passenger Name:")); resPanel.add(nameF);
        resPanel.add(new JLabel(" Train Number:")); resPanel.add(trainNoF);
        resPanel.add(new JLabel(" Train Name (auto):")); resPanel.add(trainNameF);
        resPanel.add(new JLabel(" Class Type:")); resPanel.add(classBox);
        resPanel.add(new JLabel(" Date (DD-MM-YYYY):")); resPanel.add(dateF);
        resPanel.add(new JLabel(" Source Station:")); resPanel.add(sourceF);
        resPanel.add(new JLabel(" Destination:")); resPanel.add(destF);
        JButton bookBtn = new JButton("Insert / Book Ticket");
        resPanel.add(new JLabel("")); resPanel.add(bookBtn);

        bookBtn.addActionListener(e -> {
            // Basic Validation
            if (nameF.getText().trim().isEmpty() || trainNoF.getText().trim().isEmpty() || sourceF.getText().trim().isEmpty() || destF.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(f, "All fields are required! No empty fields allowed.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!trainNoF.getText().matches("\\d+")) {
                JOptionPane.showMessageDialog(f, "Train Number must be numeric only!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!dateF.getText().matches("\\d{2}-\\d{2}-\\d{4}")) {
                JOptionPane.showMessageDialog(f, "Date must be in format DD-MM-YYYY", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String pnr = String.valueOf(100000 + new Random().nextInt(900000));
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO reservations VALUES (?,?,?,?,?,?,?,?)");
                ps.setString(1, pnr);
                ps.setString(2, nameF.getText().trim());
                ps.setString(3, trainNoF.getText().trim());
                ps.setString(4, trainNameF.getText().isEmpty() ? "Express" : trainNameF.getText());
                ps.setString(5, classBox.getSelectedItem().toString());
                ps.setString(6, dateF.getText().trim());
                ps.setString(7, sourceF.getText().trim());
                ps.setString(8, destF.getText().trim());
                ps.executeUpdate();

                JOptionPane.showMessageDialog(f, "TICKET BOOKED SUCCESSFULLY!\n\nPNR: " + pnr + "\nName: " + nameF.getText() + "\nTrain: " + trainNoF.getText() + " - " + trainNameF.getText() + "\nClass: " + classBox.getSelectedItem() + "\nRoute: " + sourceF.getText() + " -> " + destF.getText() + "\nDate: " + dateF.getText(), "Booking Confirmed - Note PNR", JOptionPane.INFORMATION_MESSAGE);

                nameF.setText(""); trainNoF.setText(""); trainNameF.setText(""); sourceF.setText(""); destF.setText("");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(f, "Error: " + ex.getMessage());
            }
        });

        // ===== Cancellation Tab =====
        JPanel canPanel = new JPanel(new BorderLayout(10,10));
        canPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JPanel top = new JPanel(new GridLayout(1,3,10,10));
        JTextField pnrF = new JTextField();
        JButton fetchBtn = new JButton("Fetch Details");
        top.add(new JLabel(" Enter PNR Number:")); top.add(pnrF); top.add(fetchBtn);
        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JButton confirmCancel = new JButton("Confirm Cancellation");

        canPanel.add(top, BorderLayout.NORTH);
        canPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        canPanel.add(confirmCancel, BorderLayout.SOUTH);

        fetchBtn.addActionListener(ev -> {
            if(pnrF.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(f, "Enter PNR first!");
                return;
            }
            try {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM reservations WHERE pnr=?");
                ps.setString(1, pnrF.getText().trim());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    detailsArea.setText("--- Booking Found ---\n\nPNR: " + rs.getString("pnr") + "\nPassenger: " + rs.getString("name") + "\nTrain No: " + rs.getString("trainNo") + "\nTrain Name: " + rs.getString("trainName") + "\nClass: " + rs.getString("classType") + "\nDate: " + rs.getString("date") + "\nFrom: " + rs.getString("source") + "\nTo: " + rs.getString("dest"));
                } else {
                    detailsArea.setText("No booking found for PNR: " + pnrF.getText().trim());
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        confirmCancel.addActionListener(ev -> {
            if (pnrF.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(f, "Enter PNR to cancel!");
                return;
            }
            int ans = JOptionPane.showConfirmDialog(f, "Are you sure you want to cancel?\nPNR: " + pnrF.getText().trim(), "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ans == JOptionPane.YES_OPTION) {
                try {
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM reservations WHERE pnr=?");
                    ps.setString(1, pnrF.getText().trim());
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(f, "Booking Cancelled Successfully! PNR: " + pnrF.getText().trim());
                        detailsArea.setText(""); pnrF.setText("");
                    } else {
                        JOptionPane.showMessageDialog(f, "PNR not found in database!");
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });

        tabs.addTab("Reservation Form", resPanel);
        tabs.addTab("Cancellation Form", canPanel);

        f.add(tabs);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}