package com.myshop;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import com.myshop.model.CategoryTrait;
import com.myshop.model.Trait;
import com.myshop.model.TypeEnum;
import com.myshop.repository.CategoryRepository;
import com.myshop.repository.CategoryTraitRepository;
import com.myshop.repository.TraitRepository;

@SpringBootTest
@TestPropertySource(value = "classpath:test.properties")
public class TraitRepositoryTests extends AbstractTestNGSpringContextTests {
	@Autowired TraitRepository traitRepo;
	@Autowired CategoryRepository catRepo;
	@Autowired CategoryTraitRepository catTraitRepo;
	
	@Test
	public void addNewTraitTest() {
		//empty name
		assertFalse(traitRepo.addOrUpdateTrait(null, null, 
				true, 
				TypeEnum.StringType, 0, 0, 
				Lists.newArrayList(), "")
				.getFirst().isPresent());
		assertFalse(traitRepo.addOrUpdateTrait(null, "", 
				true, 
				TypeEnum.StringType, 0, 0, 
				Lists.newArrayList(), "")
				.getFirst().isPresent());
		assertFalse(traitRepo.addOrUpdateTrait(null, "    ", 
				true, 
				TypeEnum.StringType, 0, 0, 
				Lists.newArrayList(), "")
				.getFirst().isPresent());
		// empty type
		assertFalse(traitRepo.addOrUpdateTrait(null, "name", 
				true, 
				null, 0, 0, 
				Lists.newArrayList(), "")
				.getFirst().isPresent());
		
		// enum null values
		assertFalse(traitRepo.addOrUpdateTrait(null, "name", 
				true, 
				TypeEnum.EnumType, 0, 0, 
				null, "")
				.getFirst().isPresent());
		// enum empty values
		assertFalse(traitRepo.addOrUpdateTrait(null, "name", 
				true, 
				TypeEnum.EnumType, 0, 0, 
				Lists.newArrayList(), "")
				.getFirst().isPresent());
		assertFalse(traitRepo.addOrUpdateTrait(null, "name", 
				true, 
				TypeEnum.EnumType, 0, 0, 
				Lists.newArrayList(""), "")
				.getFirst().isPresent());
		assertFalse(traitRepo.addOrUpdateTrait(null, "name", 
				true, 
				TypeEnum.EnumType, 0, 0, 
				Lists.newArrayList("hello", "world", "\t"), "")
				.getFirst().isPresent());
		// enum ok
		var en = traitRepo.addOrUpdateTrait(null, "name", 
				true, 
				TypeEnum.EnumType, 0, 0, 
				Lists.newArrayList("hello", "world"), "")
				.getFirst();
		assertTrue(en.isPresent());
		assertEquals(en.get().getName(), "name");
		var id = en.get().getId();
		traitRepo.deleteById(id);
		assertFalse(traitRepo.findById(id).isPresent());
		// int empy minval
		assertFalse(traitRepo.addOrUpdateTrait(null, "name", 
				true, 
				TypeEnum.IntType, null, 10, 
				null, null)
				.getFirst().isPresent());
		assertFalse(traitRepo.addOrUpdateTrait(null, "name", 
				true, 
				TypeEnum.IntType, 0, null, 
				null, null)
				.getFirst().isPresent());
		assertFalse(traitRepo.addOrUpdateTrait(null, "name", 
				true, 
				TypeEnum.IntType, null, null, 
				null, null)
				.getFirst().isPresent());
		// int min > max
		assertFalse(traitRepo.addOrUpdateTrait(null, "name", 
				true, 
				TypeEnum.IntType, 100, 10, 
				null, null)
				.getFirst().isPresent());
		// int ok
		var intTrait = traitRepo.addOrUpdateTrait(null, "name", 
				true, 
				TypeEnum.IntType, 0, 10, 
				null, null)
				.getFirst();
		assertTrue(intTrait.isPresent());
		assertTrue(intTrait.get().getName().equals("name"));
		id = intTrait.get().getId();
		traitRepo.deleteById(id);
		assertFalse(traitRepo.findById(id).isPresent());
		// string ok
		var stringTrait = traitRepo.addOrUpdateTrait(null, "name", 
				true, 
				TypeEnum.StringType, null, null,
				null, "sm")
				.getFirst();
		assertTrue(stringTrait.isPresent());
		assertTrue(stringTrait.get().getName().equals("name"));
		id = stringTrait.get().getId();
		traitRepo.deleteById(id);
		assertFalse(traitRepo.findById(id).isPresent());
	}
	
