package com.app.ecom.service;

import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.model.CartItem;
import com.app.ecom.model.Product;
import com.app.ecom.model.User;
import com.app.ecom.repository.CartItemRepository;
import com.app.ecom.repository.ProductRepository;
import com.app.ecom.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public boolean addToCart(String userId, CartItemRequest request) {

        Long uid;
        try {
            uid = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            return false;
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            return false;
        }

        Product product = productRepository.findById(request.getProductId())
                .orElse(null);
        if (product == null) return false;

        Integer stock = product.getStockQuantity();
        if (stock == null || stock < request.getQuantity()) {
            return false;
        }

        User user = userRepository.findById(uid).orElse(null);
        if (user == null) return false;

        CartItem existingCartItem =
                cartItemRepository.findByUserAndProduct(user, product);

        if (existingCartItem != null) {

            int newQuantity = existingCartItem.getQuantity() + request.getQuantity();
            existingCartItem.setQuantity(newQuantity);

            existingCartItem.setPrice(
                    product.getPrice().multiply(BigDecimal.valueOf(newQuantity))
            );

            cartItemRepository.save(existingCartItem);

        } else {

            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());

            cartItem.setPrice(
                    product.getPrice().multiply(
                            BigDecimal.valueOf(request.getQuantity())
                    )
            );

            cartItemRepository.save(cartItem);
        }

        product.setStockQuantity(stock - request.getQuantity());
        productRepository.save(product);

        return true;
    }

    public boolean deleteItemFromCart(String userId, Long productId) {

        Long uid;
        try {
            uid = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            return false;
        }

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty())
            return false;

        Optional<User> userOpt = userRepository.findById(uid);
        if (userOpt.isEmpty())
            return false;

        cartItemRepository.deleteByUserAndProduct(
                userOpt.get(),
                productOpt.get()
        );

        return true;
    }
}
