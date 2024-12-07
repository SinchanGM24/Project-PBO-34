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
        while (true) { // Loop hingga login berhasil atau pengguna menutup dialog
            JPanel loginPanel = new JPanel(new GridLayout(3, 2));
            JTextField usernameField = new JTextField();
            JPasswordField passwordField = new JPasswordField();
    
            loginPanel.add(new JLabel("Username:"));
            loginPanel.add(usernameField);
            loginPanel.add(new JLabel("Password:"));
            loginPanel.add(passwordField);
    
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
    
                // Coba autentikasi
                if (authenticate(username, password)) {
                    return true; // Login berhasil
                } else {
                    // Tampilkan pesan error jika login gagal
                    JOptionPane.showMessageDialog(
                        null,
                        "Incorrect username or password. Please try again.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                // Jika pengguna menekan tombol Cancel
                int exitOption = JOptionPane.showConfirmDialog(
                    null,
                    "Do you really want to exit?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION
                );
    
                if (exitOption == JOptionPane.YES_OPTION) {
                    System.exit(0); // Keluar dari aplikasi
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
