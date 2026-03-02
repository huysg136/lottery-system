package com.lottery.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LotterySelectionDto {

    @NotNull(message = "Lottery type is required")
    private Long lotteryTypeId;

    @NotNull(message = "Main numbers are required")
    @Size(min = 1, message = "At least one set required")
    private List<Integer> mainNumbers; // 5 numbers

    @NotNull(message = "Bonus number is required")
    @Min(value = 1, message = "Bonus number must be at least 1")
    private Integer bonusNumber;
}
