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
            addGameButton(buttonPanel, "Exit");

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
            case "Exit":
                System.exit(0);
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
            JOptionPane.showMessageDialog(this, "Congratulations! You became the ultimate hero, excelling in both strength and intelligence!");
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

    private boolean showLoginScreen() {
        while (true) { // Loop until login is successful or the user closes the dialog
            JPanel loginPanel = new JPanel() {
                // Override paintComponent to set background image
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    try {
                        // Load the custom background image (Replace with your image path)
                        Image backgroundImage = ImageIO.read(new File("Assets/Background.jpg"));
                        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    } catch (IOException e) {
                        e.printStackTrace(); // Handle error if image is not found
                    }
                }
            };
            loginPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            // Title label (DreamLife)
            JLabel titleLabel = new JLabel("DreamLife");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
            titleLabel.setForeground(Color.WHITE);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            loginPanel.add(titleLabel, gbc);

            // Username and password labels and fields
            JLabel usernameLabel = new JLabel("Username:");
            usernameLabel.setForeground(Color.WHITE);
            JTextField usernameField = new JTextField(15);
            usernameField.setText("Username"); // Set the placeholder text
            usernameField.setForeground(Color.GRAY); // Set the placeholder text color
            usernameField.setBackground(new Color(50, 50, 50)); // Dark background color
            usernameField.setCaretColor(Color.WHITE); // White caret color
            usernameField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
            usernameField.setFont(new Font("Arial", Font.PLAIN, 14));

            // Remove placeholder text when the user focuses on the field
            usernameField.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    if (usernameField.getText().equals("Username")) {
                        usernameField.setText(""); // Remove placeholder text
                        usernameField.setForeground(Color.WHITE); // Change text color to white
                    }
                }

                public void focusLost(java.awt.event.FocusEvent evt) {
                    if (usernameField.getText().isEmpty()) {
                        usernameField.setText("Username"); // Restore placeholder text if the field is empty
                        usernameField.setForeground(Color.GRAY); // Change text color to gray
                    }
                }
            });
            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setForeground(Color.WHITE);
            JPasswordField passwordField = new JPasswordField(15);
            passwordField.setText("Password"); // Set the placeholder text
            passwordField.setForeground(Color.GRAY); // Set the placeholder text color
            passwordField.setBackground(new Color(50, 50, 50)); // Dark background color
            passwordField.setCaretColor(Color.WHITE); // White caret color
            passwordField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
            passwordField.setFont(new Font("Arial", Font.PLAIN, 14));

            // Remove placeholder text when the user focuses on the field
            passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    if (new String(passwordField.getPassword()).equals("Password")) {
                        passwordField.setText(""); // Remove placeholder text
                        passwordField.setForeground(Color.WHITE); // Change text color to white
                    }
                }

                public void focusLost(java.awt.event.FocusEvent evt) {
                    if (new String(passwordField.getPassword()).isEmpty()) {
                        passwordField.setText("Password"); // Restore placeholder text if the field is empty
                        passwordField.setForeground(Color.GRAY); // Change text color to gray
                    }
                }
            });

            // Set the GridBagLayout constraints for each component
            gbc.gridx = 0;
            gbc.gridy = 1;
            loginPanel.add(usernameLabel, gbc);

            gbc.gridx = 1;
            loginPanel.add(usernameField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            loginPanel.add(passwordLabel, gbc);

            gbc.gridx = 1;
            loginPanel.add(passwordField, gbc);

            // Create Login Button with custom styling
            JButton loginButton = new JButton("Login");
            loginButton.setBackground(new Color(0, 120, 215));  // Blue color
            loginButton.setForeground(Color.WHITE);
            loginButton.setFont(new Font("Arial", Font.BOLD, 14));
            loginButton.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 180)));
            loginButton.setPreferredSize(new Dimension(100, 35));
            loginButton.setFocusPainted(false);

            // Hover effect for the button
            loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    loginButton.setBackground(new Color(0, 100, 180)); // Change color on hover
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    loginButton.setBackground(new Color(0, 120, 215)); // Restore original color
                }
            });


            // Show the login dialog
            int option = JOptionPane.showConfirmDialog(
                null,
                loginPanel,
                "Login to DreamLife",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (option == JOptionPane.OK_OPTION) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Authenticate login
                if (authenticate(username, password)) {
                    return true; // Successful login
                } else {
                    JOptionPane.showMessageDialog(
                        null,
                        "Incorrect username or password. Please try again.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                // User pressed Cancel
                int exitOption = JOptionPane.showConfirmDialog(
                    null,
                    "Do you really want to exit?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION
                );

                if (exitOption == JOptionPane.YES_OPTION) {
                    System.exit(0); // Exit the application
                }
            }
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
                    rs.getInt("intelligence")
                );
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