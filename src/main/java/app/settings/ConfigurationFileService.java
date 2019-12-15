package app.settings;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationFileService {

    public static void writeRootComponentToSettings(final String filePath, final ISettingsComponent configComponent) throws IOException {
        final Config config = ConfigFactory.parseMap(configComponent.toMap());
        try(FileOutputStream fos = new FileOutputStream(filePath)) {
            try (DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos))) {
                outStream.writeBytes(config.root().render(ConfigRenderOptions.concise()
                        .setFormatted(true)
                        .setJson(false)));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static RootComponent loadRootComponentFromSettings(final String filePath) {
        final Map<String, Object> configMap = ConfigFactory.parseFile(Paths.get(filePath).toFile()).root().unwrapped();
        final HashMap<String, Object> rootComponent = (HashMap<String, Object>) configMap.get(RootComponent.ROOTNAME);
        return RootComponent.builder().components(rootComponent).build();
    }

}
