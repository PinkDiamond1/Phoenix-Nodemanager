package app.settings;

import java.util.Map;

public interface ISettingsComponent {

    Map<String, Object> toMap();

    String getRootName();

}
