package com.myshop.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import com.myshop.model.Category;
import com.myshop.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
	public Set<Item> findItemsByCategoryId(int categoryId);
	public List<Item> findItemsByCategoryId(int categoryId, Pageable page);
	
	public default Pair<Optional<Item>, String> addOrUpdateItem(
			Integer id, Category category, String name,
			int price, Integer count, boolean active, 
			String description) {
		if (category == null) {
			return Pair.of(Optional.empty(), "no category");
		}
		if (name == null || name.trim().isEmpty()) {
			return Pair.of(Optional.empty(), "no name");
		}
		name = name.trim();
		var item = new Item()
				.setCategory(category)
				.setName(name)
				.setPrice(price)
				.setCount(count)
				.setActive(active)
				.setDescription(description);
		if (id != null) {
			item.setId(id);
		}
		try {
			item = saveAndFlush(item);
		} catch (Exception e) {
			return Pair.of(Optional.empty(), "unknown exception " + e.toString());
		}
		return Pair.of(Optional.of(item), "ok");
	}
}
