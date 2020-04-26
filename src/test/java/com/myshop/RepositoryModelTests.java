package com.myshop;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.myshop.model.Cart;
import com.myshop.model.CategoryTrait;
import com.myshop.model.Item;
import com.myshop.model.ItemTrait;
import com.myshop.model.OrderStatus;
import com.myshop.model.Trait;
import com.myshop.model.TypeEnum;
import com.myshop.model.User;
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
public class RepositoryModelTests extends AbstractTestNGSpringContextTests {
	
	@Autowired CategoryRepository catRepo;
	@Autowired ItemRepository itemRepo;
	@Autowired OrderRepository orderRepo;
	@Autowired TraitRepository traitRepo;
	@Autowired UserRepository userRepo;
	@Autowired ItemTraitRepository itemTraitRepo;
	@Autowired CartRepository cartRepo;
	@Autowired OrderItemRepository orderItemRepo;
	@Autowired CategoryTraitRepository catTraitRepo;
	
	void checkTVItems(Set<Item> tvs) {
		assertEquals(tvs.size(), 1);
		var tv = tvs.stream().findAny().get();
		assertEquals(tv.getId(), 2);
		assertEquals(tv.getCategory().getId(), 1);
		assertEquals(tv.getName(), "Телевизор Power9");
		assertEquals(tv.getPrice(), 20000);
		assertEquals(tv.getCount(), 200);
		assertTrue(tv.isActive());
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
		assertTrue(!player.isActive());
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
		assertTrue(fridge.isActive());
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
		assertTrue(wash.isActive());
		assertEquals(wash.getDescription(), "Бери и используй");
	}
	
	@Test
	public void testModelDataCategory() {
		assertEquals(catRepo.count(), 4);
		var tvs = catRepo.findById(1).get();
		assertEquals(tvs.getName(), "Телевизоры");
		assertEquals(tvs.isActive(), (Boolean)true);
		assertEquals(itemRepo.findById(2).get().getCategory().getId(), 1);
		checkTVItems(itemRepo.findItemsByCategoryId(tvs.getId()));
		var tvTraits = catTraitRepo.findByCategoryId(tvs.getId()).stream().map(CategoryTrait::getTrait).map(t -> t.getId()).sorted().toArray();
		assertTrue(Arrays.deepEquals(tvTraits, new Integer[] {1, 2, 3, 4, 5}));
		
		var players = catRepo.findById(2).get();
		assertEquals(players.getName(), "Проигрыватели");
		assertEquals(players.isActive(), (Boolean)true);
		assertEquals(itemRepo.findById(3).get().getCategory().getId(), 2);
		checkPlayers(itemRepo.findItemsByCategoryId(players.getId()));
		assertEquals(catTraitRepo.findByCategoryId(players.getId()).size(), 3);
		var playersTraits = catTraitRepo.findByCategoryId(players.getId()).stream().map(CategoryTrait::getTrait).map(t -> t.getId()).sorted().toArray();
		assertTrue(Arrays.deepEquals(playersTraits, new Integer[] {3, 6, 7}));
		
		var fridges = catRepo.findById(3).get();
		assertEquals(fridges.getName(), "Холодильники");
		assertEquals(fridges.isActive(), (Boolean)true);
		assertEquals(itemRepo.findById(1).get().getCategory().getId(), 3);
		checkFridges(itemRepo.findItemsByCategoryId(fridges.getId()));
		var fridgesTraits = catTraitRepo.findByCategoryId(fridges.getId()).stream().map(CategoryTrait::getTrait).map(t -> t.getId()).sorted().toArray();
		assertTrue(Arrays.deepEquals(fridgesTraits, new Integer[] {1, 2, 3, 4, 5}));
		
		var washes = catRepo.findById(4).get();
		assertEquals(washes.getName(), "Стиральные машины");
		assertEquals(washes.isActive(),(Boolean)true);
		assertEquals(itemRepo.findById(4).get().getCategory().getId(), 4);
		checkWashes(itemRepo.findItemsByCategoryId(washes.getId()));
		var washesTraits = catTraitRepo.findByCategoryId(washes.getId()).stream().map(CategoryTrait::getTrait).map(t -> t.getId()).sorted().toArray();
		assertTrue(Arrays.deepEquals(washesTraits, new Integer[] {1, 2, 3, 4, 5}));
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
			if (other.isAdminOrNull() != null)
				return false;
		} else if (!isAdmin.equals(other.isAdminOrNull()))
			return false;
		if (isDeleted == null) {
			if (other.isDeletedOrNull() != null)
				return false;
		} else if (!isDeleted.equals(other.isDeletedOrNull()))
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
	
