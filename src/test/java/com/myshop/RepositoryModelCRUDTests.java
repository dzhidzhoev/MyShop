package com.myshop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

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
public class RepositoryModelCRUDTests extends AbstractTestNGSpringContextTests {
	
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
		assertThat(catRepo.findByNameContaining("Гравицаппы").isEmpty());
	}

	@Test
	public void CategoryCRUDTest() {
		var cat = catRepo.saveAndFlush(new Category("Тестовая категория", true));
	}
	
	
	
}
