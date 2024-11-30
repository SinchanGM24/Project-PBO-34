import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DreamLife extends JFrame {
    private Character character = new Character();
    private JLabel lblEnergy, lblFullness, lblHappiness, lblIntelligence, lblStrength;
    private JProgressBar progressBarEnergy, progressBarFullness, progressBarHappiness, progressBarIntelligence, progressBarStrength;

    public DreamLife() {
        setTitle("DreamLife: The Path You Choose");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500); // Perbesar ukuran agar semua elemen terlihat
        setLayout(new GridLayout(7, 1)); // Tambah baris untuk tampilan Strength dan Intelligence

        // Panel Status
        JPanel panelStatus = new JPanel(new GridLayout(5, 2)); // 5 atribut sekarang
        lblEnergy = new JLabel("Energy: " + character.getEnergy());
        lblFullness = new JLabel("Fullness: " + character.getFullness());
        lblHappiness = new JLabel("Happiness: " + character.getHappiness());
        lblIntelligence = new JLabel("Intelligence: " + character.getIntelligence());
        lblStrength = new JLabel("Strength: " + character.getStrength());

        progressBarEnergy = new JProgressBar(0, 100);
        progressBarEnergy.setValue(character.getEnergy());
        progressBarFullness = new JProgressBar(0, 100);
        progressBarFullness.setValue(character.getFullness());
        progressBarHappiness = new JProgressBar(0, 100);
        progressBarHappiness.setValue(character.getHappiness());
        progressBarIntelligence = new JProgressBar(0, 100);
        progressBarIntelligence.setValue(character.getIntelligence());
        progressBarStrength = new JProgressBar(0, 100);
        progressBarStrength.setValue(character.getStrength());

        panelStatus.add(lblEnergy);
        panelStatus.add(progressBarEnergy);
        panelStatus.add(lblFullness);
        panelStatus.add(progressBarFullness);
        panelStatus.add(lblHappiness);
        panelStatus.add(progressBarHappiness);
        panelStatus.add(lblIntelligence);
        panelStatus.add(progressBarIntelligence);
        panelStatus.add(lblStrength);
        panelStatus.add(progressBarStrength);
        add(panelStatus);

        // Panel Tombol Aktivitas
        JPanel panelActions = new JPanel(new GridLayout(2, 3));
        JButton btnStudy = new JButton("Study");
        JButton btnExercise = new JButton("Exercise");
        JButton btnPlay = new JButton("Play");
        JButton btnEat = new JButton("Eat");
        JButton btnSleep = new JButton("Sleep");

        btnStudy.addActionListener(e -> performActivity("study"));
        btnExercise.addActionListener(e -> performActivity("exercise"));
        btnPlay.addActionListener(e -> performActivity("play"));
        btnEat.addActionListener(e -> performActivity("eat"));
        btnSleep.addActionListener(e -> performActivity("sleep"));

        panelActions.add(btnStudy);
        panelActions.add(btnExercise);
        panelActions.add(btnPlay);
        panelActions.add(btnEat);
        panelActions.add(btnSleep);
        add(panelActions);

        // Start game time
        Thread gameThread = new Thread(new GameTime());
        gameThread.start();
    }

    private void performActivity(String action) {
        switch (action) {
            case "study":
                character.setIntelligence(character.getIntelligence() + 10);
                character.setEnergy(character.getEnergy() - 20);
                character.setFullness(character.getFullness() - 10); // Mengurangi kenyang
                break;
            case "exercise":
                character.setStrength(character.getStrength() + 10);
                character.setEnergy(character.getEnergy() - 15);
                character.setFullness(character.getFullness() - 15); // Mengurangi kenyang
                break;
            case "play":
                character.setHappiness(character.getHappiness() + 20);
                character.setEnergy(character.getEnergy() - 10);
                character.setFullness(character.getFullness() - 5); // Sedikit mengurangi kenyang
                break;
            case "eat":
                character.setFullness(character.getFullness() + 30); // Menambah kenyang
                character.setEnergy(character.getEnergy() + 10);
                break;
            case "sleep":
                character.setEnergy(character.getEnergy() + 30);
                break;
        }
        checkGameOver();
        checkEnding();
        updateStatus();
    }

    private void checkGameOver() {
        if (character.getFullness() <= 0 || character.getEnergy() <= 0) {
            JOptionPane.showMessageDialog(this, "Game Over! You starved or ran out of energy.");
            System.exit(0);
        }
    }

    private void checkEnding() {
        if (character.getIntelligence() >= 100) {
            JOptionPane.showMessageDialog(this, "Congratulations! You became a Professor!");
            System.exit(0);
        } else if (character.getStrength() >= 100) {
            JOptionPane.showMessageDialog(this, "Congratulations! You became an Athlete!");
            System.exit(0);
        }
    }

    private void updateStatus() {
        lblEnergy.setText("Energy: " + character.getEnergy());
        lblFullness.setText("Fullness: " + character.getFullness());
        lblHappiness.setText("Happiness: " + character.getHappiness());
        lblIntelligence.setText("Intelligence: " + character.getIntelligence());
        lblStrength.setText("Strength: " + character.getStrength());

        progressBarEnergy.setValue(character.getEnergy());
        progressBarFullness.setValue(character.getFullness());
        progressBarHappiness.setValue(character.getHappiness());
        progressBarIntelligence.setValue(character.getIntelligence());
        progressBarStrength.setValue(character.getStrength());
    }

    class GameTime implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000); // Update setiap detik
                    character.setFullness(character.getFullness() - 1); // Berkurang setiap detik
                    checkGameOver();
                    updateStatus();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DreamLife game = new DreamLife();
            game.setVisible(true);
        });
    }
}
