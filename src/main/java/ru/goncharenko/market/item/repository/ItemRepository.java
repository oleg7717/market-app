package ru.goncharenko.market.item.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.goncharenko.market.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
	@Query("SELECT i " +
			"FROM Item i " +
			"LEFT JOIN FETCH i.itemInCart c " +
			"WHERE i.description like %:description% or i.title like %:description%")
	Page<Item> findByDescriptionOrTitleContainingIgnoreCase(String description, Pageable pageable);

	@Override
	@Query("SELECT i " +
			"FROM Item i " +
			"LEFT JOIN FETCH i.itemInCart c")
	Page<Item> findAll(@NonNull Pageable pageable);
}
