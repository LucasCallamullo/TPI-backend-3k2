package com.tpi.repository;

import com.tpi.model.Deposito;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositoRepository extends JpaRepository<Deposito, Long> {

    List<Deposito> findByOrderByNombreAsc();
}
