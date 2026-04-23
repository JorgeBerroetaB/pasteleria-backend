package com.pasteleria.cakeapi.repositories;
import com.pasteleria.cakeapi.entities.Cobertura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoberturaRepository extends JpaRepository<Cobertura, Long> {}