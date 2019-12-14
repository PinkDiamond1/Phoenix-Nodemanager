package app.config;

import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Optional;

public class PropertiesProfileLoader {

    private static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

    public static void initProperties() {
        final String activeProfile = Optional.ofNullable(System.getProperty(SPRING_PROFILES_ACTIVE)).orElse("dev");
        final PropertySourcesPlaceholderConfigurer config = new PropertySourcesPlaceholderConfigurer();
        final Resource[] resources = new ClassPathResource[]{
                new ClassPathResource("application.properties"),
                new ClassPathResource("application-" + activeProfile + ".properties")
        };
        config.setLocations(resources);
    }

}
