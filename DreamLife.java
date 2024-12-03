import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DreamLife extends JFrame {

    private Character character;
    private JProgressBar energyBar, fullnessBar, happinessBar, strengthBar, intelligenceBar;

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

            // Daftar gambar untuk animasi
            List<ImageIcon> slimeImages = new ArrayList<>();
            for (int i = 1; i <= 4; i++) {
                slimeImages.add(new ImageIcon("Slime/Slime" + i + ".png"));
            }

            // Label untuk menampilkan gambar
            JLabel characterImage = new JLabel();
            characterImage.setHorizontalAlignment(SwingConstants.CENTER);
            centerPanel.add(characterImage, BorderLayout.CENTER);

            // Thread untuk mengganti gambar secara berurutan
            Thread animationThread = new Thread(() -> {
                int index = 0;
                while (true) {
                    // Ganti gambar
                    characterImage.setIcon(slimeImages.get(index));

                    // Update indeks untuk gambar berikutnya
                    index = (index + 1) % slimeImages.size(); // Loop kembali ke gambar pertama setelah gambar terakhir

                    try {
                        Thread.sleep(300); // Delay antara gambar (300ms)
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            animationThread.start(); // Mulai animasi
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
                // character.setStrength(character.getStrength() + 1);
                // character.setEnergy(character.getEnergy() - 10);
                // character.setFullness(character.getFullness() - 5);
                // character.setHappiness(character.getHappiness() - 5);
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
            JOptionPane.showMessageDialog(this,
                    "Congratulations! You became the ultimate hero, excelling in both strength and intelligence!");
            System.exit(0);
        }
        if (character.getIntelligence() >= 100) {
            JOptionPane.showMessageDialog(this, "Congratulations! You became the Smartest in the world");
            System.exit(0);
        }
        if (character.getStrength() >= 100) {
            JOptionPane.showMessageDialog(this, "Congratulations! You became the Strongest in the world");
            System.exit(0);
        }

    }

    private boolean showLoginScreen() {
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
                JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            return authenticate(username, password);
        } else {
            System.exit(0);
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
        int addedIntelligence1 = (int) (Math.random() * 5 + 1);
        int addedStrength1 = (int) (Math.random() * 5 + 1);
        int addedIntelligence2 = (int) (Math.random() * 5 + 1);
        int addedStrength2 = (int) (Math.random() * 5 + 1);

        String[] options = {
                "Gain Wisdom (+Intelligence " + addedIntelligence1 + ", +Strength " + addedStrength1 + ")",
                "Discover Joy (+Intelligence " + addedIntelligence2 + ", +Strenght " + addedStrength2 + ")"
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
            character.setIntelligence(character.getIntelligence() + addedIntelligence1);
            character.setStrength(character.getStrength() + addedStrength1);
        } else if (choice == 1) {
            character.setIntelligence(character.getIntelligence() + addedIntelligence2);
            character.setStrength(character.getStrength() + addedStrength2);
        }
    }

    private void checkIntelligenceEvent() {
        if (character.getIntelligence() % 10 == 0 && character.getIntelligence() > 0) {
            showIntelligenceEvent();
        }
    }

    private void showStrengthEvent() {
        int addedStrength1 = (int) (Math.random() * 5 + 1);
        int addedIntelligence1 = (int) (Math.random() * 5 + 1);
        int addedStrength2 = (int) (Math.random() * 5 + 1);
        int addedIntelligence2 = (int) (Math.random() * 5 + 1);

        String[] options = {
                "Warrior's Path (+Strength " + addedStrength1 + ", +Intelligence " + addedIntelligence1 + ")",
                "Strategist's Path (+Strength " + addedStrength2 + ", +Intelligence " + addedIntelligence2 + ")"
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
            character.setStrength(character.getStrength() + addedStrength1);
            character.setIntelligence(character.getIntelligence() + addedIntelligence1);
        } else if (choice == 1) {
            character.setStrength(character.getStrength() + addedStrength2);
            character.setIntelligence(character.getIntelligence() + addedIntelligence2);
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
