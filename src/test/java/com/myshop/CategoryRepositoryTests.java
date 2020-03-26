package com.myshop;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.myshop.model.Category;
import com.myshop.repository.CategoryRepository;
import com.myshop.repository.ItemRepository;
import com.myshop.repository.ItemTraitRepository;
import com.myshop.repository.OrderRepository;
import com.myshop.repository.TraitRepository;
import com.myshop.repository.UserRepository;

@SpringBootTest
@TestPropertySource(value = "classpath:test.properties")
public class CategoryRepositoryTests extends AbstractTestNGSpringContextTests {
	
	@Autowired CategoryRepository catRepo;
	@Autowired ItemRepository itemRepo;
	@Autowired OrderRepository orderRepo;
	@Autowired TraitRepository traitRepo;
	@Autowired UserRepository userRepo;
	@Autowired ItemTraitRepository itemTraitRepo;
	
	@Test
	public void findByNameContaining() {
		var res = catRepo.findByNameContaining("олод");
		assertEquals(res.size(), 1);
		assertEquals(res.get(0).getName(), "Холодильники");
		assertEquals(catRepo.findByNameContaining("Холодильники").get(0).getName(), "Холодильники");
		assertEquals(catRepo.findByNameContaining("").size(), catRepo.findAll().size());
		assertTrue(catRepo.findByNameContaining("Гравицаппы").isEmpty());
	}

	@Test
	public void categoryCRUDTest() {
		var CATEGORY_NAME = "Тестовая категория";
		var CATEGORY_NAME2 = "blablabla";
		Boolean CATEGORY_ACTIVE = true;
		var cat = catRepo.saveAndFlush(new Category(CATEGORY_NAME, CATEGORY_ACTIVE));
		cat = catRepo.findById(cat.getId()).get();
		assertEquals(cat.getName(), CATEGORY_NAME);
		assertEquals(cat.isActive(), CATEGORY_ACTIVE);
		assertEquals(catRepo.findByNameContaining(CATEGORY_NAME).size(), 1);
		assertEquals(catRepo.findByNameContaining(CATEGORY_NAME).stream().findAny().get().getId(),
				cat.getId());
		cat.setActive(false);
		catRepo.saveAndFlush(cat);
		assertFalse(catRepo.findByNameContaining(CATEGORY_NAME).stream().findAny().get().isActive());
		cat.setActive(true);
		cat.setName(CATEGORY_NAME2);
		catRepo.saveAndFlush(cat);
		assertEquals(catRepo.findByNameContaining(CATEGORY_NAME2).size(), 1);
		assertEquals(catRepo.findByNameContaining(CATEGORY_NAME2).stream().findAny().get().getId(),
				cat.getId());
		cat.setName(CATEGORY_NAME);
		catRepo.saveAndFlush(cat);
		assertEquals(catRepo.findByNameContaining(CATEGORY_NAME).size(), 1);
		assertEquals(catRepo.findByNameContaining(CATEGORY_NAME).stream().findAny().get().getId(),
				cat.getId());
		catRepo.deleteAll(catRepo.findByNameContaining(CATEGORY_NAME));
		catRepo.flush();
		assertEquals(catRepo.findByNameContaining(CATEGORY_NAME).size(), 0);
	}
	
	
	
}
