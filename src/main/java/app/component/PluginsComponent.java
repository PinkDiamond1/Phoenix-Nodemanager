package app.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PluginsComponent {

    @JsonIgnore
    public static final String NAME = "plugins";

    @JsonProperty(value = SettingsField.PLUGIN_MONGODB)
    private HashMap<String, Object> mongoDb;

}
