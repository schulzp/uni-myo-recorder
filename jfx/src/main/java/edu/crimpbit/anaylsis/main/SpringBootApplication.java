package edu.crimpbit.anaylsis.main;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Locale;

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
class SpringBootApplicationConfiguration {

    @Bean
    public FXMLComponentPostProcessor fxmlComponentPostProcessor() {
        return new FXMLComponentPostProcessor();
    }

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
        return new Scene(mainLayout.getNode());
    }
}

@FXMLComponent(location = "/fxml/MainLayout.fxml")
class MainLayout {

    @FXML
    private SplitPane root;

    public Parent getNode() {
        return root;
    }

}

@Component
class FXMLComponentPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private MessageSourceResourceBundle resourceBundle;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        FXMLComponent annotation = AnnotationUtils.findAnnotation(bean.getClass(), FXMLComponent.class);
        if (annotation != null) {
            String location = annotation.location();
            try {
                load(bean, beanName, location);
            } catch (IOException e) {
                throw new BeanInitializationException("Failed to load FXML component " + bean, e);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        resourceBundle = new MessageSourceResourceBundle(applicationContext, Locale.getDefault());
    }

    protected void load(Object bean, String beanName, String location) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(applicationContext.getResource(location).getURL());
        loader.setResources(resourceBundle);
        loader.setController(bean);

        loader.load();
    }

}
