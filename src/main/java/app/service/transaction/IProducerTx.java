package app.service.transaction;

public interface IProducerTx {

     void register(String from, double gasPrice, long gasLimit, String password,
                   String registerAddress, String type, String company, String url,
                   String country, String location, Integer longitude, Integer latitude);

     void voteOnProducer(String from, double gasPrice, long gasLimit, String password,
                         String candidate, double votes, String type);

}
