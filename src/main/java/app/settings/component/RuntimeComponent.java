package app.settings.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RuntimeComponent {

    @JsonIgnore
    public static final String NAME = "runtimeParas";

    @JsonProperty(value = SettingsField.RUNTIME_STOP_PROCESS)
    private int stopProcessTxTimeSlot;

}
