import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.Image;

// Update pada kelas BGame
public class BGame {
    private JLabel backgroundLabel;
    private List<ImageIcon> defaultBackgrounds = new ArrayList<>();
    private List<ImageIcon> highStrength20Backgrounds = new ArrayList<>();
    private List<ImageIcon> highIntelligence20Backgrounds = new ArrayList<>();
    private List<ImageIcon> highStrength40Backgrounds = new ArrayList<>();
    private List<ImageIcon> highIntelligence40Backgrounds = new ArrayList<>();
    private List<ImageIcon> highStrength60Backgrounds = new ArrayList<>();
    private List<ImageIcon> highIntelligence60Backgrounds = new ArrayList<>();
    private List<ImageIcon> highStrength80Backgrounds = new ArrayList<>();
    private List<ImageIcon> highIntelligence80Backgrounds = new ArrayList<>();

    public BGame(JLabel backgroundLabel) {
        this.backgroundLabel = backgroundLabel;
        loadBackgrounds();
    }

    private void loadBackgrounds() {
        try {
            defaultBackgrounds.add(new ImageIcon("bg4.jpg"));
            highStrength20Backgrounds.add(new ImageIcon("Background.jpg"));
            highIntelligence20Backgrounds.add(new ImageIcon("bg2.png"));
            highStrength40Backgrounds.add(new ImageIcon("bg4.png"));
            highIntelligence40Backgrounds.add(new ImageIcon("intelligence40_bg.png"));
            highStrength60Backgrounds.add(new ImageIcon("bg6.png"));
            highIntelligence60Backgrounds.add(new ImageIcon("intelligence60_bg.png"));
            highStrength80Backgrounds.add(new ImageIcon("strength80_bg.png"));
            highIntelligence80Backgrounds.add(new ImageIcon("intelligence80_bg.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading backgrounds!");
        }
    }

    

    public void updateBackground(int strength, int intelligence) {
        ImageIcon selectedIcon;
        if (strength > 80) {
            selectedIcon = highStrength80Backgrounds.get(0);
        } else if (strength > 60) {
            selectedIcon = highStrength60Backgrounds.get(0);
        } else if (strength > 40) {
            selectedIcon = highStrength40Backgrounds.get(0);
        } else if (strength > 20) {
            selectedIcon = highStrength20Backgrounds.get(0);
        } else if (intelligence > 80) {
            selectedIcon = highIntelligence80Backgrounds.get(0);
        } else if (intelligence > 60) {
            selectedIcon = highIntelligence60Backgrounds.get(0);
        } else if (intelligence > 40) {
            selectedIcon = highIntelligence40Backgrounds.get(0);
        } else if (intelligence > 20) {
            selectedIcon = highIntelligence20Backgrounds.get(0);
        } else {
            selectedIcon = defaultBackgrounds.get(0);
        }

        // Update ukuran background agar sesuai dengan label
        Image scaledImage = selectedIcon.getImage().getScaledInstance(backgroundLabel.getWidth(), backgroundLabel.getHeight(), Image.SCALE_SMOOTH);
        backgroundLabel.setIcon(new ImageIcon(scaledImage));
    }
}