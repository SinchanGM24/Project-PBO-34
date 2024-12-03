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
                break;
            case "Exercise":
                character.exercise();
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
            JOptionPane.showMessageDialog(this, "Congratulations! You became the Smarter in the world");
            System.exit(0);
        }
        if (character.getStrength() >= 100) {
            JOptionPane.showMessageDialog(this, "Congratulations! You became the Strongers in the world");
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
            JOptionPane.PLAIN_MESSAGE
        );

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
                    rs.getInt("intelligence")
                );
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
        return false;
    }
    

    public static void main(String[] args) {
        new DreamLife();
    }
}
