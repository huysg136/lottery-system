package com.lottery.repository;

import com.lottery.model.CartItem;
import com.lottery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserOrderByAddedAtDesc(User user);
    long countByUser(User user);

    @Modifying
    @Transactional
    void deleteByUser(User user);
}
