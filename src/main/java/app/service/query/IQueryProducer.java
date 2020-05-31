package app.service.query;

import java.util.Optional;

public interface IQueryProducer {

    Optional<String> getProducerAddress();

    boolean isProducer();

}
