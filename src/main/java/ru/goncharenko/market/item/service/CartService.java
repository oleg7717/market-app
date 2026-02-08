package ru.goncharenko.market.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.goncharenko.market.core.types.ActionEnum;
import ru.goncharenko.market.item.dto.CartDTO;
import ru.goncharenko.market.item.mapper.ItemMapper;
import ru.goncharenko.market.item.model.Item;
import ru.goncharenko.market.item.repository.ItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
	private final ItemRepository repository;
	private final ItemService itemService;
	private final ItemMapper mapper;

	public CartDTO getItemsInCart() {
		List<Item> itemsInCart = repository.itemsInCart();
		return mapper.onlyItemsInCart(itemsInCart);
	}

	@Transactional
	public CartDTO changeItemsCountFromCart(long id, ActionEnum action) {
		itemService.changeItemCountInCart(id, action);
		return getItemsInCart();
	}
}
