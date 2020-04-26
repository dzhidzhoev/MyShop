package com.myshop;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Optional;

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
		assertEquals(itemTraitRepo.getValue(outIT, traitRepo), "10 см");
		outIT.setValueInt(5999999);
		assertEquals(itemTraitRepo.getValue(outIT, traitRepo), "250 см");
		// integer ok
		assertEquals(itemTraitRepo.getValue(itemTraitRepo.findById(new ID(1, 3)).get(), traitRepo), "50 см");
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
		assertEquals(itemTraitRepo.findAll().size(), 8);
		
		// not present
		var unknownIT = new ItemTrait().setId(new ID(1, 100));
		assertEquals(itemTraitRepo.setValue(unknownIT, "some", traitRepo, itemRepo), Optional.empty());
		unknownIT = new ItemTrait().setId(new ID(100, 1));
		assertEquals(itemTraitRepo.setValue(unknownIT, "some", traitRepo, itemRepo), Optional.empty());
		// integer out of bound
		var intId = new ID(1, 3);
		var outIT = new ItemTrait().setId(intId);
		assertEquals(itemTraitRepo.setValue(outIT, "5", traitRepo, itemRepo), Optional.empty());
		assertEquals(itemTraitRepo.setValue(outIT, "59999", traitRepo, itemRepo), Optional.empty());
		// integer ok 
		assertTrue(itemTraitRepo.setValue(outIT, "60", traitRepo, itemRepo).isPresent());
		assertEquals(itemTraitRepo.findById(intId).get().getValueInt(), 60);
		assertTrue(itemTraitRepo.setValue(outIT, "50", traitRepo, itemRepo).isPresent());
		assertEquals(itemTraitRepo.findById(intId).get().getValueInt(), 50);
		// string null
		ID stringId = new ID(3, 2);
		ItemTrait stringTrait = new ItemTrait().setId(stringId);
		// string ok
		assertTrue(itemTraitRepo.setValue(stringTrait, "helo", traitRepo, itemRepo).isPresent());
		assertEquals(itemTraitRepo.findById(stringId).get().getValue(), 
				"helo");
		assertEquals(itemTraitRepo.setValue(stringTrait, null, traitRepo, itemRepo).get().getValue(), "");
		itemTraitRepo.deleteById(stringId);
		assertFalse(itemTraitRepo.findById(stringId).isPresent());

		// enum null
		var enumId = new ID(3, 6);
		assertEquals(itemTraitRepo.setValue(new ItemTrait().setId(enumId), null, traitRepo, itemRepo), Optional.empty());
		// enum empty
		assertEquals(itemTraitRepo.setValue(new ItemTrait().setId(enumId), "unknown value", traitRepo, itemRepo), Optional.empty());
		assertEquals(itemTraitRepo.setValue(new ItemTrait().setId(enumId), "", traitRepo, itemRepo), Optional.empty());
		// enum ok
		assertEquals(itemTraitRepo.setValue(itemTraitRepo.findById(enumId).get(), "DVD", traitRepo, itemRepo).isPresent(), true);
		assertEquals(itemTraitRepo.findById(enumId).get().getValue(), "DVD");
		assertEquals(itemTraitRepo.setValue(itemTraitRepo.findById(enumId).get(), "Bluray", traitRepo, itemRepo).isPresent(), true);
		assertEquals(itemTraitRepo.findById(enumId).get().getValue(), "Bluray");
		
		assertEquals(itemTraitRepo.findAll().size(), 8);
	}
}
