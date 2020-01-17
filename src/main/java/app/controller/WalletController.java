package app.controller;

import app.config.ApplicationPaths;
import app.entity.Wallet;
import app.repository.WalletRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import crypto.CPXKey;
import crypto.CryptoService;
import message.request.cmd.GetAccountCmd;
import message.response.ExecResult;
import message.util.GenericJacksonWriter;
import message.util.RequestCallerService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.interfaces.ECPrivateKey;
import java.util.*;

@Controller
@RequestMapping(value = "/" + ApplicationPaths.WALLET_PAGE)
public class WalletController {

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

    @Value("${app.core.rpc}")
    private String rpcUrl;

    private Logger log = LoggerFactory.getLogger(WalletController.class);

    @GetMapping
    public String getWalletPage(Model model) {

        final ArrayList<Map<String, String>> walletList = new ArrayList<>();
        final List<String> addresses = new ArrayList<>();
        walletRepository.findAll().forEach(wallet -> {
            final HashMap<String, String> result = new HashMap<>();
            result.put("walletAddress", wallet.getAddress());
            addresses.add(wallet.getAddress());
            result.put("balance", "0");
            result.put("nonce", "0");
            try {
                final String responseString = requestCaller.postRequest(rpcUrl, new GetAccountCmd(wallet.getAddress()));
                final ExecResult response = jacksonWriter.getObjectFromString(ExecResult.class, responseString);
                result.put("balance", response.isSucceed() ? (String) response.getResult().get("balance") : "0");
                result.put("nonce", response.isSucceed() ? (String) response.getResult().get("nonce"): "0");
                walletList.add(result);
            } catch (Exception e) {
                walletList.add(result);
                log.warn("Wallet request failed");
            }
        });

        final MongoCursor<Document> cursor = mongoClient.getDatabase("apex")
                .getCollection("transaction")
                .find()
                .filter(Filters.and(Filters.ne("type", "Miner"),
                        Filters.or(Filters.all("from", addresses),
                                Filters.all("to", addresses))))
                .sort(new Document("createdAt", -1))
                .limit(20).iterator();

        final ArrayList<Map<String, Object>> txList = new ArrayList<>();
        cursor.forEachRemaining(document -> {
            final HashMap<String, Object> txEntry = new HashMap<>();
            document.forEach(txEntry::put);
            txList.add(txEntry);
        });

        model.addAttribute("transactions", txList);
        model.addAttribute("wallets", walletList);
        model.addAttribute( "mnemonic", CPXKey.generateMnemonic());

        return ApplicationPaths.WALLET_PAGE;

    }

    @PostMapping(params = "action=create")
    public String newWallet(@RequestParam(value = "mnemonic") final String mnemonic,
                            @RequestParam(value = "newWalletPass") final String password,
                            @RequestParam(value = "newWalletPassRep") final String repeat) {
        return importWallet(mnemonic, password, repeat);
    }

    @PostMapping(params = "action=import")
    public String importWallet(@RequestParam(value = "importSecret") final String secret,
                            @RequestParam(value = "newWalletPass") final String password,
                            @RequestParam(value = "newWalletPassRep") final String repeat){

        if(password.equals(repeat)) {
            KeyStore keyStore = null;
            try{
                keyStore = cryptoService.generateKeyStoreFromRawString(password, CryptoService.KEY_NAME, secret);
            } catch (Exception e){
                log.info("Imported Secret is not a valid raw");
            }

            try{
                keyStore = cryptoService.generateKeyStoreFromWif(password, secret);
            } catch (Exception e){
                log.info("Imported Secret is not a valid wif");
            }

            try{
                keyStore = cryptoService.generateKeyStoreFromMnemonic(password, secret);
            } catch (Exception e){
                log.info("Imported Secret is not a valid mnemonic");
            }

            if(keyStore != null) {
                saveKeystore(keyStore, password);
            }
        }

        return ApplicationPaths.WALLET_PATH;

    }

    private void saveKeystore(final KeyStore keyStore, final String password){

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
