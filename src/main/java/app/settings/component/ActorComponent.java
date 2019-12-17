package app.settings.component;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActorComponent {

    @JsonIgnore
    public static final String NAME = "actor";

    @JsonProperty(value = SettingsField.ACTOR_NODE_MAILBOX)
    private HashMap<String, String> nodeMailbox;

    @JsonProperty(value = SettingsField.ACTOR_RPC_DISPATCHER)
    private HashMap<String, Object> rpcDispatcher;

}
