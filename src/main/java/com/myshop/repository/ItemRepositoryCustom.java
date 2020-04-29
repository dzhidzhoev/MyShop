package com.myshop.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.myshop.model.Category;
import com.myshop.model.Item;
import com.myshop.model.Trait;
import com.myshop.repository.ItemRepositoryCustom.Term;

public interface ItemRepositoryCustom {

	public static class Term {
		public enum TermType { ONE_OF, BETWEEN }
		public TermType type = TermType.ONE_OF;
		public int traitID;
		public Set<String> oneOfValues = new HashSet<>();
		public int minSegmentVal = 0, maxSegmentVal = 0;
		
		public Term() {
		}
		public Term(TermType type, Trait trait, Set<String> oneOfValues, int minSegmentVal, int maxSegmentVal) {
			super();
			this.type = type;
			this.traitID = trait.getId();
			this.oneOfValues = oneOfValues;
			this.minSegmentVal = minSegmentVal;
			this.maxSegmentVal = maxSegmentVal;
		}
		public Set<String> getOneOfValues() {
			return oneOfValues;
		}
		public void setOneOfValues(Set<String> oneOfValues) {
			this.oneOfValues = oneOfValues;
		}
		public int getMinSegmentVal() {
			return minSegmentVal;
		}
		public void setMinSegmentVal(int minSegmentVal) {
			this.minSegmentVal = minSegmentVal;
		}
		public int getMaxSegmentVal() {
			return maxSegmentVal;
		}
		public void setMaxSegmentVal(int maxSegmentVal) {
			this.maxSegmentVal = maxSegmentVal;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + traitID;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Term other = (Term) obj;
			if (traitID != other.traitID)
				return false;
			return true;
		}
	}
	
	public Set<Item> findItemsByTerms(Category category, Set<Term> filter, Integer minPrice, Integer maxPrice);
}
