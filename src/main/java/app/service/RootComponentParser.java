package app.service;

import app.settings.component.*;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Primary
@Service("RootComponentParser")
public class RootComponentParser implements IParseRootComponent {

    @Override
    public RootComponent getRootComponent(Map<String, Object> formParams) {
        final HashMap<String, Object> components = new HashMap<>();

        // Actor
        final HashMap<String, String> nodeMailbox = new HashMap<>();
        nodeMailbox.put(SettingsField.ACTOR_MAILBOX_TYPE, (String) formParams.get(SettingsField.ACTOR_MAILBOX_TYPE));
        final HashMap<String, Object> rpcDispatcher = new HashMap<>();
        rpcDispatcher.put(SettingsField.ACTOR_TYPE, formParams.get(SettingsField.ACTOR_TYPE));
        rpcDispatcher.put(SettingsField.ACTOR_EXECUTOR, formParams.get(SettingsField.ACTOR_EXECUTOR));
        final HashMap<String, Object> poolSize = new HashMap<>();
        poolSize.put(SettingsField.ACTOR_POOL_SIZE, formParams.get(SettingsField.ACTOR_POOL_SIZE));
        rpcDispatcher.put(SettingsField.ACTOR_THREAD_POOL, poolSize);
        rpcDispatcher.put(SettingsField.ACTOR_THROUGHPUT, formParams.get(SettingsField.ACTOR_THROUGHPUT));
        components.put(ActorComponent.NAME, ActorComponent.builder()
                .nodeMailbox(nodeMailbox)
                .rpcDispatcher(rpcDispatcher)
                .build());

        // Network
        final ArrayList<String> seedPeers = new ArrayList<>();
        int counterSeed = 0;
        while (formParams.containsKey(SettingsField.NETWORK_SEED_PEERS + counterSeed)){
            seedPeers.add((String) formParams.get(SettingsField.NETWORK_SEED_PEERS + counterSeed));
            counterSeed++;
        }

        final ArrayList<String> knownPeers = new ArrayList<>();
        int counterPeer = 0;
        while (formParams.containsKey(SettingsField.NETWORK_KNOWN_PEERS + counterPeer)){
            knownPeers.add((String) formParams.get(SettingsField.NETWORK_KNOWN_PEERS + counterPeer));
            counterPeer++;
        }

        components.put(NetworkComponent.NAME, NetworkComponent.builder()
                .nodeName((String) formParams.get(SettingsField.NETWORK_NODE_NAME))
                .declaredAddress((String) formParams.get(SettingsField.NETWORK_DECLARED_ADDRESS))
                .bindAddress((String) formParams.get(SettingsField.NETWORK_BIND_ADDRESS))
                .peersDb((String) formParams.get(SettingsField.NETWORK_PEERS_DB))
                .seedPeers(seedPeers)
                .knownPeers(knownPeers)
                .acceptOtherPeers(formParams.containsKey(SettingsField.NETWORK_ACCEPT_OTHER_PEERS))
                .agentName((String) formParams.get(SettingsField.NETWORK_AGENT_NAME))
                .maxPacketSize(Integer.parseInt((String)formParams.get(SettingsField.NETWORK_MAX_PACKET_SIZE)))
                .localOnly(formParams.containsKey(SettingsField.NETWORK_LOCAL_ONLY))
                .appVersion((String) formParams.get(SettingsField.NETWORK_APP_VERSION))
                .maxConnections(Integer.parseInt((String)formParams.get(SettingsField.NETWORK_MAX_CONNECTIONS)))
                .connectionTimeout((String) formParams.get(SettingsField.NETWORK_CONNECTION_TIMEOUT))
                .upnp(formParams.containsKey(SettingsField.NETWORK_UPNP) ? "yes" : "no")
                .handshakeTimeout((String) formParams.get(SettingsField.NETWORK_HANDSHAKE_TIMEOUT))
                .controllerTimeout((String) formParams.get(SettingsField.NETWORK_CONTROLLER_TIMEOUT))
                .peerMaxTimeGap(Integer.parseInt((String)formParams.get(SettingsField.NETWORK_PEER_MAX_GAP)))
                .peerSyncNumber(Integer.parseInt((String)formParams.get(SettingsField.NETWORK_PEER_SYNC_NUM)))
                .peerDatabaseMax(Integer.parseInt((String)formParams.get(SettingsField.NETWORK_PEER_DB_MAX)))
                .build());

        // Chain
        final HashMap<String, Object> dataBase = new HashMap<>();
        dataBase.put(SettingsField.CHAIN_DIR, formParams.get(SettingsField.CHAIN_DATA_BASE + SettingsField.CHAIN_DIR));
        dataBase.put(SettingsField.CHAIN_CACHE_ENABLED, formParams.containsKey(SettingsField.CHAIN_DATA_BASE + SettingsField.CHAIN_CACHE_ENABLED));
        dataBase.put(SettingsField.CHAIN_CACHE_SIZE, formParams.get(SettingsField.CHAIN_DATA_BASE + SettingsField.CHAIN_CACHE_SIZE));
        dataBase.put(SettingsField.CHAIN_DB_TYPE, formParams.get(SettingsField.CHAIN_DATA_BASE + SettingsField.CHAIN_DB_TYPE));

        final HashMap<String, Object> forkBase = new HashMap<>();
        forkBase.put(SettingsField.CHAIN_DIR, formParams.get(SettingsField.CHAIN_FORK_BASE + SettingsField.CHAIN_DIR));
        forkBase.put(SettingsField.CHAIN_CACHE_ENABLED, formParams.containsKey(SettingsField.CHAIN_FORK_BASE + SettingsField.CHAIN_CACHE_ENABLED));
        forkBase.put(SettingsField.CHAIN_CACHE_SIZE, formParams.get(SettingsField.CHAIN_FORK_BASE + SettingsField.CHAIN_CACHE_SIZE));
        forkBase.put(SettingsField.CHAIN_DB_TYPE, formParams.get(SettingsField.CHAIN_FORK_BASE + SettingsField.CHAIN_DB_TYPE));

        final HashMap<String, Object> blockBase = new HashMap<>();
        blockBase.put(SettingsField.CHAIN_DIR, formParams.get(SettingsField.CHAIN_BLOCK_BASE + SettingsField.CHAIN_DIR));
        blockBase.put(SettingsField.CHAIN_CACHE_ENABLED, formParams.containsKey(SettingsField.CHAIN_BLOCK_BASE + SettingsField.CHAIN_CACHE_ENABLED));
        blockBase.put(SettingsField.CHAIN_CACHE_SIZE, formParams.get(SettingsField.CHAIN_BLOCK_BASE + SettingsField.CHAIN_CACHE_SIZE));
        blockBase.put(SettingsField.CHAIN_DB_TYPE, formParams.get(SettingsField.CHAIN_BLOCK_BASE + SettingsField.CHAIN_DB_TYPE));

        final ArrayList<HashMap<String, Object>> airdropList = new ArrayList<>();
        int counterAirdrop = 0;
        while (formParams.containsKey(SettingsField.CHAIN_AIRDROP + SettingsField.ADDRESS + counterAirdrop)){
            final HashMap<String, Object> airdrop = new HashMap<>();
            airdrop.put(SettingsField.ADDRESS, formParams.get(SettingsField.CHAIN_AIRDROP + SettingsField.ADDRESS + counterAirdrop));
            airdrop.put(SettingsField.COINS, formParams.get(SettingsField.CHAIN_AIRDROP + SettingsField.COINS + counterAirdrop));
            airdropList.add(airdrop);
            counterAirdrop++;
        }

        final HashMap<String, Object> genesis = new HashMap<>();
        genesis.put(SettingsField.CHAIN_TIMESTAMP, formParams.get(SettingsField.CHAIN_TIMESTAMP));
        genesis.put(SettingsField.CHAIN_GENESIS_PRIVATEKEY, formParams.get(SettingsField.CHAIN_GENESIS_PRIVATEKEY));
        genesis.put(SettingsField.CHAIN_AIRDROP, airdropList);
        components.put(ChainComponent.NAME, ChainComponent.builder()
                .minerAward(Double.parseDouble((String)formParams.get(SettingsField.CHAIN_MINERAWARD)))
                .dataBase(dataBase)
                .forkBase(forkBase)
                .blockBase(blockBase)
                .genesis(genesis)
                .lightNode(formParams.containsKey(SettingsField.CHAIN_LIGHTNODE))
                .build());

        // Consensus
        final ArrayList<HashMap<String, String>> initialWitnessList = new ArrayList<>();
        int counterWitness = 0;
        while (formParams.containsKey(SettingsField.CONSENSUS_INITIAL_WITNESS + SettingsField.ADDRESS + counterWitness)){
            final HashMap<String, String> witness = new HashMap<>();
            witness.put(SettingsField.ADDRESS,(String) formParams.get(SettingsField.CONSENSUS_INITIAL_WITNESS + SettingsField.ADDRESS + counterWitness));
            witness.put(SettingsField.NAME,(String) formParams.get(SettingsField.CONSENSUS_INITIAL_WITNESS + SettingsField.NAME + counterWitness));
            initialWitnessList.add(witness);
            counterWitness++;
        }
        components.put(ConsensusComponent.NAME, ConsensusComponent.builder()
                .produceInterval(Integer.parseInt((String)formParams.get(SettingsField.CONSENSUS_INTERVAL)))
                .acceptableTimeError(Integer.parseInt((String)formParams.get(SettingsField.CONSENSUS_TIME_ERROR)))
                .producerRepetitions(Integer.parseInt((String)formParams.get(SettingsField.CONSENSUS_REPETITIONS)))
                .totalWitnessNum(Integer.parseInt((String)formParams.get(SettingsField.CONSENSUS_TOTAL_WITNESS)))
                .witnessNum(Integer.parseInt((String)formParams.get(SettingsField.CONSENSUS_WITNESS)))
                .candidateAward(Double.parseDouble((String)formParams.get(SettingsField.CONSENSUS_AWARD)))
                .electeTime(Integer.parseInt((String)formParams.get(SettingsField.CONSENSUS_ELECT_TIME)))
                .initialWitness(initialWitnessList)
                .build());

        // Rpc
        components.put(RpcComponent.NAME, RpcComponent.builder()
                .enabled(formParams.containsKey(SettingsField.RPC_ENABLED))
                .host((String) formParams.get(SettingsField.RPC_HOST))
                .port((String) formParams.get(SettingsField.RPC_PORT))
                .build());

        // Miner
        final ArrayList<String> minerList = new ArrayList<>();
        minerList.add((String) formParams.get(SettingsField.MINER_PRIVKEY));
        components.put(MinerComponent.NAME, MinerComponent.builder()
                .privKeys(minerList)
                .forceStartProduce(formParams.containsKey(SettingsField.FORCE_START_PRODUCE))
                .build());

        // Plugins
        final HashMap<String, Object>  mongoDB = new HashMap<>();
        mongoDB.put(SettingsField.PLUGIN_ENABLED, formParams.containsKey(SettingsField.PLUGIN_MONGODB + SettingsField.PLUGIN_ENABLED));
        mongoDB.put(SettingsField.PLUGIN_URI, formParams.get(SettingsField.PLUGIN_URI));
        components.put(PluginsComponent.NAME, PluginsComponent.builder().mongoDb(mongoDB).build());

        // Runtime
        components.put(RuntimeComponent.NAME, RuntimeComponent.builder()
                .stopProcessTxTimeSlot(Integer.parseInt((String)formParams.get(SettingsField.RUNTIME_STOP_PROCESS)))
                .build());

        return RootComponent.builder().components(components).build();
    }

}
