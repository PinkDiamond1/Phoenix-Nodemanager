package app.settings.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NetworkComponent {

    @JsonIgnore
    public static final String NAME = "network";

    @JsonProperty(value = SettingsField.NETWORK_ACCEPT_OTHER_PEERS)
    private boolean acceptOtherPeers;

    @JsonProperty(value = SettingsField.NETWORK_AGENT_NAME)
    private String agentName;

    @JsonProperty(value = SettingsField.NETWORK_APP_VERSION)
    private String appVersion;

    @JsonProperty(value = SettingsField.NETWORK_BIND_ADDRESS)
    private String bindAddress;

    @JsonProperty(value = SettingsField.NETWORK_CONNECTION_TIMEOUT)
    private String connectionTimeout;

    @JsonProperty(value = SettingsField.NETWORK_CONTROLLER_TIMEOUT)
    private String controllerTimeout;

    @JsonProperty(value = SettingsField.NETWORK_DECLARED_ADDRESS)
    private String declaredAddress;

    @JsonProperty(value = SettingsField.NETWORK_HANDSHAKE_TIMEOUT)
    private String handshakeTimeout;

    @JsonProperty(value = SettingsField.NETWORK_KNOWN_PEERS)
    private ArrayList<String> knownPeers;

    @JsonProperty(value = SettingsField.NETWORK_LOCAL_ONLY)
    private boolean localOnly;

    @JsonProperty(value = SettingsField.NETWORK_MAX_CONNECTIONS)
    private int maxConnections;

    @JsonProperty(value = SettingsField.NETWORK_MAX_PACKET_SIZE)
    private int maxPacketSize;

    @JsonProperty(value = SettingsField.NETWORK_NODE_NAME)
    private String nodeName;

    @JsonProperty(value = SettingsField.NETWORK_PEER_DB_MAX)
    private int peerDatabaseMax;

    @JsonProperty(value = SettingsField.NETWORK_PEER_MAX_GAP)
    private int peerMaxTimeGap;

    @JsonProperty(value = SettingsField.NETWORK_PEER_SYNC_NUM)
    private int peerSyncNumber;

    @JsonProperty(value = SettingsField.NETWORK_PEERS_DB)
    private String peersDb;

    @JsonProperty(value = SettingsField.NETWORK_SEED_PEERS)
    private ArrayList<String> seedPeers;

    @JsonProperty(value = SettingsField.NETWORK_UPNP)
    private String upnp;

}
