package com.myshop.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import com.myshop.model.Category;
import com.myshop.model.Item;
import com.myshop.model.ItemTrait;
import com.myshop.model.ItemTrait_;
import com.myshop.model.Item_;

public class ItemRepositoryImpl implements ItemRepositoryCustom {
	@PersistenceContext
	EntityManager em;
	
	@Override
	@Transactional
	public Set<Item> findItemsByTerms(Category category, List<Term> filter) {
		var builder = em.getCriteriaBuilder();
		List<Set<Item>> result = new ArrayList<Set<Item>>();
		if (filter.isEmpty()) {
			return Set.of();
		}
		for (var term: filter) {
			CriteriaQuery<Item> cq = builder.createQuery(Item.class);
			Root<Item> item = cq.from(Item.class);
			Join<Item, ItemTrait> it = item.join(Item_.traits);
			
			switch (term.type) {
			case ONE_OF:
				if (term.oneOfValues.isEmpty()) { 
					return Set.of();
				}
				cq.where(builder.and(builder.equal(it.get(ItemTrait_.trait), term.trait),
							builder.and(builder.equal(item.get(Item_.category), category.getId()),
								term.oneOfValues.stream()
								.map(val -> builder.equal(it.get(ItemTrait_.value), val))
								.reduce((e1, e2) -> builder.or(e1, e2)).get())));
				break;
			case BETWEEN:
				cq.where(builder.and(builder.equal(it.get(ItemTrait_.trait), term.trait),
						builder.and(builder.lessThanOrEqualTo(it.get(ItemTrait_.valueInt), term.maxSegmenVal),
							builder.greaterThanOrEqualTo(it.get(ItemTrait_.valueInt), term.minSegmentVal))));
				break;
			}
			result.add(em.createQuery(cq).getResultStream().collect(Collectors.toSet()));
		}
		
		return result.stream()
				.reduce((s1, s2) -> { s1.retainAll(s2); return s1; })
				.get();
	}
}
