package edu.crimpbit.anaylsis;

import edu.crimpbit.anaylsis.config.AnalysisApplicationConfiguration;
import edu.crimpbit.anaylsis.view.MainLayout;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.javafx.SpringFXApplication;

public class AnalysisApplication extends SpringFXApplication {

    @Autowired
    private MainLayout mainLayout;

    public AnalysisApplication() {
        super(AnalysisApplicationConfiguration.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(mainLayout.getRootNode());
        scene.getStylesheets().addAll(
                AnalysisApplication.class.getResource("/styles/default.css").toExternalForm(),
                AnalysisApplication.class.getResource("/styles/charts.css").toExternalForm()
        );
        stage.setTitle("Crimp Bit Analysis");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

}
