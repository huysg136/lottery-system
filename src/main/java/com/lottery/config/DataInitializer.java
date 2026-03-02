package com.lottery.config;

import com.lottery.model.LotteryType;
import com.lottery.repository.LotteryTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

import com.lottery.model.User;
import com.lottery.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final LotteryTypeRepository lotteryTypeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize Lottery Types
        if (lotteryTypeRepository.count() == 0) {
            System.out.println("Initializing lottery types...");

            LotteryType powerball = LotteryType.builder()
                    .name("Powerball")
                    .description("Pick 5 numbers from 1-69 + 1 Powerball from 1-26")
                    .mainCount(5)
                    .mainMax(69)
                    .bonusMax(26)
                    .price(new BigDecimal("2.00"))
                    .jackpotAmount("$500 Million")
                    .nextDrawDate("Wednesday & Saturday")
                    .build();

            LotteryType megaMillions = LotteryType.builder()
                    .name("Mega Millions")
                    .description("Pick 5 numbers from 1-70 + 1 Mega Ball from 1-25")
                    .mainCount(5)
                    .mainMax(70)
                    .bonusMax(25)
                    .price(new BigDecimal("2.00"))
                    .jackpotAmount("$750 Million")
                    .nextDrawDate("Tuesday & Friday")
                    .build();

            lotteryTypeRepository.save(powerball);
            lotteryTypeRepository.save(megaMillions);
            System.out.println("Lottery types initialized successfully.");
        }

        // Initialize Admin User
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                .username("admin")
                .email("admin@lottery.com")
                .password(passwordEncoder.encode("admin123"))
                .firstName("System")
                .lastName("Administrator")
                .role("ROLE_ADMIN")
                .enabled(true)
                .build();
            userRepository.save(admin);
            System.out.println("Admin user initialized successfully.");
        }
    }
}