	void checkCart1(Set<Cart> items) {
		assertEquals(items.size(), 3);
		ArrayList<Cart> it = new ArrayList<>();
		items.forEach(it::add);
		it.sort((i1, i2) -> {
			if (Integer.compare(i1.getUser().getId(), i2.getUser().getId()) == 0) {
				return Integer.compare(i1.getItem().getId(), i2.getItem().getId());
			}
			return Integer.compare(i1.getUser().getId(), i2.getUser().getId());
		});
		assertEquals(it.get(0).getUser().getId(), 1);
		assertEquals(it.get(1).getUser().getId(), 1);
		assertEquals(it.get(2).getUser().getId(), 1);
		assertEquals(it.get(0).getItem().getId(), 1);
		assertEquals(it.get(1).getItem().getId(), 2);
		assertEquals(it.get(2).getItem().getId(), 3);
		assertEquals(it.get(0).getCount(), 1);
		assertEquals(it.get(1).getCount(), 10);
		assertEquals(it.get(2).getCount(), 200);
	}
	
	@Test
	public void testModelDataUser() {
		assertEquals(userRepo.count(), 3);
		var user1 = userRepo.findById(1).get();
		var user2 = userRepo.findById(2).get();
		var user3 = userRepo.findById(3).get();
		assertTrue(equalsUserData(user1, 
				"1@1.com", 
				"444d01eb0131025c0f712674662ecd25", 
				null, 
				null, 
				true, 
				null, 
				"Админ","Иванов", "Иванович", 
				"+000", 
				"Москва"));
		assertTrue(equalsUserData(user2, 
				"2@1.com", 
				"444d01eb0131025c0f712674662ecd25", 
				null, 
				null, 
				false, 
				null, 
				 "Петр","Петров", "Петрович", 
				"+000", 
				"Москва"));
		assertTrue(equalsUserData(user3, 
				"3@1.com", 
				"444d01eb0131025c0f712674662ecd25", 
				null, 
				null, 
				false, 
				null, 
				 "Семён","Сидоров", "Семёнович", 
				"+000", 
				"Иваново"));
		assertEquals(cartRepo.findByUserId(user2.getId()).size(), 0);
		assertEquals(cartRepo.findByUserId(user3.getId()).size(), 0);
		checkCart1(cartRepo.findByUserId(user1.getId()));
		var ord1 = orderRepo.findByUserId(user1.getId());
		var ord2 = orderRepo.findByUserId(user2.getId());
		assertEquals(ord1.size(), 1);
		assertEquals(ord1.stream().findAny().get().getId(), 1);
		assertEquals(ord2.size(), 2);
		assertEquals(ord2.stream().filter(x -> x.getId() == 2).count(), 1);
		assertEquals(ord2.stream().filter(x -> x.getId() == 3).count(), 1);
	}
	
