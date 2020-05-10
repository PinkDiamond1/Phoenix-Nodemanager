package app.repository;

import app.entity.Wallet;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface WalletRepository extends CrudRepository<Wallet, String> {
    Optional<Wallet> findByAddress(final String address);
}
