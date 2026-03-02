package com.lottery.repository;

import com.lottery.model.LotteryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LotteryTypeRepository extends JpaRepository<LotteryType, Long> {
    Optional<LotteryType> findByName(String name);
}
