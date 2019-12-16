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
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = RuntimeComponent.NAME)
public class RuntimeComponent {

    @JsonIgnore
    public static final String NAME = "runtimeParas";

    @JsonProperty(value = SettingsField.RUNTIME_STOP_PROCESS)
    private int stopProcessTimeSlot;

}
