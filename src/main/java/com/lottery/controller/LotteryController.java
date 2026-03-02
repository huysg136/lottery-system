package com.lottery.controller;

import com.lottery.model.*;
import com.lottery.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/lottery")
@RequiredArgsConstructor
public class LotteryController {

    private final UserService userService;
    private final LotteryService lotteryService;
    private final CartService cartService;

    @GetMapping("/select")
    public String selectType(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("lotteryTypes", lotteryService.getAllLotteryTypes());
        model.addAttribute("cartCount", cartService.getCartCount(user));
        model.addAttribute("activePage", "lottery");
        return "lottery/select";
    }

    @GetMapping("/numbers/{id}")
    public String numberSelection(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        LotteryType lotteryType = lotteryService.findById(id);
        model.addAttribute("lotteryType", lotteryType);
        model.addAttribute("cartCount", cartService.getCartCount(user));
        model.addAttribute("activePage", "lottery");
        return "lottery/numbers";
    }

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam Long lotteryTypeId,
                            @RequestParam String mainNumbers,
                            @RequestParam int bonusNumber,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(userDetails.getUsername());
        LotteryType lotteryType = lotteryService.findById(lotteryTypeId);

        // Parse and validate
        List<Integer> nums = Arrays.stream(mainNumbers.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        if (!lotteryService.validateNumbers(lotteryType, nums, bonusNumber)) {
            redirectAttributes.addFlashAttribute("error", "Invalid numbers selected. Please try again.");
            return "redirect:/lottery/numbers/" + lotteryTypeId;
        }

        String numbersStr = lotteryService.numbersToString(nums);
        cartService.addToCart(user, lotteryType, numbersStr, bonusNumber);
        redirectAttributes.addFlashAttribute("success", "Ticket added to cart!");
        return "redirect:/cart";
    }

    @PostMapping("/quick-pick")
    @ResponseBody
    public QuickPickResponse quickPick(@RequestParam Long lotteryTypeId) {
        LotteryType lotteryType = lotteryService.findById(lotteryTypeId);
        List<Integer> mainNums = lotteryService.generateQuickPick(lotteryType);
        int bonus = lotteryService.generateQuickPickBonus(lotteryType);
        return new QuickPickResponse(mainNums, bonus);
    }

    public record QuickPickResponse(List<Integer> mainNumbers, int bonusNumber) {}
}
