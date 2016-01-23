package edu.crimpbit.anaylsis.scene;

import javafx.scene.Scene;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Simple holder of a {@link Scene} instance.
 */
@Component
public class SceneHolder implements Supplier<Optional<Scene>> {

    private Optional<Scene> scene;

    public void setScene(Scene scene) {
        this.scene = Optional.ofNullable(scene);
    }

    @Override
    public Optional<Scene> get() {
        return scene;
    }

}
