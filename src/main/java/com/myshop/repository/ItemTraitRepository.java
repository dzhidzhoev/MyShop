package com.myshop.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myshop.model.ItemTrait;
import com.myshop.model.Trait;

public interface ItemTraitRepository extends JpaRepository<ItemTrait, ItemTrait.ID> {
	public Set<ItemTrait> findByItemId(int itemId);
	
	public default String getValue(ItemTrait itemTrait, TraitRepository traitRepo) {
		Optional<Trait> traitRes = traitRepo.findById(itemTrait.getId().getTraitID());
		if (traitRes.isPresent()) {
			Trait trait = traitRes.get();
			switch (trait.getType()) {
			case IntType:
				String unit = trait.getUnit();
				return String.valueOf(Math.min(trait.getMaxValueScalar(), Math.max(trait.getMinValueScalar(), itemTrait.getValueInt()))) 
						+ (unit == null || unit.isEmpty() ? "" : " " + unit);
			case StringType:
				return itemTrait.getValue() == null ? "" : itemTrait.getValue();
			case EnumType:
				if (trait.getValues() == null || itemTrait.getValue() == null) {
					return "";
				}
				if (trait.getValues().contains(itemTrait.getValue())) {
					return itemTrait.getValue();
				} else {
					return "";
				}
			default:
				break;
			}
		}
		return "";
	}
	
	public default Optional<ItemTrait> setValue(ItemTrait itemTrait, String value, TraitRepository traitRepo, ItemRepository itemRepo) {
		if (itemTrait == null) return null;
		if (value == null) {
			value = "";
		}
		Optional<Trait> traitRes = traitRepo.findById(itemTrait.getId().getTraitID());
		if (!traitRes.isPresent()) {
			return Optional.empty();
		}
		if (!itemRepo.findById(itemTrait.getId().getItemID()).isPresent()) {
			return Optional.empty();
		}
		var trait = traitRes.get();
		switch (trait.getType()) {
		case StringType:
			itemTrait.setValue(value);
			break;
		case IntType:
			try {
				int valueInt;
				try {
					valueInt = Integer.valueOf(value);
				} catch (NumberFormatException e) {
					return Optional.empty();
				}
				if (valueInt < trait.getMinValueScalar() 
						|| valueInt > trait.getMaxValueScalar()) {
					return Optional.empty();
				}
				itemTrait.setValueInt(valueInt);
			} catch (NumberFormatException e) {
				return Optional.empty();
			}
			break;
		case EnumType:
			if (trait.getValues() == null || !trait.getValues().contains(value)) {
				return Optional.empty();
			}
			itemTrait.setValue(value);
			break;
		default:
			return Optional.empty();
		}
		try {
			itemTrait.setItem(itemRepo.findById(itemTrait.getId().getItemID()).get());
			itemTrait.setTrait(traitRepo.findById(itemTrait.getId().getTraitID()).get());
			itemTrait = saveAndFlush(itemTrait);
		} catch (Exception e) {
			return Optional.empty();
		}
		return Optional.of(itemTrait);
	}
}
