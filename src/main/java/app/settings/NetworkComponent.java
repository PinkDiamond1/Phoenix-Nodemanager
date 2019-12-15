package app.settings;

import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetworkComponent implements ISettingsComponent {

    public static final String ROOTNAME = "network";

    private final String keyNodeName = "nodeName";
    private final String keyBindAddress = "bindAddress";
    private final String keyDeclaredAddress = "declaredAddress";
    private final String keyPeersDB = "peersDB";
    private final String keySeedPeers = "seedPeers";
    private final String keyKnownPeers = "knownPeers";
    private final String keyAgentName = "agentName";
    private final String keyMaxPacketSize = "maxPacketSize";
    private final String keyLocalOnly = "localOnly";
    private final String keyAppVersion = "appVersion";
    private final String keyMaxConnections = "maxConnections";
    private final String keyConnectionTimeout = "connectionTimeout";
    private final String keyUpnpEnabled = "upnpEnabled";
    private final String keyHandshakeTimeout = "handshakeTimeout";
    private final String keyControllerTimeout = "controllerTimeout";
    private final String keyPeerMaxTimeGap = "peerMaxTimeGap";
    private final String keyAcceptOtherPeers = "acceptOtherPeers";
    private final String keyPeerSyncNumber = "peerSyncNumber";
    private final String keyPeerDatabaseMax = "peerDatabaseMax";

    private String nodeName;
    private String bindAddress;
    private String declaredAddress;
    private String peersDB;
    private List<String> seedPeers;
    private List<String> knownPeers;
    private String agentName;
    private int maxPacketSize;
    private boolean localOnly;
    private String appVersion;
    private int maxConnections;
    private String connectionTimeout;
    private String upnpEnabled;
    private String handshakeTimeout;
    private String controllerTimeout;
    private int peerMaxTimeGap;
    private boolean acceptOtherPeers;
    private int peerSyncNumber;
    private int peerDatabaseMax;

    @SuppressWarnings("unchecked")
    public NetworkComponent(final HashMap<String, Object> map){
        nodeName = (String) map.get(keyNodeName);
        bindAddress = (String) map.get(keyBindAddress);
        declaredAddress = (String) map.get(keyDeclaredAddress);
        peersDB = (String) map.get(keyPeersDB);
        seedPeers = (List<String>) map.get(keySeedPeers);
        knownPeers = (List<String>) map.get(keyKnownPeers);
        agentName = (String) map.get(keyAgentName);
        maxPacketSize = (int) map.get(keyMaxPacketSize);
        localOnly = (boolean) map.get(keyLocalOnly);
        appVersion = (String) map.get(keyAppVersion);
        maxConnections = (int) map.get(keyMaxConnections);
        connectionTimeout = (String) map.get(keyConnectionTimeout);
        controllerTimeout = (String) map.get(keyControllerTimeout);
        peerMaxTimeGap = (int) map.get(keyPeerMaxTimeGap);
        acceptOtherPeers = (boolean) map.get(keyAcceptOtherPeers);
        peerSyncNumber = (int) map.get(keyPeerSyncNumber);
        peerDatabaseMax = (int) map.get(keyPeerDatabaseMax);
    }

    @Override
    public Map<String, Object> toMap() {
        final HashMap<String, Object> result = new HashMap<>();
        result.put(keyNodeName, nodeName);
        result.put(keyBindAddress, bindAddress);
        result.put(keyDeclaredAddress, declaredAddress);
        result.put(keyPeersDB, peersDB);
        result.put(keySeedPeers, seedPeers);
        result.put(keyKnownPeers, knownPeers);
        result.put(keyAgentName, agentName);
        result.put(keyMaxPacketSize, maxPacketSize);
        result.put(keyLocalOnly, localOnly);
        result.put(keyAppVersion, appVersion);
        result.put(keyMaxConnections, maxConnections);
        result.put(keyConnectionTimeout, connectionTimeout);
        result.put(keyUpnpEnabled, upnpEnabled);
        result.put(keyHandshakeTimeout, handshakeTimeout);
        result.put(keyControllerTimeout, controllerTimeout);
        result.put(keyPeerMaxTimeGap, peerMaxTimeGap);
        result.put(keyAcceptOtherPeers, keyAcceptOtherPeers);
        result.put(keyPeerSyncNumber, peerSyncNumber);
        result.put(keyPeerDatabaseMax, peerDatabaseMax);
        return result;
    }

    @Override
    public String getRootName() {
        return ROOTNAME;
    }

}
