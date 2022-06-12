package de.uniks.pioneers.model;

public record Resources(
        Integer unknown,
        Integer grain,
        Integer brick,
        Integer ore,
        Integer lumber,
        Integer wool
        ) {

        /*
        Erz zu Kohle
        Getreide zu Walknochen
        Wolle zu Fell
        Lehm zu Packeis
        Holz zu Fisch

         */


        // record fields are immutable
        public Resources updateResources(String type, int i) {
                Resources resources = null;
                switch (type) {
                        case "grain" -> resources = new Resources(unknown,grain+i,brick,ore,lumber,wool);
                        case "brick" -> resources = new Resources(unknown,grain,brick+i,ore,lumber,wool);
                        case "ore" -> resources = new Resources(unknown,grain,brick,ore+i,lumber,wool);
                        case "lumber" -> resources = new Resources(unknown,grain,brick,ore,lumber+i,wool);
                        case "wool" -> resources = new Resources(unknown,grain,brick,ore,lumber,wool+i);
                }
                return resources;
        }
}
