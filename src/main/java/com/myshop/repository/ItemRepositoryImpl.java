package com.myshop.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import com.myshop.model.Category;
import com.myshop.model.Item;
import com.myshop.model.ItemTrait;
import com.myshop.model.ItemTrait_;
import com.myshop.model.Item_;

public class ItemRepositoryImpl implements ItemRepositoryCustom {
	@PersistenceContext
	EntityManager em;
	@Autowired TraitRepository traitRepo;
	
	@Override
	@Transactional
	public Set<Item> findItemsByTerms(Category category, Set<Term> filter, Integer minPrice, Integer maxPrice) {
		var builder = em.getCriteriaBuilder();
		List<Set<Item>> result = new ArrayList<Set<Item>>();
		if (filter.isEmpty()) {
			return Set.of();
		}
		for (var term: filter) {
			CriteriaQuery<Item> cq = builder.createQuery(Item.class);
			Root<Item> item = cq.from(Item.class);
			Join<Item, ItemTrait> it = item.join(Item_.traits);
			Expression<Boolean> minPriceTerm = null, maxPriceTerm = null, priceTerm = null;
			if (minPrice != null) {
				minPriceTerm = builder.greaterThanOrEqualTo(item.get(Item_.price), minPrice);
			}
			if (maxPrice != null) {
				maxPriceTerm = builder.lessThanOrEqualTo(item.get(Item_.price), maxPrice);
			}
			if (minPrice != null && maxPrice != null) {
				priceTerm = builder.and(minPriceTerm, maxPriceTerm);
			} else if (minPrice != null) {
				priceTerm = minPriceTerm;
			} else {
				priceTerm = maxPriceTerm;
			}
			Expression<Boolean> pred = null;
			switch (term.type) {
			case ONE_OF:
				if (term.oneOfValues.isEmpty()) { 
					return Set.of();
				}
				pred = builder.and(builder.equal(it.get(ItemTrait_.trait), traitRepo.findById(term.traitID).get()),
							builder.and(builder.equal(item.get(Item_.category), category.getId()),
								term.oneOfValues.stream()
								.map(val -> (Expression<Boolean>) builder.equal(it.get(ItemTrait_.value), val))
								.reduce((e1, e2) -> builder.or(e1, e2)).get()));
				break;
			case BETWEEN:
				pred = builder.and(builder.equal(it.get(ItemTrait_.trait), traitRepo.findById(term.traitID).get()),
						builder.equal(item.get(Item_.category), category.getId()),
						builder.and(builder.lessThanOrEqualTo(it.get(ItemTrait_.valueInt), term.maxSegmentVal),
							builder.greaterThanOrEqualTo(it.get(ItemTrait_.valueInt), term.minSegmentVal)));
				break;
			}
			if (priceTerm != null) {
				if (pred == null) {
					pred = priceTerm;
				} else {
					pred = builder.and(pred, priceTerm); 
				}
			}
			if (pred != null) {
				cq.where(pred);
			}
			result.add(em.createQuery(cq).getResultStream().collect(Collectors.toSet()));
		}
		
		return result.stream()
				.reduce((s1, s2) -> { s1.retainAll(s2); return s1; })
				.get();
	}
}