	boolean equalsTrait(Trait obj, String name, Boolean isSearchable, TypeEnum type, Integer minValue, Integer maxValue, String[] values, String unit) {
		if (obj == null)
			return false;
		Trait other = (Trait) obj;
		if (isSearchable == null) {
			if (other.isSearchable())
				return false;
		} else if (!isSearchable.equals(other.isSearchable()))
			return false;
		if (maxValue == null) {
			if (other.getMaxValue() != null)
				return false;
		} else if (!maxValue.equals(other.getMaxValueScalar()))
			return false;
		if (minValue == null) {
			if (other.getMinValue() != null)
				return false;
		} else if (!minValue.equals(other.getMinValueScalar()))
			return false;
		if (name == null) {
			if (other.getName() != null)
				return false;
		} else if (!name.equals(other.getName()))
			return false;
		if (type != other.getType())
			return false;
		if (unit == null) {
			if (other.getUnit() != null)
				return false;
		} else if (!unit.equals(other.getUnit()))
			return false;
		if (values == null) {
			if (other.getValues() != null)
				return false;
		} else if (!Arrays.deepEquals(values, other.getValues().toArray()))
			return false;
		return true;
	}
	
	@Test
	public void testModelDataTrait() {
		assertEquals(traitRepo.count(), 7);
		assertTrue(equalsTrait(traitRepo.findById(1).get(), "Производитель", true, TypeEnum.StringType, null, null, null, null));
		assertTrue(equalsTrait(traitRepo.findById(2).get(), "Страна сборки", true, TypeEnum.StringType, null, null, null, null));
		assertTrue(equalsTrait(traitRepo.findById(3).get(), "Ширина", true, TypeEnum.IntType, 10, 250, null, "см"));
		assertTrue(equalsTrait(traitRepo.findById(4).get(), "Высота", true, TypeEnum.IntType, 10, 250, null, "см"));
		assertTrue(equalsTrait(traitRepo.findById(7).get(), "Длина", true, TypeEnum.IntType, 10, 250, null, "см"));
		assertTrue(equalsTrait(traitRepo.findById(5).get(), "Толщина", true, TypeEnum.IntType, 1, 250, null, "см"));
		assertTrue(equalsTrait(traitRepo.findById(6).get(), "Стандарт дисков", true, TypeEnum.EnumType, null, null, new String[] {"DVD", "Bluray"}, null));
	}

	@Test
	public void testModelDataItem() {
		assertEquals(itemRepo.count(), 4);
		checkFridges(itemRepo.findById(1).stream().collect(Collectors.toSet()));
		checkTVItems(itemRepo.findById(2).stream().collect(Collectors.toSet()));
		checkPlayers(itemRepo.findById(3).stream().collect(Collectors.toSet()));
		checkWashes(itemRepo.findById(4).stream().collect(Collectors.toSet()));
		assertFalse(itemTraitRepo.findByItemId(itemRepo.findById(2).get().getId()).isEmpty());
		assertFalse(itemTraitRepo.findByItemId(itemRepo.findById(4).get().getId()).isEmpty());
		List<ItemTrait> fridgeTraitSet = 
				itemTraitRepo.findByItemId(itemRepo.findById(1).get().getId()).stream()
				.filter(it -> it.getId().getTraitID() != 1)
				.sorted((i1, i2) -> 
					Integer.compare(i1.getTrait().getId(), i2.getTrait().getId()))
				.collect(Collectors.toList());
		assertEquals(fridgeTraitSet.size(), 3);
		fridgeTraitSet.forEach(f -> assertEquals(f.getValue(), null));
		fridgeTraitSet.stream()
			.map(t -> t.getItem().getId())
			.forEach(t -> assertTrue(t == 1));
		assertTrue(Arrays.deepEquals(fridgeTraitSet.stream()
			.map(t -> t.getTrait().getId())
			.toArray(), new Integer[] {3, 4, 5}));
		assertTrue(Arrays.deepEquals(fridgeTraitSet.stream()
				.map(ItemTrait::getValueInt).toArray(), 
				new Integer[] {50, 150, 70}));
		var labels = itemTraitRepo.findByTraitId(1).stream().map(it -> it.getValue()).collect(Collectors.toSet());
		assertEquals(labels.size(), 4);
		for (var label: List.of("Me", "IBM", "SONTY", "БОШ")) {
			assertTrue(labels.contains(label));
		}
		
		var blurays = itemTraitRepo.findByItemId(itemRepo.findById(3).get().getId()).stream()
				.filter(it -> it.getId().getTraitID() != 1).collect(Collectors.toSet());
		assertEquals(blurays.size(), 1);
		var bluray = blurays.stream().findAny().get();
		assertEquals(bluray.getItem().getId(), 3);
		assertEquals(bluray.getTrait().getId(), 6);
		assertEquals(bluray.getValue(), "Bluray");
		assertEquals(bluray.getValueInt(), 0);
	}
	
