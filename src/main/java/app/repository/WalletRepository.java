package app.repository;

import app.entity.Wallet;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface WalletRepository extends CrudRepository<Wallet, String> {
    List<Wallet> findAll();
}
