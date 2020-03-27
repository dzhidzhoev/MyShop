package com.myshop;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.myshop.model.Item;
import com.myshop.repository.CategoryRepository;
import com.myshop.repository.ItemRepository;
import com.myshop.repository.ItemTraitRepository;
import com.myshop.repository.OrderRepository;
import com.myshop.repository.TraitRepository;
import com.myshop.repository.UserRepository;

@SpringBootTest
@TestPropertySource(value = "classpath:test.properties")
public class ItemRepositoryTests extends AbstractTestNGSpringContextTests {
	@Autowired CategoryRepository catRepo;
	@Autowired ItemRepository itemRepo;
	@Autowired OrderRepository orderRepo;
	@Autowired TraitRepository traitRepo;
	@Autowired UserRepository userRepo;
	@Autowired ItemTraitRepository itemTraitRepo;

	@Test
	public void itemCrudTest() {
		assertEquals(itemRepo.findAll().size(), 4);
		
		var category = catRepo.findById(1).get();
		var item = itemRepo.saveAndFlush(new Item()
				.setCategory(category).setName("my item"));
		assertEquals(itemRepo.findById(item.getId()).get().getName(), "my item");
		var id = item.getId();
		assertFalse(itemRepo.findById(item.getId()).get().isActive());
		item = itemRepo.saveAndFlush(item.setActive(true));
		assertTrue(itemRepo.findById(id).get().isActive());
		assertEquals(id, item.getId());
		itemRepo.delete(item);
		itemRepo.flush();
		assertFalse(itemRepo.findById(id).isPresent());
	}
	
	@Test
	public void addNewItemTest() {
		assertEquals(itemRepo.findAll().size(), 4);
		
		var category = catRepo.findById(1).get();
		
		// category null
		assertFalse(itemRepo.addOrUpdateItem(null, null, "new item", 100, null, true, "Hello").getFirst().isPresent());
		// name empty
		assertFalse(itemRepo.addOrUpdateItem(null, category, null, 100, null, true, "Hello").getFirst().isPresent());
		assertFalse(itemRepo.addOrUpdateItem(null, category, "", 100, null, true, "Hello").getFirst().isPresent());
		assertFalse(itemRepo.addOrUpdateItem(null, category, "   \t", 100, null, true, "Hello").getFirst().isPresent());
		// ok
		var item = itemRepo.addOrUpdateItem(null, category, "new item  ", 100, 5, true, "Hello").getFirst().get();
		assertEquals(item.getCategory().getId(), category.getId());
		assertEquals(item.getName(), "new item");
		assertEquals(item.getPrice(), 100);
		assertEquals(item.getCount(), 5);
		assertTrue(item.isActive());
		assertEquals(item.getDescription(), "Hello");
		var id = item.getId();
		itemRepo.delete(item);
		itemRepo.flush();
		assertEquals(itemRepo.findById(id).isPresent(), false);
	}
	
	@Test
	public void updateItemTest() {
		assertEquals(itemRepo.findAll().size(), 4);
		var category = catRepo.findById(1).get();
		var item = itemRepo.addOrUpdateItem(null, category, "new item  ", 100, 5, true, "Hello").getFirst().get();
		
		// category null
		assertFalse(itemRepo.addOrUpdateItem(item.getId(), null, "new item", 100, null, true, "Hello").getFirst().isPresent());
		// name empty
		assertFalse(itemRepo.addOrUpdateItem(item.getId(), category, null, 100, null, true, "Hello").getFirst().isPresent());
		assertFalse(itemRepo.addOrUpdateItem(item.getId(), category, "", 100, null, true, "Hello").getFirst().isPresent());
		assertFalse(itemRepo.addOrUpdateItem(item.getId(), category, "   \t", 100, null, true, "Hello").getFirst().isPresent());
		// ok
		var id = item.getId();
		item = itemRepo.addOrUpdateItem(item.getId(), category, "new name of item", 100, 100, false, "new desc").getFirst().get();
		item = itemRepo.findById(id).get();
		assertEquals(item.getId(), id);
		assertEquals(item.getName(), "new name of item");
		assertEquals(item.getPrice(), 100);
		assertEquals(item.getCount(), 100);
		assertEquals(item.isActive(), false);
		assertEquals(item.getDescription(), "new desc");
		
		itemRepo.delete(item);
		itemRepo.flush();
		assertEquals(itemRepo.findAll().size(), 4);
	}
}
