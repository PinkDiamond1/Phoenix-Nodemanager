package app.repository;

import app.entity.TelegramConfiguration;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TelegramConfigurationRepository extends CrudRepository<TelegramConfiguration, Long> {

    Optional<TelegramConfiguration> findFirstByTokenNotNull();

}
