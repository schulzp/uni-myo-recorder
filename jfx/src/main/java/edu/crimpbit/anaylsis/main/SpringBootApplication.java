package edu.crimpbit.anaylsis.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.javafx.EnableFXMLComponents;
import org.springframework.javafx.FXMLComponent;
import org.springframework.stereotype.Service;

public class SpringBootApplication extends Application {

    private static String[] savedArgs;

    private ConfigurableApplicationContext applicationContext;

    private @Autowired SceneService sceneService;

    @Override
    public void init() throws Exception {
        applicationContext = SpringApplication.run(SpringBootApplicationConfiguration.class, savedArgs);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Crimp Bit");
        stage.setScene(sceneService.getMainScene());
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        applicationContext.close();
    }

    public static void main(String[] args) {
        SpringBootApplication.savedArgs = args;
        SpringBootApplication.launch(SpringBootApplication.class, args);
    }

}

@Configuration
@EnableFXMLComponents
class SpringBootApplicationConfiguration {

    @Bean
    public SceneService sceneService() {
        return new SceneService();
    }

    @Bean
    public MainLayout mainLayout() {
        return new MainLayout();
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:bundles/languageBundle");
        return messageSource;
    }

}

@Service
class SceneService {

    @Autowired
    private MainLayout mainLayout;

    public Scene getMainScene() {
        return new Scene(mainLayout.getParent());
    }
}

@FXMLComponent
class MainLayout implements FXMLComponent.ParentAware<SplitPane> {

    private SplitPane parent;

    @Override
    public SplitPane getParent() {
        return parent;
    }

    @Override
    public void setParent(SplitPane parent) {
        this.parent = parent;
    }

}

