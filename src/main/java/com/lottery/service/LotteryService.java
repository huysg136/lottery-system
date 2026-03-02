package com.lottery.service;

import com.lottery.model.LotteryType;
import com.lottery.repository.LotteryTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LotteryService {

    private final LotteryTypeRepository lotteryTypeRepository;

    public List<LotteryType> getAllLotteryTypes() {
        return lotteryTypeRepository.findAll();
    }

    public LotteryType findById(Long id) {
        return lotteryTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lottery type not found: " + id));
    }

    /**
     * Generate random (Quick Pick) numbers for a lottery type
     */
    public List<Integer> generateQuickPick(LotteryType lotteryType) {
        Random random = new Random();
        List<Integer> numbers = new ArrayList<>();

        // Generate unique main numbers
        List<Integer> pool = new ArrayList<>();
        for (int i = 1; i <= lotteryType.getMainMax(); i++) {
            pool.add(i);
        }
        Collections.shuffle(pool, random);
        for (int i = 0; i < lotteryType.getMainCount(); i++) {
            numbers.add(pool.get(i));
        }
        Collections.sort(numbers);

        return numbers;
    }

    public int generateQuickPickBonus(LotteryType lotteryType) {
        Random random = new Random();
        return random.nextInt(lotteryType.getBonusMax()) + 1;
    }

    /**
     * Validate numbers for a lottery type
     */
    public boolean validateNumbers(LotteryType lotteryType, List<Integer> mainNumbers, int bonusNumber) {
        if (mainNumbers == null || mainNumbers.size() != lotteryType.getMainCount()) {
            return false;
        }
        // Check uniqueness
        if (mainNumbers.stream().distinct().count() != mainNumbers.size()) {
            return false;
        }
        // Check range
        for (int n : mainNumbers) {
            if (n < 1 || n > lotteryType.getMainMax()) return false;
        }
        // Check bonus
        return bonusNumber >= 1 && bonusNumber <= lotteryType.getBonusMax();
    }

    public String numbersToString(List<Integer> numbers) {
        StringBuilder sb = new StringBuilder();
        List<Integer> sorted = new ArrayList<>(numbers);
        Collections.sort(sorted);
        for (int i = 0; i < sorted.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(sorted.get(i));
        }
        return sb.toString();
    }
}
