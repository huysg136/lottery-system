package com.lottery.controller;

import com.lottery.model.*;
import com.lottery.repository.CartItemRepository;
import com.lottery.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final UserService userService;
    private final CartService cartService;
    private final TicketService ticketService;
    private final CartItemRepository cartItemRepository;

    @GetMapping
    public String viewCart(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<CartItem> cartItems = cartService.getCartItems(user);
        BigDecimal total = cartService.calculateTotal(cartItems);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("cartCount", cartItems.size());
        model.addAttribute("activePage", "cart");
        return "cart/view";
    }

    @PostMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(userDetails.getUsername());
        cartService.removeFromCart(id, user);
        redirectAttributes.addFlashAttribute("success", "Item removed from cart.");
        return "redirect:/cart";
    }

    @GetMapping("/edit/{id}")
    public String editCartItem(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        CartItem item = cartItemRepository.findById(id).orElse(null);
        if (item == null || !item.getUser().getId().equals(user.getId())) {
            return "redirect:/cart";
        }

        model.addAttribute("cartItem", item);
        model.addAttribute("lotteryType", item.getLotteryType());
        model.addAttribute("activePage", "cart");
        model.addAttribute("cartCount", cartService.getCartCount(user));
        return "cart/edit";
    }

    @PostMapping("/update/{id}")
    public String updateCartItem(@PathVariable Long id, 
                                 @RequestParam(required = false) List<Integer> mainNumbers,
                                 @RequestParam(required = false) Integer bonusNumber,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        CartItem item = cartItemRepository.findById(id).orElse(null);

        if (item != null && item.getUser().getId().equals(user.getId())) {
            if (mainNumbers != null && bonusNumber != null) {
                // Manual update
                item.setMainNumbers(mainNumbers);
                item.setBonusNumber(bonusNumber);
            }
            cartItemRepository.save(item);
        }
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(@AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<CartItem> cartItems = cartService.getCartItems(user);

        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Your cart is empty.");
            return "redirect:/cart";
        }

        List<Ticket> tickets = ticketService.checkout(user, cartItems);
        cartService.clearCart(user);

        redirectAttributes.addFlashAttribute("success",
                "Purchase successful! " + tickets.size() + " ticket(s) purchased.");
        return "redirect:/tickets/history";
    }
}
