package app.settings.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = MinerComponent.NAME)
public class MinerComponent {

    @JsonIgnore
    public static final String NAME = "miner";

    @JsonProperty(value = SettingsField.MINER_PRIVKEY)
    private ArrayList<String> privKeys;

}
