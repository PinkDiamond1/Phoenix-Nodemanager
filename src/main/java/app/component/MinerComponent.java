package app.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MinerComponent {

    @JsonIgnore
    public static final String NAME = "miner";

    @JsonProperty(value = SettingsField.MINER_PRIVKEY)
    private ArrayList<String> privKeys;

    @JsonProperty(value = SettingsField.FORCE_START_PRODUCE)
    private boolean forceStartProduce;

}
