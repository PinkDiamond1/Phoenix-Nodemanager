package app.service.transaction;

public interface ITransferTx {

    void transfer(String from, String to, double amount, double gasPrice, double gasLimit, String password);

}
