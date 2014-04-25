package fr.rushland.enums;

public enum Constants {
    SECONDS_IN_YEAR(365*24*60*60);

    private int value;
    private Constants(int value) {
        this.value = value;
    }
    public int getValue() {
        return this.value;
    }
}
