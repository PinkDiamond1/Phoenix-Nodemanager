package app.service.wallet;

import app.entity.Wallet;
import app.repository.WalletRepository;
import com.mongodb.MongoClient;
import crypto.CPXKey;
import crypto.CryptoService;
import message.transaction.IProduceTransaction;
import message.util.GenericJacksonWriter;
import message.util.RequestCallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.interfaces.ECPrivateKey;

@Primary
@Service("WalletLogic")
public class WalletLogic implements IWalletOperations {

    private final Logger log = LoggerFactory.getLogger(WalletLogic.class);

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private RequestCallerService requestCaller;

    @Autowired
    private GenericJacksonWriter jacksonWriter;

    @Autowired
    private IProduceTransaction txFactory;

    @Value("${app.core.rpc}")
    private String rpcUrl;

    @Override
    public void create(final String secret, final String password, final String repeat) {
        if(password.equals(repeat)) {
            KeyStore keyStore = null;
            try {
                keyStore = cryptoService.generateKeyStoreFromRawString(password, CryptoService.KEY_NAME, secret);
            } catch (Exception e) {
                log.info("Imported Secret is not a valid raw");
            }
            try {
                keyStore = cryptoService.generateKeyStoreFromWif(password, secret);
            } catch (Exception e) {
                log.info("Imported Secret is not a valid wif");
            }
            try {
                keyStore = cryptoService.generateKeyStoreFromMnemonic(password, secret);
            } catch (Exception e) {
                log.info("Imported Secret is not a valid mnemonic");
            }
            if (keyStore != null) {
                saveKeystore(keyStore, password);
            }
        }
    }

    @Override
    public void delete(final String address) {
        walletRepository.deleteById(address);
    }

    void saveKeystore(final KeyStore keyStore, final String password){
        try {
            final String address = CPXKey.getPublicAddressCPX((ECPrivateKey) keyStore.getKey(CryptoService.KEY_NAME, password.toCharArray()));
            try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                keyStore.store(out, password.toCharArray());
                final Wallet newWallet = Wallet.builder()
                        .address(address)
                        .keystore(out.toByteArray())
                        .build();
                walletRepository.save(newWallet);
            }
        } catch (Exception e) {
            log.error("Failed to crate wallet: " + e.getMessage());
        }
    }

}
