package app.settings;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActorComponent implements ISettingsComponent {

    public final static String ROOTNAME = "actor";

    private final String keyMailBoxType = "mailbox-type";
    private final String keyNodeMailBox = "node-mailbox";
    private final String keyType = "type";
    private final String keyExecutor = "executor";
    private final String keyFixedPoolSize = "fixed-pool-size";
    private final String keyThreadPool = "thread-pool-executor";
    private final String keyThroughput = "throughput";
    private final String keyRpcDispatcher = "rpc-dispatcher";

    private String mailboxType;
    private String rpcType;
    private String executor;
    private int poolSize;
    private int throughput;

    @SuppressWarnings("unchecked")
    public ActorComponent(final HashMap<String, Object> map) {
        final HashMap<String, Object> nodeMailBox = (HashMap<String, Object>) map.get(keyNodeMailBox);
        mailboxType = (String) nodeMailBox.get(keyMailBoxType);
        final HashMap<String, Object> rpcDispatcher = (HashMap<String, Object>) map.get(keyRpcDispatcher);
        rpcType = (String) rpcDispatcher.get(keyType);
        executor = (String) rpcDispatcher.get(keyExecutor);
        throughput = (int) rpcDispatcher.get(keyThroughput);
        final HashMap<String, Object> threadPoolMap = (HashMap<String, Object>) rpcDispatcher.get(keyThreadPool);
        poolSize = (int) threadPoolMap.get(keyFixedPoolSize);
    }

    @Override
    public Map<String, Object> toMap() {
        final HashMap<String, Object> result = new HashMap<>();
        final HashMap<String, String> nodeMailBox = new HashMap<>();
        nodeMailBox.put(keyMailBoxType, mailboxType);
        result.put(keyNodeMailBox, nodeMailBox);
        final HashMap<String, Object> rpcDispatcher = new HashMap<>();
        rpcDispatcher.put(keyType, rpcType);
        rpcDispatcher.put(keyExecutor, executor);
        final HashMap<String, Integer> poolSizeMap = new HashMap<>();
        poolSizeMap.put(keyFixedPoolSize, poolSize);
        rpcDispatcher.put(keyThreadPool, poolSizeMap);
        rpcDispatcher.put(keyThroughput, throughput);
        result.put(keyRpcDispatcher, rpcDispatcher);
        return result;
    }

    @Override
    public String getRootName(){
        return ROOTNAME;
    }

}
