public class Character {
    private int energy = 100;
    private int fullness = 100; // 100 berarti kenyang penuh
    private int happiness = 50;
    private int intelligence = 0;
    private int strength = 0;

    public int getEnergy() { return energy; }
    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(100, energy)); // Batas 0-100
    }

    public int getFullness() { return fullness; }
    public void setFullness(int fullness) {
        this.fullness = Math.max(0, Math.min(100, fullness)); // Batas 0-100
    }

    public int getHappiness() { return happiness; }
    public void setHappiness(int happiness) {
        this.happiness = Math.max(0, Math.min(100, happiness));
    }

    public int getIntelligence() { return intelligence; }
    public void setIntelligence(int intelligence) {
        this.intelligence = Math.max(0, intelligence);
    }

    public int getStrength() { return strength; }
    public void setStrength(int strength) {
        this.strength = Math.max(0, strength);
    }
}
