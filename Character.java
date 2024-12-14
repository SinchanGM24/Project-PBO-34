import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Character {
    private String name; // Nama juga digunakan sebagai username di database
    private int energy, fullness, happiness, strength, intelligence;

    public Character(String name, int energy, int fullness, int happiness, int strength, int intelligence) {
        this.name = name;
        this.energy = Math.min(energy, 100);
        this.fullness = Math.min(fullness, 100);
        this.happiness = Math.min(happiness, 100);
        this.strength = Math.min(strength, 100);
        this.intelligence = Math.min(intelligence, 100);
    }

    public void study() {
        intelligence += 1;
        energy -= 5;
        fullness -= 5;
        happiness -= 10;
    }

    public void exercise() {
        strength += 1;
        energy -= 10;
        fullness -= 5;
        happiness -= 5;
    }

    public void play() {
        happiness += 15;
        energy -= 5;
        fullness -= 5;
    }

    public void eat() {
        fullness += 20;
        energy += 10;
    }

    public void sleep() {
        energy += 20;
        happiness += 5;
    }

    public void saveToDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/game_db", "root", "")) {
            // Membatasi nilai maksimal 100
            energy = Math.min(energy, 100);
            fullness = Math.min(fullness, 100);
            happiness = Math.min(happiness, 100);
            strength = Math.min(strength, 100);
            intelligence = Math.min(intelligence, 100);
    
            String query = "UPDATE users SET energy = ?, fullness = ?, happiness = ?, strength = ?, intelligence = ? WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, energy);
            stmt.setInt(2, fullness);
            stmt.setInt(3, happiness);
            stmt.setInt(4, strength);
            stmt.setInt(5, intelligence);
            stmt.setString(6, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to save data: " + e.getMessage());
        }
    }
    

    public void resetCharacter() {
        // Set nilai awal
        energy = 100;
        fullness = 100;
        happiness = 100;
        strength = 0;
        intelligence = 0;
    
        // Simpan nilai awal ke database
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/game_db", "root", "")) {
            String query = "UPDATE users SET energy = ?, fullness = ?, happiness = ?, strength = ?, intelligence = ? WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, energy);
            stmt.setInt(2, fullness);
            stmt.setInt(3, happiness);
            stmt.setInt(4, strength);
            stmt.setInt(5, intelligence);
            stmt.setString(6, name); // Username sebagai identifier
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to reset character: " + e.getMessage());
        }
    }
    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getEnergy() { return energy; }
    public int getFullness() { return fullness; }
    public int getHappiness() { return happiness; }
    public int getStrength() { return strength; }
    public int getIntelligence() { return intelligence; }
    public String getName() { return name; }
}