	@Test
	public void testModelDataOrder() {
		assertEquals(orderRepo.count(), 3);
		var order1 = orderRepo.findById(1).get();
		assertEquals(order1.getId(), 1);
		assertEquals(order1.getUser().getId(), 1);
		assertNotNull(order1.getOrderTime());
		assertEquals(order1.getName(), "Получатель Х");
		assertEquals(order1.getStatus(), OrderStatus.Processing);
		assertEquals(order1.getTotal(), 1000);
		assertEquals(order1.getDeliveryTime(), null);
		assertEquals(order1.getPhone(), null);
		assertEquals(order1.getEmail(), null);
		assertEquals(order1.getAddress(), null);
		assertEquals(order1.getComment(), null);
		var items1 = orderItemRepo.findByOrderId(order1.getId());
		assertEquals(items1.size(), 1);
		var it11 = items1.stream().findAny().get();
		assertEquals(it11.getOrder().getId(), 1);
		assertEquals(it11.getItem().getId(), 1);
		assertEquals(it11.getPrice(), Integer.valueOf(1000));
		assertEquals(it11.getCount(), Integer.valueOf(1));
		
		var order2 = orderRepo.findById(2).get();
		assertEquals(order2.getId(), 2);
		assertEquals(order2.getUser().getId(), 2);
		assertNotNull(order2.getOrderTime());
		assertEquals(order2.getName(), "Ваня");
		assertEquals(order2.getStatus(), OrderStatus.Canceled);
		assertEquals(order2.getTotal(), 1000);
		assertEquals(order2.getDeliveryTime(), null);
		assertEquals(order2.getPhone(), null);
		assertEquals(order2.getEmail(), null);
		assertEquals(order2.getAddress(), null);
		assertEquals(order2.getComment(), null);
		var items2 = orderItemRepo.findByOrderId(order2.getId());
		assertEquals(items2.size(), 2);
		items2.stream().map(x -> x.getOrder().getId()).forEach(x -> assertEquals(x, (Integer)2));
		Arrays.deepEquals(items2.stream()
					.map(x -> x.getItem().getId()).toArray(),
					new Integer[] {2, 3});
		Arrays.deepEquals(items2.stream()
				.map(x -> x.getPrice()).toArray(),
				new Integer[] {1000, 3000});
		Arrays.deepEquals(items2.stream()
				.map(x -> x.getCount()).toArray(),
				new Integer[] {1, 20});
		
		var order3 = orderRepo.findById(3).get();
		assertEquals(order3.getId(), 3);
		assertEquals(order3.getUser().getId(), 2);
		assertNotNull(order3.getOrderTime());
		assertEquals(order3.getName(), "Петя");
		assertEquals(order3.getStatus(), OrderStatus.Done);
		assertEquals(order3.getTotal(), 1000);
		assertEquals(order3.getDeliveryTime(), null);
		assertEquals(order3.getPhone(), null);
		assertEquals(order3.getEmail(), null);
		assertEquals(order3.getAddress(), null);
		assertEquals(order3.getComment(), null);
		var items3 = orderItemRepo.findByOrderId(order3.getId());
		assertEquals(items3.size(), 1);
		var it31 = items3.stream().findAny().get();
		assertEquals(it31.getOrder().getId(), 3);
		assertEquals(it31.getItem().getId(), 3);
		assertEquals(it31.getPrice(), (Integer)4000);
		assertEquals(it31.getCount(), (Integer)2);
	}
}
