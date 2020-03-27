package com.myshop;

import static org.testng.Assert.assertFalse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import com.myshop.model.TypeEnum;
import com.myshop.repository.TraitRepository;

@SpringBootTest
@TestPropertySource(value = "classpath:test.properties")
public class TraitRepositoryTests extends AbstractTestNGSpringContextTests {
	@Autowired TraitRepository traitRepo;
	
	@Test
	public void addNewTraitTest() {
		//empty name
		assertFalse(traitRepo.addNewTrait(null, 
				true, 
				TypeEnum.StringType, 0, 0, 
				Lists.newArrayList(), "")
				.getFirst().isPresent());
		assertFalse(traitRepo.addNewTrait("", 
				true, 
				TypeEnum.StringType, 0, 0, 
				Lists.newArrayList(), "")
				.getFirst().isPresent());
		assertFalse(traitRepo.addNewTrait("    ", 
				true, 
				TypeEnum.StringType, 0, 0, 
				Lists.newArrayList(), "")
				.getFirst().isPresent());
		// empty type
		assertFalse(traitRepo.addNewTrait("name", 
				true, 
				null, 0, 0, 
				Lists.newArrayList(), "")
				.getFirst().isPresent());
	}
}
