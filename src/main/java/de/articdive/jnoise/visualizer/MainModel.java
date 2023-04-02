package de.articdive.jnoise.visualizer;

import java.awt.image.BufferedImage;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class MainModel {
    private final ObjectProperty<BufferedImage> noiseImageProperty = new SimpleObjectProperty<>();


    public void setNoiseImage(BufferedImage noiseImageProperty) {
        this.noiseImageProperty.set(noiseImageProperty);
    }

    public ObjectProperty<BufferedImage> getNoiseImageProperty() {
        return noiseImageProperty;
    }


}
