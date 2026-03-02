package com.lottery.service;

import com.lottery.model.*;
import com.lottery.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;

    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUserOrderByAddedAtDesc(user);
    }

    public long getCartCount(User user) {
        return cartItemRepository.countByUser(user);
    }

    @Transactional
    public CartItem addToCart(User user, LotteryType lotteryType, String mainNumbers, int bonusNumber) {
        CartItem item = CartItem.builder()
                .user(user)
                .lotteryType(lotteryType)
                .mainNumbers(mainNumbers)
                .bonusNumber(bonusNumber)
                .build();
        return cartItemRepository.save(item);
    }

    @Transactional
    public CartItem updateCartItem(Long itemId, User user, String mainNumbers, int bonusNumber) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized");
        }
        item.setMainNumbers(mainNumbers);
        item.setBonusNumber(bonusNumber);
        return cartItemRepository.save(item);
    }

    @Transactional
    public void removeFromCart(Long itemId, User user) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized");
        }
        cartItemRepository.delete(item);
    }

    public BigDecimal calculateTotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> item.getLotteryType().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }
}
