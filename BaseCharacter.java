abstract class Basecharacter {
    protected String name;
    protected int energy, fullness, happiness, strength, intelligence;

    public Basecharacter(String name) {
        this.name = name;
        this.energy = 100;
        this.fullness = 100;
        this.happiness = 100;
        this.strength = 0;
        this.intelligence = 0;
    }

    
}
