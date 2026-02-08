package ru.goncharenko.market.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.goncharenko.market.core.types.ActionEnum;
import ru.goncharenko.market.item.dto.ItemInCartDTO;
import ru.goncharenko.market.core.exception.ResourceNotFoundException;
import ru.goncharenko.market.core.types.SortEnum;
import ru.goncharenko.market.item.mapper.ItemMapper;
import ru.goncharenko.market.item.model.Cart;
import ru.goncharenko.market.item.model.Item;
import ru.goncharenko.market.item.repository.ItemRepository;
import ru.goncharenko.market.core.response.PageableApiResponse;
import ru.goncharenko.market.core.response.Paging;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
	private final ItemRepository repository;
	private final ItemMapper mapper;

	public PageableApiResponse getItems(String search, SortEnum sort, int pageNumber, int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(sort.getFieldName()));
		Page<Item> page;
		if (search == null || search.isEmpty()) {
			page = repository.findAll(pageable);
		} else {
			page = repository.findByDescriptionOrTitleContainingIgnoreCase(search, pageable);
		}

		int totalItems = page.getNumberOfElements();
		int groupSize = Math.min(pageSize, 3);
		List<Item> group;
		List<List<ItemInCartDTO>> itemsInCart = new ArrayList<>();
		for (int i = 0; i < totalItems; i += groupSize) {
			int end = Math.min(i + groupSize, totalItems);
			group = new ArrayList<>(page.getContent().subList(i, end));

			while (group.size() < groupSize) {
				group.add(new Item(-1));
			}

			itemsInCart.add(mapper.itemListInCart(group));
		}

		return PageableApiResponse.builder()
				.items(itemsInCart)
				.search(search)
				.sort(sort)
				.paging(new Paging(pageNumber, pageSize, pageNumber > 1, page.hasNext()))
				.build();
	}

	@Transactional
	public ItemInCartDTO changeCountAndReturnItemInCart(long id, ActionEnum action) {
		return mapper.itemInCart(changeItemCountInCart(id, action));
	}

	@Transactional
	public Item changeItemCountInCart(long id, ActionEnum action) {
		Item item = getItemById(id);
		switch (action) {
			case MINUS -> decreaseItemCount(item);
			case PLUS -> increaseItemCount(item);
		}

		return item;
	}

	public ItemInCartDTO findById(long id) {
		return mapper.itemInCart(getItemById(id));
	}

	private Item getItemById(long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format("Item with id: %s not found.", id)));
	}

	private void decreaseItemCount(Item item) {
		Cart cart = item.getItemInCart();
		if (cart == null) return;

		if (cart.getCount() == 1) {
			item.setItemInCart(null);
		} else {
			cart.removeOne();
		}
	}

	private void increaseItemCount(Item item) {
		Cart cart = item.getItemInCart();
		if (cart == null) {
			cart = new Cart();
			cart.setItem(item);
			cart.setCount(1);
			item.setItemInCart(cart);
		} else {
			cart.addOne();
		}
	}
}
