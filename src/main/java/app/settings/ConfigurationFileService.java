package app.settings;

import app.settings.component.RootComponent;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import message.util.GenericJacksonWriter;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class ConfigurationFileService {

    private final static GenericJacksonWriter jacksonWriter = new GenericJacksonWriter();

    public static void writeSettings(final String filename, final RootComponent settingsConf) throws IOException {

        final Config config1 = ConfigFactory.parseString(jacksonWriter.getStringFromRequestObject(settingsConf));
        try(FileOutputStream fos = new FileOutputStream(filename)) {
            try (DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos))) {
                outStream.writeBytes(config1.root().render(ConfigRenderOptions.concise()
                        .setFormatted(true)
                        .setJson(false)));
            }
        }

    }

    public static RootComponent loadSettings(final String filePath) throws IOException {

        final String configJson = ConfigFactory.parseFile(Paths.get(filePath).toFile()).root().render(ConfigRenderOptions.concise()
                .setFormatted(true)
                .setJson(true));
        return jacksonWriter.getObjectFromString(RootComponent.class, configJson);

    }

}
