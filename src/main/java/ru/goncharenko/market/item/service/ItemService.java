package ru.goncharenko.market.item.service;

import jakarta.servlet.http.HttpServletRequest;
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
import ru.goncharenko.market.item.mapper.CartItemMapper;
import ru.goncharenko.market.item.model.Cart;
import ru.goncharenko.market.item.model.CartItem;
import ru.goncharenko.market.item.model.Item;
import ru.goncharenko.market.item.repository.CartItemRepository;
import ru.goncharenko.market.item.repository.CartRepository;
import ru.goncharenko.market.item.repository.ItemRepository;
import ru.goncharenko.market.item.dto.ListItemsDTO;
import ru.goncharenko.market.core.response.Paging;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
	private final ItemRepository itemRepository;
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final CartItemMapper mapper;
	private final HttpServletRequest request;
	private final String userName = "anonymous";

	public ListItemsDTO getItems(String search, SortEnum sort, int pageNumber, int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(sort.getFieldName()));
		Page<Item> page;
		if (search == null || search.isEmpty()) {
			page = itemRepository.findAll(pageable);
		} else {
			page = itemRepository.findByDescriptionOrTitleContainingIgnoreCase(search, pageable);
		}

		Cart cart = cartRepository.findAllByUserName(userName);

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

			List<ItemInCartDTO> itemInCartDTO = mapper.toItemListInCart(group);
			itemInCartDTO.forEach(item -> {
				mapper.setCountToItem(cart, item);
			});
			mapper.setImgUrl(itemInCartDTO, request);

			itemsInCart.add(itemInCartDTO);
		}

		return ListItemsDTO.builder()
				.items(itemsInCart)
				.paging(new Paging(pageNumber, pageSize, pageNumber > 1, page.hasNext()))
				.build();
	}

	@Transactional
	public void changeItemCountInCart(long id, ActionEnum action) {
		Optional<CartItem> itemInCart = cartItemRepository.findItemInCartById(id, userName);
		switch (action) {
			case MINUS -> decreaseItemCount(itemInCart.get());
			case PLUS -> increaseItemCount(itemInCart, id);
		}
	}

	public ItemInCartDTO findById(long id) {
		Item item = itemRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format("Item with id: %s not found.", id)));

		ItemInCartDTO itemDTO = mapper.toItemInCart(item);
		String userName = "anonymous";
		Cart cart = cartRepository.findAllByUserName(userName);
		mapper.setCountToItem(cart, itemDTO);
		mapper.setImgUrl(itemDTO, request);

		return itemDTO;
	}

	private void decreaseItemCount(CartItem itemInCart) {
		if (itemInCart.getCount() == 1) {
			itemInCart.setCart(null);
		} else {
			itemInCart.removeOne();
		}
	}

	private void increaseItemCount(Optional<CartItem> itemInCart, Long id) {
		if (!itemInCart.isPresent()) {
			Optional<Item> item = itemRepository.findById(id);
			CartItem newItemInCart = new CartItem();
			newItemInCart.setItem(item.get());
			newItemInCart.setCount(1);
			newItemInCart.setCart(cartRepository.findAllByUserName(userName));
			cartItemRepository.save(newItemInCart);
		} else {
			itemInCart.get().addOne();
		}
	}

	public static String getBaseUrl(HttpServletRequest request) {
		return "/" + request.getServerName() + ":" + request.getServerPort();
	}
}
