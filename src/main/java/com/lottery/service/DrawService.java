package com.lottery.service;

import com.lottery.model.LotteryType;
import com.lottery.model.Ticket;
import com.lottery.model.TicketNumber;
import com.lottery.model.User;
import com.lottery.repository.TicketRepository;
import com.lottery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class DrawService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Transactional
    public Map<String, Integer> conductDraw() {
        List<Ticket> pendingTickets = ticketRepository.findByPublishedDateIsNull();
        
        if (pendingTickets.isEmpty()) {
            return Map.of("checked", 0, "winners", 0);
        }

        LocalDateTime now = LocalDateTime.now();
        Map<Long, Set<Integer>> winningMainMap = new HashMap<>();
        Map<Long, Integer> winningBonusMap = new HashMap<>();

        int totalChecked = 0;
        int totalWinners = 0;
        Set<User> updatedUsers = new HashSet<>();

        for (Ticket ticket : pendingTickets) {
            LotteryType type = ticket.getLotteryType();
            
            // Generate winning numbers once per LotteryType during this draw
            if (!winningMainMap.containsKey(type.getId())) {
                winningMainMap.put(type.getId(), generateRandomSet(type.getMainCount(), type.getMainMax()));
                winningBonusMap.put(type.getId(), ThreadLocalRandom.current().nextInt(1, type.getBonusMax() + 1));
            }

            Set<Integer> winMain = winningMainMap.get(type.getId());
            int winBonus = winningBonusMap.get(type.getId());

            BigDecimal ticketWin = BigDecimal.ZERO;

            for (TicketNumber tn : ticket.getTicketNumbers()) {
                Set<Integer> userNumbers = parseNumbers(tn.getMainNumbers());
                int matchCount = 0;
                for (Integer num : userNumbers) {
                    if (winMain.contains(num)) {
                        matchCount++;
                    }
                }
                boolean bonusMatch = (tn.getBonusNumber() == winBonus);
                ticketWin = ticketWin.add(calculatePrize(matchCount, bonusMatch));
            }

            String winStr = winMain.stream().sorted().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("") + "|" + winBonus;
            ticket.setWinningNumbers(winStr);

            ticket.setPublishedDate(now);
            ticket.setWinAmount(ticketWin);
            
            if (ticketWin.compareTo(BigDecimal.ZERO) > 0) {
                User user = ticket.getUser();
                user.setBalance(user.getBalance().add(ticketWin));
                updatedUsers.add(user);
                totalWinners++;
            }
            totalChecked++;
        }

        ticketRepository.saveAll(pendingTickets);
        userRepository.saveAll(updatedUsers);

        return Map.of("checked", totalChecked, "winners", totalWinners);
    }

    private Set<Integer> generateRandomSet(int count, int max) {
        Set<Integer> set = new HashSet<>();
        while (set.size() < count) {
            set.add(ThreadLocalRandom.current().nextInt(1, max + 1));
        }
        return set;
    }

    private Set<Integer> parseNumbers(String numbersStr) {
        Set<Integer> set = new HashSet<>();
        for (String s : numbersStr.split(",")) {
            set.add(Integer.parseInt(s.trim()));
        }
        return set;
    }

    private BigDecimal calculatePrize(int matchCount, boolean bonusMatch) {
        if (matchCount == 5 && bonusMatch) return new BigDecimal("500000000.00"); // Jackpot
        if (matchCount == 5) return new BigDecimal("1000000.00");
        if (matchCount == 4 && bonusMatch) return new BigDecimal("50000.00");
        if (matchCount == 4) return new BigDecimal("1000.00");
        if (matchCount == 3 && bonusMatch) return new BigDecimal("500.00");
        if (matchCount == 3) return new BigDecimal("10.00");
        if (matchCount == 2 && bonusMatch) return new BigDecimal("4.00");
        if (matchCount == 1 && bonusMatch) return new BigDecimal("4.00");
        if (matchCount == 0 && bonusMatch) return new BigDecimal("2.00");
        return BigDecimal.ZERO;
    }
}
