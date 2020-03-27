package com.myshop.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.myshop.model.Cart;
import com.myshop.model.Item;
import com.myshop.model.Order;
import com.myshop.model.OrderItem;
import com.myshop.model.OrderStatus;
import com.myshop.model.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
	public Set<Order> findByUserId(int userId);
	public List<Order> findByUserId(int userId, Pageable page);
	
	public default Pair<Optional<Order>, String> placeNewOrder(User user, CartRepository cartRepo, ItemRepository itemRepo, OrderItemRepository orderItemRepo) {
		Set<Cart> items = cartRepo.findByUserId(user.getId());
		if (items.isEmpty()) {
			return Pair.of(Optional.empty(), "no items in cart");
		}
		int total = 0;
		for (Cart cartItem : items) {
			Optional<Item> itemRes = itemRepo.findById(cartItem.getId().getItemID());
			if (!itemRes.isPresent() || !itemRes.get().isActive()) {
				return Pair.of(Optional.empty(), "cart contains deleted item");
			}
			total += cartItem.getCount() * itemRes.get().getPrice();
		}
		try {
			var order = save(new Order()
					.setUser(user)
					.setOrderTime(new Timestamp(System.currentTimeMillis()))
					.setStatus(OrderStatus.Processing)
					.setTotal(total));
			cartRepo.deleteAll(items);
			var orderItems = items.stream().map(cartItem -> {
				OrderItem orderItem = new OrderItem();
				var item = itemRepo.findById(cartItem.getId().getItemID()).get();
				orderItem.setOrder(order)
					.setItem(item)
					.setPrice(item.getPrice())
					.setCount(cartItem.getCount());
				return orderItem;
			}).collect(Collectors.toSet());
			orderItemRepo.saveAll(orderItems);
			orderItemRepo.flush();
			return Pair.of(Optional.of(order), "ok");
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return Pair.of(Optional.empty(), "unknown exception " + e.toString());
		}
	}
}
