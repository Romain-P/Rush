package fr.rushland.server.objects;

import lombok.Getter;
import lombok.Setter;

public class VipPlayer {
    @Getter private String name;
    @Getter @Setter private int grade;

    public VipPlayer(String name, int grade) {
        this.name = name;
        this.grade = grade;
    }
}
