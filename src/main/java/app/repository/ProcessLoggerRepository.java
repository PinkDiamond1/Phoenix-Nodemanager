package app.repository;

import app.entity.ProcessLogger;
import org.springframework.data.repository.CrudRepository;

public interface ProcessLoggerRepository extends CrudRepository<ProcessLogger, String> { }
