import java.awt.*;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.border.EmptyBorder;
import java.util.Timer;
import java.util.TimerTask;

public class MainMenu extends JFrame {
    private double calculateEstimatedPrice(String initialPoint, String finalDestination) {
        return Math.random() * 100 + 20;
    }

    private boolean checkTaxiAvailability(String initialPoint, String finalDestination) {
        return Math.random() > 0.5;
    }

    private double walletBalance;
    private JLabel clockLabel;
    private JLabel notificationLabel;
    private JLabel walletLabel;
    private ArrayList<Taxi> taxis;
    private DefaultListModel<String> notificationsModel;
    private JList<String> notificationList;
    private JComboBox<String> taxiComboBox;

    public MainMenu(double initialBalance) {
        this.walletBalance = initialBalance;
        taxis = new ArrayList<>();
        notificationsModel = new DefaultListModel<>();
        initializeTaxis();
        addSampleNewsNotifications();

        setTitle("Taxi Service Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        fileMenu.add(saveMenuItem);
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem themeMenuItem = new JMenuItem("Switch Theme");
        editMenu.add(themeMenuItem);
        menuBar.add(editMenu);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutMenuItem = new JMenuItem("About");
        helpMenu.add(aboutMenuItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        saveMenuItem.addActionListener(e -> saveTaxiData());
        exitMenuItem.addActionListener(e -> System.exit(0));
        themeMenuItem.addActionListener(e -> switchTheme());
        aboutMenuItem.addActionListener(e -> JOptionPane.showMessageDialog(this, "Taxi Service Dashboard v1.0", "About", JOptionPane.INFORMATION_MESSAGE));

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(new Color(255, 215, 0));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        headerPanel.setBackground(new Color(0, 122, 51));

        JLabel logoLabel = new JLabel("Taxi Service Dashboard", JLabel.LEFT);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 28));
        logoLabel.setForeground(Color.WHITE);
        headerPanel.add(logoLabel, BorderLayout.WEST);

        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Arial", Font.BOLD, 24));
        clockLabel.setForeground(Color.WHITE);
        headerPanel.add(clockLabel, BorderLayout.EAST);
        startClock();

        JTextField searchField = new JTextField("Search...");
        searchField.setPreferredSize(new Dimension(300, 30));
        searchField.setBackground(new Color(200, 230, 201));
        searchField.setForeground(Color.BLACK);
        searchField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        JButton searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(100, 30));
        searchButton.setBackground(new Color(255, 193, 7));
        searchButton.setForeground(Color.BLACK);
        searchButton.setFocusPainted(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        headerPanel.add(searchPanel, BorderLayout.CENTER);

        searchButton.addActionListener(e -> {
            String searchQuery = searchField.getText().trim();
            if (!searchQuery.isEmpty()) {
                StringBuilder result = new StringBuilder();
                for (Taxi taxi : taxis) {
                    if (taxi.getTaxiNumber().toLowerCase().contains(searchQuery.toLowerCase()) ||
                        searchQuery.equalsIgnoreCase(taxi.getStartTime()) ||
                        searchQuery.equalsIgnoreCase(taxi.getEndTime())) {
                        result.append("Taxi Number: ").append(taxi.getTaxiNumber())
                                .append(", Available Start Time: ").append(taxi.getStartTime())
                                .append(", Available End Time: ").append(taxi.getEndTime()).append("\n");
                    }
                }
                if (result.length() == 0) {
                    JOptionPane.showMessageDialog(this, "No taxis found for the given search.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, result.toString(), "Search Result", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a search term.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        mainContainer.add(headerPanel, BorderLayout.NORTH);

        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(0, 56, 168));
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));

        Color buttonColor = new Color(255, 152, 0);

        JButton bookTaxiButton = createSidebarButton("Book a Taxi", buttonColor);
        JButton nearbyTaxiButton = createSidebarButton("See Nearby Taxis", buttonColor);
        JButton taxiRoutesButton = createSidebarButton("View Taxi Routes", buttonColor);
        JButton taxiScheduleButton = createSidebarButton("Taxi Schedule", buttonColor);
        JButton themeSwitcher = createSidebarButton("Switch Theme", buttonColor);

        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebarPanel.add(bookTaxiButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(nearbyTaxiButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(taxiRoutesButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(taxiScheduleButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(themeSwitcher);

        mainContainer.add(sidebarPanel, BorderLayout.WEST);

        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setOpaque(false);

        JPanel walletPanel = new JPanel(new GridBagLayout());
        walletPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        walletPanel.setOpaque(false);

        walletLabel = new JLabel(String.format("Wallet Balance: R%.2f", walletBalance), SwingConstants.CENTER);
        walletLabel.setFont(new Font("Arial", Font.BOLD, 28));
        walletLabel.setForeground(new Color(0, 150, 136));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        inputPanel.setOpaque(false);

        JLabel initialPointLabel = new JLabel("Initial Point:");
        initialPointLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        initialPointLabel.setForeground(new Color(0, 150, 136));
        JTextField initialPointField = new JTextField();
        initialPointField.setBackground(new Color(200, 230, 201));
        initialPointField.setForeground(Color.BLACK);
        initialPointField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));

        JLabel finalDestinationLabel = new JLabel("Final Destination:");
        finalDestinationLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        finalDestinationLabel.setForeground(new Color(0, 150, 136));
        JTextField finalDestinationField = new JTextField();
        finalDestinationField.setBackground(new Color(200, 230, 201));
        finalDestinationField.setForeground(Color.BLACK);
        finalDestinationField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));

        JLabel taxiSelectionLabel = new JLabel("Select Taxi:");
        taxiSelectionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        taxiSelectionLabel.setForeground(new Color(0, 150, 136));
        taxiComboBox = new JComboBox<>(new String[]{"Standard Taxi", "Luxury Taxi", "Minibus"});
        taxiComboBox.setBackground(new Color(200, 230, 201));
        taxiComboBox.setForeground(Color.BLACK);

        inputPanel.add(initialPointLabel);
        inputPanel.add(initialPointField);
        inputPanel.add(finalDestinationLabel);
        inputPanel.add(finalDestinationField);
        inputPanel.add(taxiSelectionLabel);
        inputPanel.add(taxiComboBox);

        gbc.gridx = 0;
        gbc.gridy = 0;
        walletPanel.add(walletLabel, gbc);

        gbc.gridy = 1;
        walletPanel.add(inputPanel, gbc);

        gbc.gridy = 2;
        JPanel walletButtonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        walletButtonPanel.setOpaque(false);
        RoundedButton addMoneyButton = createActionButton("Add Money", buttonColor);
        RoundedButton withdrawMoneyButton = createActionButton("Withdraw Money", buttonColor);
        JButton showMapButton = createActionButton("Show Map", buttonColor);

        walletButtonPanel.add(addMoneyButton);
        walletButtonPanel.add(withdrawMoneyButton);
        walletButtonPanel.add(showMapButton);

        walletPanel.add(walletButtonPanel, gbc);

        gbc.gridy = 3;
        JButton calculateButton = createActionButton("Calculate Fare & Availability", buttonColor);
        walletPanel.add(calculateButton, gbc);

        gbc.gridy = 4;
        JLabel estimatedPriceLabel = new JLabel("Estimated Price: N/A");
        estimatedPriceLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        estimatedPriceLabel.setForeground(new Color(0, 150, 136));
        walletPanel.add(estimatedPriceLabel, gbc);

        gbc.gridy = 5;
        JLabel taxiAvailabilityLabel = new JLabel("Taxi Availability: N/A");
        taxiAvailabilityLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        taxiAvailabilityLabel.setForeground(new Color(0, 150, 136));
        walletPanel.add(taxiAvailabilityLabel, gbc);

        contentPanel.add(walletPanel, "walletPanel");
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        JPanel notificationPanel = new JPanel(new BorderLayout());
        notificationPanel.setPreferredSize(new Dimension(300, 300));
        notificationPanel.setBackground(new Color(0, 150, 136));

        notificationLabel = new JLabel("Notifications", JLabel.LEFT);
        notificationLabel.setFont(new Font("Arial", Font.BOLD, 28));
        notificationLabel.setForeground(Color.WHITE);
        notificationPanel.add(notificationLabel, BorderLayout.NORTH);

        notificationList = new JList<>(notificationsModel);
        notificationList.setBackground(new Color(200, 230, 201));
        notificationList.setForeground(Color.BLACK);
        notificationList.setFont(new Font("Arial", Font.PLAIN, 16));

        JScrollPane notificationScrollPane = new JScrollPane(notificationList);
        notificationPanel.add(notificationScrollPane, BorderLayout.CENTER);

        mainContainer.add(notificationPanel, BorderLayout.SOUTH);

        setContentPane(mainContainer);
        setVisible(true);

        bookTaxiButton.addActionListener(e -> {
            String initialPoint = JOptionPane.showInputDialog(this, "Enter initial point:");
            String finalDestination = JOptionPane.showInputDialog(this, "Enter final destination:");
            if (initialPoint != null && finalDestination != null && !initialPoint.isEmpty() && !finalDestination.isEmpty()) {
                double estimatedPrice = calculateEstimatedPrice(initialPoint, finalDestination);
                boolean isTaxiAvailable = checkTaxiAvailability(initialPoint, finalDestination);
                String message = "Estimated Price: R" + String.format("%.2f", estimatedPrice) + "\nTaxi Availability: " + (isTaxiAvailable ? "Available" : "Not Available");
                JOptionPane.showMessageDialog(this, message, "Booking Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter both initial and final destinations.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        nearbyTaxiButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "See Nearby Taxis..."));
        taxiRoutesButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "View Taxi Routes..."));
        taxiScheduleButton.addActionListener(e -> showTaxiSchedule());
        themeSwitcher.addActionListener(e -> switchTheme());
        addMoneyButton.addActionListener(e -> adjustWalletBalance(50.0));
        withdrawMoneyButton.addActionListener(e -> adjustWalletBalance(-50.0));
        calculateButton.addActionListener(e -> {
            String initialPoint = initialPointField.getText();
            String finalDestination = finalDestinationField.getText();
            String taxiType = (String) taxiComboBox.getSelectedItem();
            if (!initialPoint.isEmpty() && !finalDestination.isEmpty()) {
                double estimatedPrice = calculateEstimatedPrice(initialPoint, finalDestination);
                boolean isTaxiAvailable = checkTaxiAvailability(initialPoint, finalDestination);
                estimatedPriceLabel.setText("Estimated Price: R" + String.format("%.2f", estimatedPrice) + " (" + taxiType + ")");
                taxiAvailabilityLabel.setText("Taxi Availability: " + (isTaxiAvailable ? "Available" : "Not Available"));
            } else {
                JOptionPane.showMessageDialog(this, "Please enter both initial and final destinations.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        showMapButton.addActionListener(e -> {
            String initialPoint = initialPointField.getText();
            String finalDestination = finalDestinationField.getText();
            if (!initialPoint.isEmpty() && !finalDestination.isEmpty()) {
                try {
                    String googleMapsUrl = "https://www.google.com/maps/dir/?api=1&origin=" + initialPoint + "&destination=" + finalDestination;
                    Desktop.getDesktop().browse(new java.net.URI(googleMapsUrl));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error while opening Google Maps: " + ex.getMessage(), "Map Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter both initial and final destinations.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void startClock() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                clockLabel.setText(sdf.format(new Date()));
            }
        }, 0, 1000);
    }

    private JButton createSidebarButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 50));
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private RoundedButton createActionButton(String text, Color color) {
        RoundedButton button = new RoundedButton(text, color, color.brighter());
        button.setPreferredSize(new Dimension(160, 50));
        return button;
    }

    private void showTaxiSchedule() {
        if (taxis.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No taxi schedule available.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] columnNames = {"Taxi Number", "Available Start Time", "Available End Time"};
        String[][] tableData = new String[taxis.size()][3];
        for (int i = 0; i < taxis.size(); i++) {
            Taxi taxi = taxis.get(i);
            tableData[i][0] = taxi.getTaxiNumber();
            tableData[i][1] = taxi.getStartTime();
            tableData[i][2] = taxi.getEndTime();
        }

        JTable table = new JTable(tableData, columnNames);
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(255, 193, 7));
        header.setForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Taxi Schedule", JOptionPane.INFORMATION_MESSAGE);
    }

    private void switchTheme() {
        Color panelBackground;
        Color labelForeground;
        Color buttonBackground;
        Color buttonForeground;

        if (getContentPane().getBackground().equals(new Color(255, 255, 255))) {
            panelBackground = new Color(50, 50, 50);
            labelForeground = Color.ORANGE;
            buttonBackground = new Color(100, 149, 237);
            buttonForeground = Color.WHITE;
        } else {
            panelBackground = new Color(255, 255, 255);
            labelForeground = Color.BLACK;
            buttonBackground = new Color(255, 140, 0);
            buttonForeground = Color.BLACK;
        }

        getContentPane().setBackground(panelBackground);
        for (Component component : getContentPane().getComponents()) {
            updateComponentTheme(component, panelBackground, labelForeground, buttonBackground, buttonForeground);
        }
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void updateComponentTheme(Component component, Color panelBackground, Color labelForeground, Color buttonBackground, Color buttonForeground) {
        if (component instanceof JPanel) {
            component.setBackground(panelBackground);
            for (Component child : ((JPanel) component).getComponents()) {
                updateComponentTheme(child, panelBackground, labelForeground, buttonBackground, buttonForeground);
            }
        } else if (component instanceof JLabel) {
            component.setForeground(labelForeground);
        } else if (component instanceof JButton) {
            component.setBackground(buttonBackground);
            component.setForeground(buttonForeground);
        } else if (component instanceof JTextField) {
            component.setBackground(new Color(200, 230, 201));
            component.setForeground(Color.BLACK);
        }
    }

    private void adjustWalletBalance(double amount) {
        walletBalance += amount;
        walletLabel.setText(String.format("Wallet Balance: R%.2f", walletBalance));
        JOptionPane.showMessageDialog(this, String.format("Wallet updated. New Balance: R%.2f", walletBalance), "Wallet Update", JOptionPane.INFORMATION_MESSAGE);
    }

    private void initializeTaxis() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("taxis.ser"))) {
            taxis = (ArrayList<Taxi>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            taxis = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Failed to load taxi data. Starting with an empty list.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveTaxiData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("taxis.ser"))) {
            oos.writeObject(taxis);
            JOptionPane.showMessageDialog(this, "Taxi data saved successfully.", "Save Data", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save taxi data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSampleNewsNotifications() {
        notificationsModel.addElement("Welcome to the Taxi Service App!");
        notificationsModel.addElement("Maintenance on Sunday.");
        notificationsModel.addElement("New routes to Pretoria.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu(100.0));
    }
}


