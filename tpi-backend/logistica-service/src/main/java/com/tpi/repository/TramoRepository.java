package com.tpi.repository;

import com.tpi.model.Tramo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TramoRepository extends JpaRepository<Tramo, Long> {
}
