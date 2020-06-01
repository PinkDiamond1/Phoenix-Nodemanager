package app.service.query;

import java.util.List;
import java.util.Map;

public interface IQueryWallet {

    List<String> getAllWalletAddress();

    List<Map<String, Object>> getAllWalletMaps();

    List<Map<String, Object>> getAllTxForWallets();

}
