package app.settings.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChainComponent {

    @JsonIgnore
    public static final String NAME = "chain";

    @JsonProperty(value = SettingsField.CHAIN_BLOCK_BASE)
    private HashMap<String, Object> blockBase;

    @JsonProperty(value = SettingsField.CHAIN_DATA_BASE)
    private HashMap<String, Object> dataBase;

    @JsonProperty(value = SettingsField.CHAIN_FORK_BASE)
    private HashMap<String, Object> forkBase;

    @JsonProperty(value = SettingsField.CHAIN_GENESIS)
    private HashMap<String, Object> genesis;

    @JsonProperty(value = SettingsField.CHAIN_MINERAWARD)
    private double minerAward;

    @JsonProperty(value = SettingsField.CHAIN_LIGHTNODE)
    private boolean lightNode;

}
