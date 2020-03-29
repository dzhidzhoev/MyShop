package com.myshop;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import org.testng.collections.Lists;
import com.myshop.model.Item;
import com.myshop.repository.CategoryRepository;
import com.myshop.repository.ItemRepository;
import com.myshop.repository.ItemRepositoryCustom;
import com.myshop.repository.ItemRepositoryCustom.Term.TermType;
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
	
	@Test
	public void filterTest() {
		assertEquals(itemRepo.findAll().size(), 4);
		var category = catRepo.findById(1).get();
		var trait3 = traitRepo.findById(3).get();
		var trait4 = traitRepo.findById(4).get();
		var category2 = catRepo.findById(2).get();
		var trait6 = traitRepo.findById(6).get();
		// empty
		assertTrue(itemRepo.findItemsByTerms(category, Lists.newArrayList()).isEmpty());
		
		// no trait
		var res = itemRepo.findItemsByTerms(category2, List.of(new ItemRepositoryCustom.Term(TermType.BETWEEN, traitRepo.findById(7).get(), Set.of(), 0, 10000)));
		assertEquals(res.size(), 0);
		
		// between ok
		res = itemRepo.findItemsByTerms(category, List.of(new ItemRepositoryCustom.Term(TermType.BETWEEN, trait3, null, 0, 200)));
		assertEquals(res.size(), 1);
		assertEquals(res.stream().findAny().get().getId(), 1);
		res = itemRepo.findItemsByTerms(category, List.of(new ItemRepositoryCustom.Term(TermType.BETWEEN, trait3, null, 50, 50)));
		assertEquals(res.size(), 1);
		assertEquals(res.stream().findAny().get().getId(), 1);
		// between not ok
		res = itemRepo.findItemsByTerms(category, List.of(new ItemRepositoryCustom.Term(TermType.BETWEEN, trait3, null, 0, 49)));
		assertEquals(res.size(), 0);
		res = itemRepo.findItemsByTerms(category, List.of(new ItemRepositoryCustom.Term(TermType.BETWEEN, trait3, null, 51, 10000)));
		assertEquals(res.size(), 0);
		res = itemRepo.findItemsByTerms(category, List.of(new ItemRepositoryCustom.Term(TermType.BETWEEN, trait3, null, 100, 0)));
		assertEquals(res.size(), 0);
		
		// one of not ok
		res = itemRepo.findItemsByTerms(category2, List.of(new ItemRepositoryCustom.Term(TermType.ONE_OF, trait6, Set.of("DVD", "something cool"), 100, 0)));
		assertEquals(res.size(), 0);
		res = itemRepo.findItemsByTerms(category2, List.of(new ItemRepositoryCustom.Term(TermType.ONE_OF, trait6, Set.of("something cool"), 100, 0)));
		assertEquals(res.size(), 0);
		res = itemRepo.findItemsByTerms(category2, List.of(new ItemRepositoryCustom.Term(TermType.ONE_OF, trait6, Set.of(), 100, 0)));
		assertEquals(res.size(), 0);
		
		// one of ok
		res = itemRepo.findItemsByTerms(category2, List.of(new ItemRepositoryCustom.Term(TermType.ONE_OF, trait6, Set.of("DVD", "Bluray", "something cool"), 50, 50)));
		assertEquals(res.size(), 1);
		assertEquals(res.stream().findAny().get().getId(), 3);
		res = itemRepo.findItemsByTerms(category2, List.of(new ItemRepositoryCustom.Term(TermType.ONE_OF, trait6, Set.of("DVD", "Bluray"), 50, 50)));
		assertEquals(res.size(), 1);
		assertEquals(res.stream().findAny().get().getId(), 3);
		res = itemRepo.findItemsByTerms(category2, List.of(new ItemRepositoryCustom.Term(TermType.ONE_OF, trait6, Set.of("Bluray"), 50, 50)));
		assertEquals(res.size(), 1);
		assertEquals(res.stream().findAny().get().getId(), 3);
		
		// two between not ok
		res = itemRepo.findItemsByTerms(category, List.of(new ItemRepositoryCustom.Term(TermType.BETWEEN, trait3, null, 50, 49),
				new ItemRepositoryCustom.Term(TermType.BETWEEN, trait4, null, 0, 1000)));
		assertEquals(res.size(), 0);
		res = itemRepo.findItemsByTerms(category, List.of(new ItemRepositoryCustom.Term(TermType.BETWEEN, trait3, null, 50, 50),
				new ItemRepositoryCustom.Term(TermType.BETWEEN, trait4, null, 151, 1000)));
		assertEquals(res.size(), 0);
		res = itemRepo.findItemsByTerms(category, List.of(new ItemRepositoryCustom.Term(TermType.BETWEEN, trait3, null, 0, 20),
				new ItemRepositoryCustom.Term(TermType.BETWEEN, trait4, null, 151, 1000)));
		assertEquals(res.size(), 0);
		// two between ok
		res = itemRepo.findItemsByTerms(category, List.of(new ItemRepositoryCustom.Term(TermType.BETWEEN, trait3, null, 50, 50),
				new ItemRepositoryCustom.Term(TermType.BETWEEN, trait4, null, 0, 1000)));
		assertEquals(res.size(), 1);
		assertEquals(res.stream().findAny().get().getId(), 1);
	}
}