	@Test
	public void updateTraitTest() {
		var trait = traitRepo.addOrUpdateTrait(null, "test_trait", true, TypeEnum.StringType, null, null, null, null).getFirst().get();
		var id = trait.getId();
		
		//empty name
		assertFalse(traitRepo.addOrUpdateTrait(id, null, 
				true, 
				TypeEnum.StringType, 0, 0, 
				Lists.newArrayList(), "")
				.getFirst().isPresent());
		assertFalse(traitRepo.addOrUpdateTrait(id, "", 
				true, 
				TypeEnum.StringType, 0, 0, 
				Lists.newArrayList(), "")
				.getFirst().isPresent());
		assertFalse(traitRepo.addOrUpdateTrait(id, "    ", 
				true, 
				TypeEnum.StringType, 0, 0, 
				Lists.newArrayList(), "")
				.getFirst().isPresent());
		// empty type
		assertFalse(traitRepo.addOrUpdateTrait(id, "name", 
				true, 
				null, 0, 0, 
				Lists.newArrayList(), "")
				.getFirst().isPresent());
		
		// enum null values
		assertFalse(traitRepo.addOrUpdateTrait(id, "name", 
				true, 
				TypeEnum.EnumType, 0, 0, 
				null, "")
				.getFirst().isPresent());
		// enum empty values
		assertFalse(traitRepo.addOrUpdateTrait(id, "name", 
				true, 
				TypeEnum.EnumType, 0, 0, 
				Lists.newArrayList(), "")
				.getFirst().isPresent());
		assertFalse(traitRepo.addOrUpdateTrait(id, "name", 
				true, 
				TypeEnum.EnumType, 0, 0, 
				Lists.newArrayList(""), "")
				.getFirst().isPresent());
		assertFalse(traitRepo.addOrUpdateTrait(id, "name", 
				true, 
				TypeEnum.EnumType, 0, 0, 
				Lists.newArrayList("hello", "world", "\t"), "")
				.getFirst().isPresent());
		// enum ok
		var en = traitRepo.addOrUpdateTrait(id, "name", 
				true, 
				TypeEnum.EnumType, 0, 0, 
				Lists.newArrayList("hello", "world"), "")
				.getFirst();
		assertTrue(en.isPresent());
		assertEquals(en.get().getName(), "name");
		assertEquals(traitRepo.findById(id).get().getType(), TypeEnum.EnumType);
		assertEquals(traitRepo.findById(id).get().getValues(), Lists.newArrayList("hello", "world"));

		// int empy minval
		assertFalse(traitRepo.addOrUpdateTrait(id, "name", 
				true, 
				TypeEnum.IntType, null, 10, 
				null, null)
				.getFirst().isPresent());
		assertFalse(traitRepo.addOrUpdateTrait(id, "name", 
				true, 
				TypeEnum.IntType, 0, null, 
				null, null)
				.getFirst().isPresent());
		assertFalse(traitRepo.addOrUpdateTrait(id, "name", 
				true, 
				TypeEnum.IntType, null, null, 
				null, null)
				.getFirst().isPresent());
		// int min > max
		assertFalse(traitRepo.addOrUpdateTrait(id, "name", 
				true, 
				TypeEnum.IntType, 100, 10, 
				null, null)
				.getFirst().isPresent());
		// int ok
		var intTrait = traitRepo.addOrUpdateTrait(id, "name", 
				true, 
				TypeEnum.IntType, 0, 10, 
				null, null)
				.getFirst();
		assertTrue(intTrait.isPresent());
		assertTrue(intTrait.get().getName().equals("name"));
		assertEquals(traitRepo.findById(id).get().getType(), TypeEnum.IntType);
		assertEquals(traitRepo.findById(id).get().getMinValueOrNull(), (Integer)0);
		assertEquals(traitRepo.findById(id).get().getMaxValueOrNull(), (Integer)10);
		
		// string ok
		var stringTrait = traitRepo.addOrUpdateTrait(id, "name", 
				true, 
				TypeEnum.StringType, null, null,
				null, "sm")
				.getFirst();
		assertTrue(stringTrait.isPresent());
		assertTrue(stringTrait.get().getName().equals("name"));
		assertEquals(traitRepo.findById(id).get().getType(), TypeEnum.StringType);
		assertEquals(traitRepo.findById(id).get().getUnit(), "sm");
		
		id = stringTrait.get().getId();
		traitRepo.deleteById(id);
		assertFalse(traitRepo.findById(id).isPresent());
	}
	
	@Test
	public void traitCrudTest() {
		assertEquals(traitRepo.findAll().size(), 7);
		var myTrait = traitRepo.saveAndFlush(new Trait()
				.setName("some_trait").setType(TypeEnum.IntType).setMaxValue(10).setMinValue(9));
		var TRAIT_ID = myTrait.getId();
		assertEquals(myTrait.getId(), TRAIT_ID);
		myTrait = traitRepo.saveAndFlush(traitRepo.findById(TRAIT_ID).get().setType(TypeEnum.StringType));
		assertEquals(myTrait.getType(), TypeEnum.StringType);
		assertTrue(traitRepo.findById(TRAIT_ID).isPresent());
		traitRepo.deleteById(TRAIT_ID);
		assertFalse(traitRepo.findById(TRAIT_ID).isPresent());
	}

	@Test
	@Transactional
	public void categoryTraitCRUDTest() {
		var CATEGORY_ID = 2;
		var cat = catRepo.findById(CATEGORY_ID).get();
		assertEquals(cat.getName(), "Проигрыватели");
		assertEquals(catTraitRepo.findByCategoryId(CATEGORY_ID).size(), 3);
		catTraitRepo.deleteByCategoryIdAndTraitId(CATEGORY_ID, 1);
		assertEquals(catTraitRepo.findByCategoryId(CATEGORY_ID).size(), 3);
		catTraitRepo.deleteByCategoryIdAndTraitId(CATEGORY_ID, 3);
		var nt = catTraitRepo.findByCategoryId(CATEGORY_ID).stream().map(CategoryTrait::getTrait).map(Trait::getId).sorted().toArray();
		assertTrue(Arrays.deepEquals(nt, new Integer[] {6, 7}));
		catTraitRepo.addTraitToCategory(CATEGORY_ID, 3);
		assertTrue(Arrays.deepEquals(catTraitRepo.findByCategoryId(CATEGORY_ID).stream().map(CategoryTrait::getTrait).map(Trait::getId).sorted().toArray(),
				new Integer[] {3, 6, 7}));
	}
}
