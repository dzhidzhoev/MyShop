package com.myshop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.myshop.model.Cart;
import com.myshop.model.Item;
import com.myshop.model.Order;
import com.myshop.model.User;
import com.myshop.repository.CategoryRepository;
import com.myshop.repository.ItemRepository;
import com.myshop.repository.OrderRepository;
import com.myshop.repository.TraitRepository;
import com.myshop.repository.UserRepository;

@SpringBootTest
@TestPropertySource(value = "classpath:test.properties")
public class RepositoryModelTests extends AbstractTestNGSpringContextTests {
	
	@Autowired CategoryRepository catRepo;
	@Autowired ItemRepository itemRepo;
	@Autowired OrderRepository orderRepo;
	@Autowired TraitRepository traitRepo;
	@Autowired UserRepository userRepo;
	
	@Test
	public void contextLoads() {
	}
	
	void checkTVItems(Set<Item> tvs) {
		assertEquals(tvs.size(), 1);
		var tv = tvs.stream().findAny().get();
		assertEquals(tv.getId(), 2);
		assertEquals(tv.getCategory().getId(), 1);
		assertEquals(tv.getName(), "Телевизор Power9");
		assertEquals(tv.getPrice(), 20000);
		assertEquals(tv.getCount(), 200);
		assertThat(tv.isActive());
		assertEquals(tv.getDescription(), "Дешевый телевизор");
	}
	
	void checkPlayers(Set<Item> players) {
		assertEquals(players.size(), 1);
		var player = players.stream().findAny().get();
		assertEquals(player.getId(), 3);
		assertEquals(player.getCategory().getId(), 2);
		assertEquals(player.getName(), "Bluray player SONTY");
		assertEquals(player.getPrice(), 9000);
		assertEquals(player.getCount(), 0);
		assertThat(!player.isActive());
		assertEquals(player.getDescription(), "Снят с продажи");
	}
	
	void checkFridges(Set<Item> fridges) {
		assertEquals(fridges.size(), 1);
		var fridge = fridges.stream().findAny().get();
		assertEquals(fridge.getId(), 1);
		assertEquals(fridge.getCategory().getId(), 3);
		assertEquals(fridge.getName(), "Холодильник Me-3000");
		assertEquals(fridge.getPrice(), 100000);
		assertEquals(fridge.getCount(), 10);
		assertThat(fridge.isActive());
		assertEquals(fridge.getDescription(), "Хороший холодильник для дома");
	}
	
	void checkWashes(Set<Item> washes) {
		assertEquals(washes.size(), 1);
		var wash = washes.stream().findAny().get();
		assertEquals(wash.getId(), 4);
		assertEquals(wash.getCategory().getId(), 4);
		assertEquals(wash.getName(), "Стиральная машина БОШ");
		assertEquals(wash.getPrice(), 50000);
		assertEquals(wash.getCount(), 100);
		assertThat(wash.isActive());
		assertEquals(wash.getDescription(), "Бери и используй");
	}
	
	@Test
	public void testModelDataCategory() {
		assertEquals(catRepo.count(), 4);
		var tvs = catRepo.findById(1).get();
		assertEquals(tvs.getName(), "Телевизоры");
		assertEquals(tvs.isActive(), (Boolean)true);
		assertEquals(itemRepo.findById(2).get().getCategory().getId(), 1);
		checkTVItems(tvs.getItems());
		
		var players = catRepo.findById(2).get();
		assertEquals(players.getName(), "Проигрыватели");
		assertEquals(players.isActive(), (Boolean)true);
		assertEquals(itemRepo.findById(3).get().getCategory().getId(), 2);
		checkPlayers(players.getItems());
		
		var fridges = catRepo.findById(3).get();
		assertEquals(fridges.getName(), "Холодильники");
		assertEquals(fridges.isActive(), (Boolean)true);
		assertEquals(itemRepo.findById(1).get().getCategory().getId(), 3);
		checkFridges(fridges.getItems());
		
		var washes = catRepo.findById(4).get();
		assertEquals(washes.getName(), "Стиральные машины");
		assertEquals(washes.isActive(),(Boolean)true);
		assertEquals(itemRepo.findById(4).get().getCategory().getId(), 4);
		checkWashes(washes.getItems());
	}
	
