package com.myshop;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import com.myshop.model.Cart;
import com.myshop.model.Cart.ID;
import com.myshop.model.OrderStatus;
import com.myshop.repository.CartRepository;
import com.myshop.repository.CategoryRepository;
import com.myshop.repository.CategoryTraitRepository;
import com.myshop.repository.ItemRepository;
import com.myshop.repository.ItemTraitRepository;
import com.myshop.repository.OrderItemRepository;
import com.myshop.repository.OrderRepository;
import com.myshop.repository.TraitRepository;
import com.myshop.repository.UserRepository;

@SpringBootTest
@TestPropertySource(value = "classpath:test.properties")
public class OrderRepositoryTests extends AbstractTestNGSpringContextTests {
	@Autowired TraitRepository traitRepo;
	@Autowired CategoryRepository catRepo;
	@Autowired CategoryTraitRepository catTraitRepo;
	@Autowired UserRepository userRepo;
	@Autowired OrderRepository orderRepo;
	@Autowired ItemRepository itemRepo;
	@Autowired ItemTraitRepository itemTraitRepo;
	@Autowired OrderItemRepository orderItemRepo;
	@Autowired CartRepository cartRepo;
	
	@Test
	public void placeOrderTest() {
		var user1 = userRepo.findById(1).get();
		Set<Cart> initialUser1Cart = cartRepo.findByUserId(user1.getId());
		Set<Cart> initialUser1CartCopy = initialUser1Cart.stream().collect(Collectors.toSet());
		assertEquals(initialUser1Cart.size(), 3);
		
		// empty cart
		cartRepo.deleteAll(initialUser1Cart);
		cartRepo.flush();
		assertEquals(cartRepo.findByUserId(user1.getId()).size(), 0);
		assertFalse(orderRepo.placeNewOrder(user1, cartRepo, itemRepo, orderItemRepo).getFirst().isPresent());
		// put back to cart
		cartRepo.saveAll(initialUser1Cart);
		cartRepo.flush();
		assertEquals(cartRepo.findByUserId(user1.getId()).size(), 3);
		
		// not available
		assertFalse(orderRepo.placeNewOrder(user1, cartRepo, itemRepo, orderItemRepo).getFirst().isPresent());
		cartRepo.deleteById(new ID(user1.getId(), 3));
		cartRepo.flush();
		assertEquals(cartRepo.findByUserId(user1.getId()).size(), 2);
		initialUser1Cart.remove(new Cart().setId(new ID(1, 3)));
		assertEquals(initialUser1Cart.size(), 2);
		
		// ok
		var order = orderRepo.placeNewOrder(user1, cartRepo, itemRepo, orderItemRepo).getFirst().get();
		assertEquals(cartRepo.findByUserId(user1.getId()).size(), 0);
		int total = initialUser1Cart.stream()
				.map(cartItem -> cartItem.getCount() * itemRepo.findById(cartItem.getId().getItemID()).get().getPrice())
				.reduce(0, Integer::sum);
		assertEquals(order.getTotal(), total);
		assertEquals(orderRepo.findUserIdById(order.getId()), user1.getId());
		assertEquals(order.getStatus(), OrderStatus.Processing);
		var orderItems = orderItemRepo.findByOrderId(order.getId());
		assertEquals(orderItems.size(), initialUser1Cart.size());
		orderItemRepo.deleteAll(orderItems);
		orderItemRepo.flush();
		assertEquals(orderItemRepo.findByOrderId(order.getId()).size(), 0);
		orderRepo.delete(order);
		orderRepo.flush();
		assertEquals(orderRepo.findById(order.getId()).isPresent(), false);
		
		// put back to cart
		cartRepo.saveAll(initialUser1CartCopy);
		cartRepo.flush();
		assertEquals(cartRepo.findByUserId(user1.getId()).size(), 3);
	}
	
	@Test
	public void updateOrderDetailsTest() {
		var order = orderRepo.findById(1).get();
		order = orderRepo.saveAndFlush(order.setId(4));
		var orderId = order.getId();
		assertEquals(orderRepo.count(), 4);
		assertEquals(order.getId(), orderId);
		assertEquals(order.getName(), "Получатель Х");
		assertEquals(order.getAddress(), null);
		
		// empty name
		assertFalse(orderRepo.updateOrderDetails(order, "some time", null, "some phone", "some email", "some address", "some comment", 100).getFirst().isPresent());
		assertFalse(orderRepo.updateOrderDetails(order, "some time", "", "some phone", "some email", "some address", "some comment", 100).getFirst().isPresent());
		assertFalse(orderRepo.updateOrderDetails(order, "some time", "     ", "some phone", "some email", "some address", "some comment", 100).getFirst().isPresent());
		assertFalse(orderRepo.updateOrderDetails(order, "some time", "   \t\n ", "some phone", "some email", "some address", "some comment", 100).getFirst().isPresent());
		// empty address
		assertFalse(orderRepo.updateOrderDetails(order, "some time", "name", "some phone", "some email", null, "some comment", 100).getFirst().isPresent());
		assertFalse(orderRepo.updateOrderDetails(order, "some time", "name", "some phone", "some email", "", "some comment", 100).getFirst().isPresent());
		assertFalse(orderRepo.updateOrderDetails(order, "some time", "name", "some phone", "some email", "    ", "some comment", 100).getFirst().isPresent());
		assertFalse(orderRepo.updateOrderDetails(order, "some time", "name", "some phone", "some email", " \t   ", "some comment", 100).getFirst().isPresent());
		
		// all ok
		for (var time: Lists.newArrayList(null, "some time", "")) {
			for (var phone: Lists.newArrayList(null, "some phone", "")) {
				for (var email: Lists.newArrayList(null, "some email", "")) {
					for (var comment: Lists.newArrayList(null, "some comment", "")) {
						for (int total: Lists.newArrayList(0, 1, 2)) {
							order = orderRepo.updateOrderDetails(order, time, "some name", phone, email, "some address", comment, total).getFirst().get();
							assertEquals(order.getId(), orderId);
							assertEquals(order.getName(), "some name");
							assertEquals(order.getAddress(), "some address");
							assertEquals(order.getDeliveryTime(), time);
							assertEquals(order.getPhone(), phone);
							assertEquals(order.getComment(), comment);
							assertEquals(order.getEmail(), email);
							assertEquals(order.getTotal(), total);
						}
					}
				}
			}
		}
		orderRepo.delete(order);
		orderRepo.flush();
		assertEquals(orderRepo.count(), 3);
	}
}
