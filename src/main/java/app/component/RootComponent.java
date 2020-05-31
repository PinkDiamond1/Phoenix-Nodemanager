package app.component;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.HashMap;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RootComponent {

    @JsonProperty(value = "apex")
    private HashMap<String, Object> components;

}
