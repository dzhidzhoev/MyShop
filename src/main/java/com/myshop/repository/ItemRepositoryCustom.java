package com.myshop.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.myshop.model.Category;
import com.myshop.model.Item;
import com.myshop.model.Trait;

public interface ItemRepositoryCustom {

	public static class Term {
		public enum TermType { ONE_OF, BETWEEN }
		public TermType type = TermType.ONE_OF;
		public Trait trait;
		public Set<String> oneOfValues = new HashSet<>();
		public int minSegmentVal = 0, maxSegmenVal = 0;
		
		public Term() {
		}
		public Term(TermType type, Trait trait, Set<String> oneOfValues, int minSegmentVal, int maxSegmenVal) {
			super();
			this.type = type;
			this.trait = trait;
			this.oneOfValues = oneOfValues;
			this.minSegmentVal = minSegmentVal;
			this.maxSegmenVal = maxSegmenVal;
		}
		
	}
	
	public Set<Item> findItemsByTerms(Category category, List<Term> filter);
}
