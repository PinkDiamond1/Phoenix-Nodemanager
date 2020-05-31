package app.controller;

import app.config.ApplicationPaths;
import app.service.query.IQueryProducer;
import app.service.query.IQueryWallet;
import app.service.transaction.IProducerTx;
import app.service.transaction.ITransferTx;
import app.service.wallet.IWalletOperations;
import crypto.CPXKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/" + ApplicationPaths.WALLET_PAGE)
public class WalletController {

    @Autowired
    @Qualifier("DataQueryLogic")
    private IQueryProducer producerQuery;

    @Autowired
    @Qualifier("DataQueryLogic")
    private IQueryWallet walletQuery;

    @Autowired
    @Qualifier("WalletLogic")
    private IWalletOperations walletOperations;

    @Autowired
    @Qualifier("TransactionLogic")
    private ITransferTx transferLogic;

    @Autowired
    @Qualifier("TransactionLogic")
    private IProducerTx producerLogic;

    @GetMapping
    public String getWalletPage(Model model) {
        model.addAttribute("addresses", walletQuery.getAllWalletAddress());
        model.addAttribute("transactions", walletQuery.getAllTxForWallets());
        model.addAttribute("wallets", walletQuery.getAllWalletMaps());
        model.addAttribute("witnesses", producerQuery.getAllWitnesses());
        model.addAttribute( "mnemonic", CPXKey.generateMnemonic());
        return ApplicationPaths.WALLET_PAGE;
    }

    @PostMapping(params = "action=create")
    public String newWallet(@RequestParam(value = "mnemonic") final String mnemonic,
                            @RequestParam(value = "newWalletPass") final String password,
                            @RequestParam(value = "newWalletPassRep") final String repeat) {
        return importWallet(mnemonic, password, repeat);
    }

    @PostMapping(params = "action=deleteWallet")
    public String deleteWallet(@RequestParam(value = "address") final String address) {
        walletOperations.delete(address);
        return ApplicationPaths.WALLET_PATH;
    }

    @PostMapping(params = "action=import")
    public String importWallet(@RequestParam(value = "importSecret") final String secret,
                            @RequestParam(value = "newWalletPass") final String password,
                            @RequestParam(value = "newWalletPassRep") final String repeat){
        walletOperations.create(secret, password, repeat);
        return ApplicationPaths.WALLET_PATH;
    }

    @PostMapping(params = "action=transfer")
    public String transfer(@RequestParam(value = "fromAddress") final String from,
                           @RequestParam(value = "toAddress") final String to,
                           @RequestParam(value = "amount") final double amount,
                           @RequestParam(value = "gasPrice") final double gasPrice,
                           @RequestParam(value = "walletPassword") final String password) {
        transferLogic.transfer(from, to, amount, gasPrice, 500, password);
        return ApplicationPaths.WALLET_PATH;
    }

    @PostMapping(params = "action=vote")
    public String vote(@RequestParam(value = "fromAddressVm") final String from,
                       @RequestParam(value = "gasPriceVm") final double gasPrice,
                       @RequestParam(value = "gasLimitVm") final long gasLimit,
                       @RequestParam(value = "walletPasswordVm") final String password,
                       @RequestParam(value = "voteCandidate") final String candidate,
                       @RequestParam(value = "voteAmount") final double votes,
                       @RequestParam(value = "voteType") final String type) {
        producerLogic.voteOnProducer(from, gasPrice, gasLimit, password, candidate, votes, type);
        return ApplicationPaths.WALLET_PATH;
    }

    @PostMapping(params = "action=register")
    public String register(@RequestParam(value = "fromAddressVm") final String from,
                           @RequestParam(value = "gasPriceVm") final double gasPrice,
                           @RequestParam(value = "gasLimitVm") final long gasLimit,
                           @RequestParam(value = "walletPasswordVm") final String password,
                           @RequestParam(value = "registerAddress") final String registerAddress,
                           @RequestParam(value = "registerType") final String type,
                           @RequestParam(value = "company", required = false) final String company,
                           @RequestParam(value = "url", required = false) final String url,
                           @RequestParam(value = "country", required = false) final String country,
                           @RequestParam(value = "location", required = false) final String location,
                           @RequestParam(value = "longitude", required = false) final Integer longitude,
                           @RequestParam(value = "latitude", required = false) final Integer latitude) {
        producerLogic.register(from, gasPrice, gasLimit, password, registerAddress, type,
                company, url, country, location, longitude, latitude);
        return ApplicationPaths.WALLET_PATH;
    }
}
