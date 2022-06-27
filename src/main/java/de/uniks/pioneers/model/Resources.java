package de.uniks.pioneers.model;

public record Resources(
        Integer unknown,
        Integer grain,
        Integer brick,
        Integer ore,
        Integer lumber,
        Integer wool
) {

    public Resources(Integer grain, Integer brick, Integer ore, Integer lumber, Integer wool) {
        this(null, grain, brick, ore, lumber, wool);
    }
}
