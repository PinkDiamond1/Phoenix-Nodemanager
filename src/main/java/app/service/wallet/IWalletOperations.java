package app.service.wallet;

public interface IWalletOperations {

    void create(String secret, String password, String repeat);

    void delete(String address);

}
