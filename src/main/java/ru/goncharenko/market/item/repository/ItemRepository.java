package ru.goncharenko.market.item.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.goncharenko.market.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends ReactiveCrudRepository<Item, Long> {
//	Flux<Item> findByDescriptionOrTitleContainingIgnoreCase(String description, Pageable pageable);

//	Flux<Item> findAll(@NonNull Pageable pageable);

	Flux<Item> findAllByIdIn(Collection<Long> ids);

	<T> Flux<T> findItemsByIdIn(Collection<Long> ids);
}
