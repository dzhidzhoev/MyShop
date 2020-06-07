package com.myshop.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import com.myshop.model.Category;
import com.myshop.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer>, ItemRepositoryCustom {
	@EntityGraph(attributePaths = {"image"})
	public Optional<Item> findWithImageById(int id);
	public List<Item> findByPriceLessThanEqual(int price);
	public List<Item> findByPriceGreaterThanEqual(int price);
	public List<Item> findByPriceBetween(int minPrice, int maxPrice);
	public Set<Item> findItemsByCategoryId(int categoryId);
	public List<Item> findItemsByCategoryIdAndPriceLessThanEqual(int categoryId, int price);
	public List<Item> findItemsByCategoryIdAndPriceGreaterThanEqual(int categoryId, int price);
	public List<Item> findItemsByCategoryIdAndPriceBetween(int categoryId, int minPrice, int maxPrice);
	public List<Item> findItemsByCategoryId(int categoryId, Pageable page);
	
	public default byte[] getImageById(int id) {
		var item = findById(id);
		return item.isPresent() ? item.get().getImage() : null;
	}
	
	public default Pair<Optional<Item>, String> addOrUpdateItem(
			Integer id, Category category, String name,
			int price, Integer count, boolean active, 
			String description, byte[] image) {
		if (category == null) {
			return Pair.of(Optional.empty(), "no category");
		}
		if (name == null || name.trim().isEmpty()) {
			return Pair.of(Optional.empty(), "no name");
		}
		name = name.trim();
		var item = id != null ? findById(id).get() : new Item();
		item.setCategory(category)
			.setName(name)
			.setPrice(price)
			.setCount(count)
			.setActive(active)
			.setDescription(description)
			.setImage(image);
		item = saveAndFlush(item);
		return Pair.of(Optional.of(item), "ok");
	}
}
