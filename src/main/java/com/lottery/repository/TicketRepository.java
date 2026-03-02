package com.lottery.repository;

import com.lottery.model.Ticket;
import com.lottery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserOrderByDateOfSellingDesc(User user);
}
