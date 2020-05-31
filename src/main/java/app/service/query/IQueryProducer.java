package app.service.query;

import java.util.List;
import java.util.Optional;

public interface IQueryProducer {

    Optional<String> getProducerAddress();

    List<String> getAllWitnesses();

    boolean isProducer();

}
