import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;

public class DreamLife extends JFrame {

    private Character character;
    private JProgressBar energyBar, fullnessBar, happinessBar, strengthBar, intelligenceBar;

    private boolean authenticate = false;

    private List<ImageIcon> defaultFrames = new ArrayList<>();
    private List<ImageIcon> highStrength20Frames = new ArrayList<>();
    private List<ImageIcon> highIntelligence20Frames = new ArrayList<>();
    private List<ImageIcon> highStrength40Frames = new ArrayList<>();
    private List<ImageIcon> highIntelligence40Frames = new ArrayList<>();
    private List<ImageIcon> highStrength60Frames = new ArrayList<>();
    private List<ImageIcon> highIntelligence60Frames = new ArrayList<>();
    private List<ImageIcon> highStrength80Frames = new ArrayList<>();
    private List<ImageIcon> highIntelligence80Frames = new ArrayList<>();
    private List<ImageIcon> currentFrames;

    public DreamLife() {
        // Hanya memulai game jika login berhasil
        if (showLoginScreen()) {
            if (character == null) {
                JOptionPane.showMessageDialog(this, "Error loading character data. Exiting game.");
                System.exit(0);
            }

            // Frame setup
            setTitle("DreamLife");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setLayout(new BorderLayout());
            setLocationRelativeTo(null);

            // Center: Character image and stats
            JPanel centerPanel = new JPanel(new BorderLayout());
            add(centerPanel, BorderLayout.CENTER);

            // Label untuk menampilkan gambar animasi
            JLabel characterImage = new JLabel();
            characterImage.setHorizontalAlignment(SwingConstants.CENTER);
            centerPanel.add(characterImage, BorderLayout.CENTER);

            // List untuk menyimpan frame animasi
            try {
                loadFrames(defaultFrames, "idel.png", 10);
                loadFrames(highStrength20Frames, "idel2.png", 10);
                loadFrames(highIntelligence20Frames, "idel2.png", 10);
                loadFrames(highStrength40Frames, "idel3.png", 10);
                loadFrames(highIntelligence40Frames, "idel3.png", 10);
                loadFrames(highStrength60Frames, "idel4.png", 10);
                loadFrames(highIntelligence60Frames, "idel4.png", 10);
                loadFrames(highStrength80Frames, "idel5.png", 10);
                loadFrames(highIntelligence80Frames, "idel6.png", 10);
            } catch (IOException e) {
                e.printStackTrace();
            }

            currentFrames = defaultFrames;

            Thread animationThread = new Thread(() -> {
                int index = 0;
                while (true) {
                    updateCurrentFrames();
                    characterImage.setIcon(currentFrames.get(index));
                    index = (index + 1) % currentFrames.size();

                    try {
                        Thread.sleep(130);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            animationThread.start();
            // Stats section
            JPanel statsPanel = new JPanel(new GridLayout(3, 2, 10, 10));

            energyBar = createProgressBar("Energy", character.getEnergy());
            fullnessBar = createProgressBar("Fullness", character.getFullness());
            happinessBar = createProgressBar("Happiness", character.getHappiness());
            strengthBar = createProgressBar("Strength", character.getStrength());
            intelligenceBar = createProgressBar("Intelligence", character.getIntelligence());

            statsPanel.add(createStatPanel("Energy", energyBar));
            statsPanel.add(createStatPanel("Strength", strengthBar));
            statsPanel.add(createStatPanel("Fullness", fullnessBar));
            statsPanel.add(createStatPanel("Intelligence", intelligenceBar));
            statsPanel.add(createStatPanel("Happiness", happinessBar));
            statsPanel.add(new JLabel()); // Empty placeholder for layout symmetry

            centerPanel.add(statsPanel, BorderLayout.SOUTH);
            add(centerPanel, BorderLayout.CENTER);

            // Footer: Action buttons
            JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
            addGameButton(buttonPanel, "Study");
            addGameButton(buttonPanel, "Exercise");
            addGameButton(buttonPanel, "Play");
            addGameButton(buttonPanel, "Eat");
            addGameButton(buttonPanel, "Sleep");
            addGameButton(buttonPanel, "Log out");

            add(buttonPanel, BorderLayout.SOUTH);

            setVisible(true);
        }
    }

    private void loadFrames(List<ImageIcon> frames, String filePath, int frameCount) throws IOException {
        BufferedImage spriteSheet = ImageIO.read(new File(filePath));
        int frameWidth = spriteSheet.getWidth() / frameCount;
        int frameHeight = spriteSheet.getHeight();
        int scaledWidth = frameWidth * 4;
        int scaledHeight = frameHeight * 4;

        for (int i = 0; i < frameCount; i++) {
            BufferedImage frame = spriteSheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
            Image scaledImage = frame.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            frames.add(new ImageIcon(scaledImage));
        }
    }

    private void updateCurrentFrames() {
        int strength = character.getStrength();
        int intelligence = character.getIntelligence();

        // Prioritaskan kondisi tertinggi terlebih dahulu
        if (strength > 80) {
            currentFrames = highStrength80Frames;
        } else if (strength > 60) {
            currentFrames = highStrength60Frames;
        } else if (strength > 40) {
            currentFrames = highStrength40Frames;
        } else if (strength > 20) {
            currentFrames = highStrength20Frames;
        } else if (intelligence > 80) {
            currentFrames = highIntelligence80Frames;
        } else if (intelligence > 60) {
            currentFrames = highIntelligence60Frames;
        } else if (intelligence > 40) {
            currentFrames = highIntelligence40Frames;
        } else if (intelligence > 20) {
            currentFrames = highIntelligence20Frames;
        } else {
            currentFrames = defaultFrames;
        }
    }

    private JPanel createStatPanel(String name, JProgressBar progressBar) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(name, JLabel.CENTER);
        panel.add(label, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        return panel;
    }

    private JProgressBar createProgressBar(String name, int value) {
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(value);
        return progressBar;
    }

    private void addGameButton(JPanel panel, String action) {
        JButton button = new JButton(action);
        panel.add(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAction(action);
            }
        });
    }

    private void performAction(String action) {
        switch (action) {
            case "Study":
                character.study();
                checkIntelligenceEvent();
                break;
            case "Exercise":
                character.exercise();
                checkStrengthEvent();
                break;
            case "Play":
                character.play();
                break;
            case "Eat":
                character.eat();
                break;
            case "Sleep":
                character.sleep();
                break;
            case "Log out":
                dispose(); 
                showLoginScreen();
                break;
        }

        updateStats();
        character.saveToDatabase(); // Simpan perubahan ke database
        checkEnding();
    }

    private void updateStats() {
        energyBar.setValue(character.getEnergy());
        fullnessBar.setValue(character.getFullness());
        happinessBar.setValue(character.getHappiness());
        strengthBar.setValue(character.getStrength());
        intelligenceBar.setValue(character.getIntelligence());
    }

    private void checkEnding() {
        if (character.getEnergy() <= 0) {
            character.resetCharacter();
            JOptionPane.showMessageDialog(this, "You worked too hard and collapsed. Game Over!");
            System.exit(0);
        }
        if (character.getHappiness() <= 0) {
            character.resetCharacter();
            JOptionPane.showMessageDialog(this, "You lost all joy in life. Game Over!");
            System.exit(0);
        }
        if (character.getStrength() >= 100 && character.getIntelligence() >= 100) {
            JOptionPane.showMessageDialog(this,
                    "Congratulations! You became the ultimate hero, excelling in both strength and intelligence!");
            System.exit(0);
        }
        if (character.getIntelligence() >= 100) {
            character.resetCharacter();
            JOptionPane.showMessageDialog(this, "Congratulations! You became the Smarter in the world");
            System.exit(0);
        }
        if (character.getStrength() >= 100) {
            character.resetCharacter();
            JOptionPane.showMessageDialog(this, "Congratulations! You became the Strongers in the world");
            System.exit(0);
        }

    }

    public boolean showLoginScreen() {
        while (true) {
            JDialog loginDialog = new JDialog((Frame) null, "LOGIN", true); // Modal dialog

            JPanel loginPanel = new JPanel() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    try {
                        Image backgroundImage = ImageIO.read(new File("Background.jpg"));
                        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            loginPanel.setPreferredSize(new Dimension(500, 300));
            GridBagLayout layout = new GridBagLayout();
            loginPanel.setLayout(layout);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.CENTER;

            JLabel titleLabel = new JLabel("DreamLife");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
            titleLabel.setForeground(Color.WHITE);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            layout.setConstraints(titleLabel, gbc);
            loginPanel.add(titleLabel);

            JLabel usernameLabel = new JLabel("Username:");
            usernameLabel.setForeground(Color.WHITE);
            JTextField usernameField = new JTextField(15);
            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setForeground(Color.WHITE);
            JPasswordField passwordField = new JPasswordField(15);

            JButton loginButton = new JButton("Login");
            loginButton.setBackground(new Color(0, 120, 215));
            loginButton.setForeground(Color.WHITE);
            JButton registerButton = new JButton("Register");
            registerButton.setBackground(new Color(0, 120, 215));
            registerButton.setForeground(Color.WHITE);

            gbc.gridwidth = 1;
            gbc.gridx = 0;
            gbc.gridy = 1;
            layout.setConstraints(usernameLabel, gbc);
            loginPanel.add(usernameLabel);

            gbc.gridx = 1;
            layout.setConstraints(usernameField, gbc);
            loginPanel.add(usernameField);

            gbc.gridx = 0;
            gbc.gridy = 2;
            layout.setConstraints(passwordLabel, gbc);
            loginPanel.add(passwordLabel);

            gbc.gridx = 1;
            layout.setConstraints(passwordField, gbc);
            loginPanel.add(passwordField);

            // Mengatur ukuran tombol Login dan Register
            Dimension buttonSize = new Dimension(236, 30); // Ukuran tombol yang seragam

            // Mengatur layout untuk login button (ditempatkan di tengah atas)
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2; // Tombol akan mengambil dua kolom
            loginButton.setPreferredSize(buttonSize); // Mengatur ukuran tombol login
            layout.setConstraints(loginButton, gbc);
            loginPanel.add(loginButton);

            // Mengatur layout untuk register button (ditempatkan di tengah bawah)
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 2; // Tombol akan mengambil dua kolom
            registerButton.setPreferredSize(buttonSize); // Mengatur ukuran tombol register
            layout.setConstraints(registerButton, gbc);
            loginPanel.add(registerButton);

            loginDialog.add(loginPanel);
            loginDialog.pack();
            loginDialog.setLocationRelativeTo(null);

            // ActionListener untuk tombol Login
            loginButton.addActionListener(e -> {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (authenticate(username, password)) {
                    JOptionPane.showMessageDialog(loginDialog, "Login successful!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loginDialog.dispose();
                    authenticate = true; // Status autentikasi berhasil
                } else {
                    JOptionPane.showMessageDialog(loginDialog, "Invalid username or password.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            // ActionListener untuk tombol Register
            registerButton.addActionListener(e -> {
                loginDialog.dispose();
                showRegisterScreen(); // Menampilkan layar registrasi
            });

            // Menambahkan window listener untuk menutup aplikasi jika dialog ditutup
            loginDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    System.exit(0); // Menutup aplikasi ketika dialog ditutup
                }
            });

            loginDialog.setVisible(true);

            // Mengembalikan status autentikasi
            return authenticate;
        }
    }

    private void showRegisterScreen() {
        JDialog registerDialog = new JDialog((Frame) null, "REGISTER", true);

        JPanel registerPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image backgroundImage = ImageIO.read(new File("Background.jpg"));
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    // Menambahkan window listener untuk menutup aplikasi jika dialog ditutup
                    registerDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                            System.exit(0); // Menutup aplikasi ketika dialog ditutup
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        registerPanel.setPreferredSize(new Dimension(500, 300));
        GridBagLayout layout = new GridBagLayout();
        registerPanel.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Register");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        layout.setConstraints(titleLabel, gbc);
        registerPanel.add(titleLabel);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        JTextField usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        JPasswordField passwordField = new JPasswordField(15);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(128, 0, 128));
        loginButton.setForeground(Color.WHITE);
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(128, 0, 128));

        saveButton.setForeground(Color.WHITE);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        layout.setConstraints(usernameLabel, gbc);
        registerPanel.add(usernameLabel);

        gbc.gridx = 1;
        layout.setConstraints(usernameField, gbc);
        registerPanel.add(usernameField);

        gbc.gridx = 0;
        gbc.gridy = 2;
        layout.setConstraints(passwordLabel, gbc);
        registerPanel.add(passwordLabel);

        gbc.gridx = 1;
        layout.setConstraints(passwordField, gbc);
        registerPanel.add(passwordField);

        // Mengatur ukuran tombol Login dan Register
        Dimension buttonSize = new Dimension(236, 30); // Ukuran tombol yang seragam

        // Mengatur layout untuk login button (ditempatkan di tengah atas)
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Tombol akan mengambil dua kolom
        loginButton.setPreferredSize(buttonSize); // Mengatur ukuran tombol login
        layout.setConstraints(loginButton, gbc);
        registerPanel.add(loginButton);

        // Mengatur layout untuk register button (ditempatkan di tengah bawah)
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Tombol akan mengambil dua kolom
        saveButton.setPreferredSize(buttonSize); // Mengatur ukuran tombol register
        layout.setConstraints(saveButton, gbc);
        registerPanel.add(saveButton);

        registerDialog.add(registerPanel);
        registerDialog.pack();
        registerDialog.setLocationRelativeTo(null);

        // ActionListener untuk tombol Login
        loginButton.addActionListener(e -> {
            showLoginScreen();
            registerDialog.dispose();

        });

        saveButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (SaveData(username, password)) {
                JOptionPane.showMessageDialog(registerDialog, "Registration successful! You can now log in.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                registerDialog.dispose();
                showLoginScreen();
            } else {
                JOptionPane.showMessageDialog(registerDialog, "Registration failed. Username might already exist.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerDialog.setVisible(true);
    }

    private boolean SaveData(String username, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/game_db", "root", "")) {
            String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);

            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                return false;
            }

            String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean authenticate(String username, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/game_db", "root", "")) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Muat data atribut karakter
                character = new Character(
                        rs.getString("username"),
                        rs.getInt("energy"),
                        rs.getInt("fullness"),
                        rs.getInt("happiness"),
                        rs.getInt("strength"),
                        rs.getInt("intelligence"));
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
        return false;
    }

    private void showIntelligenceEvent() {
        int addedIntelligence = (int) (Math.random() * 5 + 1);
        int addedStrength = (int) (Math.random() * 5 + 1);
        int addedIntelligence1 = (int) (Math.random() * 5 + 1);
        int addedStrength1 = (int) (Math.random() * 5 + 1);

        String[] options = {
                "Gain Wisdom (+Intelligence " + addedIntelligence + ", +Strength " + addedStrength + ")",
                "Discover Joy (+Intelligence " + addedIntelligence1 + ", +Strenght " + addedStrength1 + ")"
        };

        int choice = JOptionPane.showOptionDialog(
                null,
                "You have reached an intelligence milestone! Choose your reward:\n\n" +
                        "Option 1: Delve into ancient texts, gaining wisdom and physical endurance.\n" +
                        "Option 2: Explore life's joys, expanding your intellect and Strenght.",
                "Intelligence Milestone Event",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            character.setIntelligence(character.getIntelligence() + addedIntelligence);
            character.setStrength(character.getStrength() + addedStrength);
        } else if (choice == 1) {
            character.setIntelligence(character.getIntelligence() + addedIntelligence1);
            character.setStrength(character.getStrength() + addedStrength1);
        }
    }

    private void checkIntelligenceEvent() {
        if (character.getIntelligence() % 10 == 0 && character.getIntelligence() > 0) {
            showIntelligenceEvent();
        }
    }

    private void showStrengthEvent() {
        int addedStrength = (int) (Math.random() * 5 + 1);
        int addedIntelligence = (int) (Math.random() * 5 + 1);
        int addedStrength1 = (int) (Math.random() * 5 + 1);
        int addedIntelligence1 = (int) (Math.random() * 5 + 1);

        String[] options = {
                "Warrior's Path (+Strength " + addedStrength + ", +Intelligence " + addedIntelligence + ")",
                "Strategist's Path (+Strength " + addedStrength1 + ", +Intelligence " + addedIntelligence1 + ")"
        };

        int choice = JOptionPane.showOptionDialog(
                null,
                "You have reached a strength milestone! Choose your reward:\n\n" +
                        "Option 1: Embrace the path of a warrior, strengthening your body and spirit.\n" +
                        "Option 2: Pursue the strategist's path, building both strength and intellect.",
                "Strength Milestone Event",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            character.setStrength(character.getStrength() + addedStrength);
            character.setIntelligence(character.getIntelligence() + addedIntelligence);
        } else if (choice == 1) {
            character.setStrength(character.getStrength() + addedStrength1);
            character.setIntelligence(character.getIntelligence() + addedIntelligence1);
        }
    }

    private void checkStrengthEvent() {
        if (character.getStrength() % 10 == 0 && character.getStrength() > 0) {
            showStrengthEvent();
        }
    }

    public static void main(String[] args) {
        new DreamLife();
    }
}