package com.myshop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import com.myshop.model.Trait;
import com.myshop.model.TypeEnum;

@Repository
public interface TraitRepository extends JpaRepository<Trait, Integer> {
	
	public default Pair<Optional<Trait>, String> addOrUpdateTrait(Integer id, String name, boolean isSearchable, TypeEnum type, Integer minValue, Integer maxValue, List<String> values, String unit) {
		if (name == null || name.trim().isEmpty()) {
			return Pair.of(Optional.empty(), "empty name");
		}
		name = name.trim();
		if (type == null) {
			return Pair.of(Optional.empty(), "type not chosen");
		}
		switch (type) {
		case EnumType:
			if (values == null || values.size() == 0 
				|| values.stream()
					.map(String::trim)
					.map(String::isEmpty)
					.reduce(false, Boolean::logicalOr)) {
				return Pair.of(Optional.empty(), "values are not set or contain empty value");
			}
			values.forEach(String::trim);
			break;
		case IntType:
			if (minValue == null || maxValue == null || minValue > maxValue) {
				return Pair.of(Optional.empty(), "minvalue and maxvalue are not set or are not ordered");
			}
			break;
		case StringType:
			break;
		default:
			break;
		}
		var trait = new Trait()
				.setName(name)
				.setSearchableChained(isSearchable)
				.setType(type)
				.setMinValue(minValue)
				.setMaxValue(maxValue)
				.setValues(values)
				.setUnit(unit);
		if (id != null) {
			trait.setId(id);
		}
		trait = saveAndFlush(trait);
		return Pair.of(Optional.of(trait), "ok");
	}
}
