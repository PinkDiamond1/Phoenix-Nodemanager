package app.component;

public class SettingsField {

    // General fields
    public static final String ADDRESS = "addr";
    public static final String NAME = "name";
    public static final String COINS = "coins";

    // Actor component fields
    public static final String ACTOR_NODE_MAILBOX = "node-mailbox";
    public static final String ACTOR_MAILBOX_TYPE = "mailbox-type";
    public static final String ACTOR_RPC_DISPATCHER = "rpc-dispatcher";
    public static final String ACTOR_EXECUTOR= "executor";
    public static final String ACTOR_THREAD_POOL= "thread-pool-executor";
    public static final String ACTOR_POOL_SIZE= "fixed-pool-size";
    public static final String ACTOR_THROUGHPUT= "throughput";
    public static final String ACTOR_TYPE= "type";

    // Chain component fields
    public static final String CHAIN_BLOCK_BASE = "blockBase";
    public static final String CHAIN_CACHE_ENABLED = "cacheEnabled";
    public static final String CHAIN_CACHE_SIZE = "cacheSize";
    public static final String CHAIN_DB_TYPE = "dbType";
    public static final String CHAIN_DIR = "dir";
    public static final String CHAIN_DATA_BASE = "dataBase";
    public static final String CHAIN_FORK_BASE = "forkBase";
    public static final String CHAIN_GENESIS = "genesis";
    public static final String CHAIN_AIRDROP = "genesisCoinAirdrop";
    public static final String CHAIN_GENESIS_PRIVATEKEY = "privateKey";
    public static final String CHAIN_TIMESTAMP = "timeStamp";
    public static final String CHAIN_MINERAWARD = "minerAward";
    public static final String CHAIN_LIGHTNODE = "lightNode";


    // Consensus component fields
    public static final String CONSENSUS_TIME_ERROR = "acceptableTimeError";
    public static final String CONSENSUS_AWARD = "candidateAward";
    public static final String CONSENSUS_ELECT_TIME = "electeTime";
    public static final String CONSENSUS_INITIAL_WITNESS = "initialWitness";
    public static final String CONSENSUS_INTERVAL = "produceInterval";
    public static final String CONSENSUS_REPETITIONS = "producerRepetitions";
    public static final String CONSENSUS_TOTAL_WITNESS = "totalWitnessNum";
    public static final String CONSENSUS_WITNESS = "witnessNum";

    // Miner component fields
    public static final String MINER_PRIVKEY = "privKeys";
    public static final String FORCE_START_PRODUCE = "forceStartProduce";

    // Network component fields
    public static final String NETWORK_ACCEPT_OTHER_PEERS = "acceptOtherPeers";
    public static final String NETWORK_AGENT_NAME = "agentName";
    public static final String NETWORK_APP_VERSION = "appVersion";
    public static final String NETWORK_BIND_ADDRESS = "bindAddress";
    public static final String NETWORK_CONNECTION_TIMEOUT = "connectionTimeout";
    public static final String NETWORK_CONTROLLER_TIMEOUT = "controllerTimeout";
    public static final String NETWORK_DECLARED_ADDRESS = "declaredAddress";
    public static final String NETWORK_HANDSHAKE_TIMEOUT = "handshakeTimeout";
    public static final String NETWORK_KNOWN_PEERS = "knownPeers";
    public static final String NETWORK_LOCAL_ONLY = "localOnly";
    public static final String NETWORK_MAX_CONNECTIONS = "maxConnections";
    public static final String NETWORK_MAX_PACKET_SIZE = "maxPacketSize";
    public static final String NETWORK_NODE_NAME = "nodeName";
    public static final String NETWORK_PEER_DB_MAX = "peerDatabaseMax";
    public static final String NETWORK_PEER_MAX_GAP = "peerMaxTimeGap";
    public static final String NETWORK_PEER_SYNC_NUM = "peerSyncNumber";
    public static final String NETWORK_SEED_PEERS = "seedPeers";
    public static final String NETWORK_PEERS_DB = "peersDB";
    public static final String NETWORK_UPNP = "upnpEnabled";

    // Plugin component fields
    public static final String PLUGIN_MONGODB = "mongodb";
    public static final String PLUGIN_ENABLED = "enabled";
    public static final String PLUGIN_URI = "uri";

    // Plugin component fields
    public static final String RPC_ENABLED = "enabled";
    public static final String RPC_HOST = "host";
    public static final String RPC_PORT = "port";

    // Runtime component fields
    public static final String RUNTIME_STOP_PROCESS = "stopProcessTxTimeSlot";
}
