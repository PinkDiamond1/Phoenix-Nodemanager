package app.settings.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = RpcComponent.NAME)
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
