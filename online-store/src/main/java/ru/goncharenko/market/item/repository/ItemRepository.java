package ru.goncharenko.market.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends ReactiveCrudRepository<Item, Long> {
	Mono<Item> findById(Long id);

	Flux<Item> findAllByIdIn(Collection<Long> ids);

	Flux<Item> findAllBy(Pageable pageable);

	Flux<Item> findByDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCase(String description, String title, Pageable pageable);

	Mono<Long> count();

	Mono<Long> countByDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCase(String description, String title);
}
