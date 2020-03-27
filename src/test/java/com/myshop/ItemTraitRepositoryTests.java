package com.myshop;

import static org.testng.Assert.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.myshop.model.ItemTrait;
import com.myshop.model.ItemTrait.ID;
import com.myshop.repository.CategoryRepository;
import com.myshop.repository.ItemRepository;
import com.myshop.repository.ItemTraitRepository;
import com.myshop.repository.OrderRepository;
import com.myshop.repository.TraitRepository;
import com.myshop.repository.UserRepository;

@SpringBootTest
@TestPropertySource(value = "classpath:test.properties")
public class ItemTraitRepositoryTests extends AbstractTestNGSpringContextTests {
	
	@Autowired CategoryRepository catRepo;
	@Autowired ItemRepository itemRepo;
	@Autowired OrderRepository orderRepo;
	@Autowired TraitRepository traitRepo;
	@Autowired UserRepository userRepo;
	@Autowired ItemTraitRepository itemTraitRepo;
	
	@Test
	public void itemTraitGetTest() {
		// not present
		var unknownIT = new ItemTrait().setId(new ID(100, 100));
		assertEquals(itemTraitRepo.getValue(unknownIT, traitRepo), "");
		// integer out of bound
		var outIT = new ItemTrait().setId(new ID(1, 3)).setValueInt(5);
		assertEquals(itemTraitRepo.getValue(outIT, traitRepo), "10");
		outIT.setValueInt(5999999);
		assertEquals(itemTraitRepo.getValue(outIT, traitRepo), "250");
		// integer ok
		assertEquals(itemTraitRepo.getValue(itemTraitRepo.findById(new ID(1, 3)).get(), traitRepo), "50");
		// string null
		var nullIT = new ItemTrait().setId(new ID(3, 1)).setValue(null);
		assertEquals(itemTraitRepo.getValue(nullIT, traitRepo), "");
		// string ok
		assertEquals(itemTraitRepo.getValue(new ItemTrait()
				.setId(new ID(3, 1)).setValue("helo"), traitRepo), 
				"helo");
		// enum null
		var enumId = new ID(3, 6);
		assertEquals(itemTraitRepo.getValue(new ItemTrait().setId(enumId).setValue(null), traitRepo), "");
		// enum empty
		assertEquals(itemTraitRepo.getValue(new ItemTrait().setId(enumId).setValue("unknown value"), traitRepo), "");
		assertEquals(itemTraitRepo.getValue(new ItemTrait().setId(enumId).setValue(""), traitRepo), "");
		// enum ok
		assertEquals(itemTraitRepo.getValue(itemTraitRepo.findById(enumId).get(), traitRepo), "Bluray");	
	}
	
	@Test
	public void itemTraitSetTest() {
		assertEquals(itemTraitRepo.findAll().size(), 4);
		
		
		
		assertEquals(itemTraitRepo.findAll().size(), 4);
	}
}