	boolean equalsUserData(User obj, String email, String pwdHash, String emailToken, String pwdChangeToken, Boolean isAdmin, Boolean isDeleted, String firstName, String lastName, String middleName, String phone, String address) {
		if (obj == null)
			return false;
		User other = (User) obj;
		if (address == null) {
			if (other.getAddress() != null)
				return false;
		} else if (!address.equals(other.getAddress()))
			return false;
		
		if (email == null) {
			if (other.getEmail() != null)
				return false;
		} else if (!email.equals(other.getEmail()))
			return false;
		if (emailToken == null) {
			if (other.getEmailToken() != null)
				return false;
		} else if (!emailToken.equals(other.getEmailToken()))
			return false;
		if (firstName == null) {
			if (other.getFirstName() != null)
				return false;
		} else if (!firstName.equals(other.getFirstName()))
			return false;
		if (isAdmin == null) {
			if (other.isAdmin() != null)
				return false;
		} else if (!isAdmin.equals(other.isAdmin()))
			return false;
		if (isDeleted == null) {
			if (other.isDeleted() != null)
				return false;
		} else if (!isDeleted.equals(other.isDeleted()))
			return false;
		if (lastName == null) {
			if (other.getLastName() != null)
				return false;
		} else if (!lastName.equals(other.getLastName()))
			return false;
		if (middleName == null) {
			if (other.getMiddleName() != null)
				return false;
		} else if (!middleName.equals(other.getMiddleName()))
			return false;
		if (phone == null) {
			if (other.getPhone() != null)
				return false;
		} else if (!phone.equals(other.getPhone()))
			return false;
		if (pwdChangeToken == null) {
			if (other.getPwdChangeToken() != null)
				return false;
		} else if (!pwdChangeToken.equals(other.getPwdChangeToken()))
			return false;
		if (pwdHash == null) {
			if (other.getPwdHash() != null)
				return false;
		} else if (!pwdHash.equals(other.getPwdHash()))
			return false;
		return true;
	}
	
	void checkCart1(List<Cart> items) {
		assertEquals(items.size(), 3);
		Cart[] it = new Cart[3];
		it[0] = items.get(0);
		it[1] = items.get(1);
		it[2] = items.get(2);
		Arrays.sort(it, (i1, i2) -> {
			if (Integer.compare(i1.getUser().getId(), i2.getUser().getId()) == 0) {
				return Integer.compare(i1.getItem().getId(), i2.getItem().getId());
			}
			return Integer.compare(i1.getUser().getId(), i2.getUser().getId());
		});
		assertEquals(it[0].getUser().getId(), 1);
		assertEquals(it[1].getUser().getId(), 1);
		assertEquals(it[2].getUser().getId(), 1);
		assertEquals(it[0].getItem().getId(), 1);
		assertEquals(it[1].getItem().getId(), 2);
		assertEquals(it[2].getItem().getId(), 3);
		assertEquals(it[0].getCount(), 1);
		assertEquals(it[1].getCount(), 10);
		assertEquals(it[2].getCount(), 200);
	}
	
	@Test
	public void testModelDataUser() {
		assertEquals(userRepo.count(), 3);
		var user1 = userRepo.findById(1).get();
		var user2 = userRepo.findById(2).get();
		var user3 = userRepo.findById(3).get();
		assertThat(equalsUserData(user1, 
				"1@1.com", 
				"444d01eb0131025c0f712674662ecd25", 
				null, 
				null, 
				true, 
				null, 
				"Иванов", "Админ", "Иванович", 
				"+000", 
				"Москва"));
		assertThat(equalsUserData(user2, 
				"2@1.com", 
				"444d01eb0131025c0f712674662ecd25", 
				null, 
				null, 
				false, 
				null, 
				"Петров", "Петр", "Петрович", 
				"+000", 
				"Москва"));
		assertThat(equalsUserData(user3, 
				"3@1.com", 
				"444d01eb013025c0f712674662ecd25", 
				null, 
				null, 
				false, 
				null, 
				"Сидоров", "Семён", "Семёнович", 
				"+000", 
				"Иваново"));
		assertEquals(user2.getCart().size(), 0);
		assertEquals(user3.getCart().size(), 0);
		checkCart1(user1.getCart());
		var ord1 = user1.getOrders();
		var ord2 = user2.getOrders();
		assertEquals(ord1.size(), 1);
		assertEquals(ord1.stream().findAny().get().getId(), 1);
		assertEquals(ord2.size(), 2);
		assertEquals(ord2.stream().filter(x -> x.getId() == 2).count(), 1);
		assertEquals(ord2.stream().filter(x -> x.getId() == 3).count(), 1);
	}
}
