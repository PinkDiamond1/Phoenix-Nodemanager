package app.settings.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcComponent {

    @JsonIgnore
    public static final String NAME = "rpc";

    @JsonProperty(value = SettingsField.RPC_ENABLED)
    private boolean enabled;

    @JsonProperty(value = SettingsField.RPC_HOST)
    private String host;

    @JsonProperty(value = SettingsField.RPC_PORT)
    private String port;

}
