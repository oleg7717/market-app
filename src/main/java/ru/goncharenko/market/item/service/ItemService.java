package ru.goncharenko.market.item.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.goncharenko.market.item.dto.ItemInCartDTO;
import ru.goncharenko.market.core.exception.ResourceNotFoundException;
import ru.goncharenko.market.core.types.SortEnum;
import ru.goncharenko.market.item.mapper.CartItemMapper;
import ru.goncharenko.market.item.model.Cart;
import ru.goncharenko.market.item.model.Item;
import ru.goncharenko.market.item.repository.ItemRepository;
import ru.goncharenko.market.item.dto.ListItemsDTO;
import ru.goncharenko.market.core.response.Paging;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
	private final ItemRepository itemRepository;
	private final CartService cartService;
	private final CartItemMapper mapper;
	private final HttpServletRequest request;

	public ListItemsDTO getItems(String search, SortEnum sort, int pageNumber, int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(sort.getFieldName()));
		Page<Item> page;
		if (search == null || search.isEmpty()) {
			page = itemRepository.findAll(pageable);
		} else {
			page = itemRepository.findByDescriptionOrTitleContainingIgnoreCase(search, pageable);
		}

		Cart cart = cartService.getOrCreateCart();

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
			for (ItemInCartDTO item : itemInCartDTO) {
				mapper.setCountToItem(cart, item);
			}
			mapper.setImgUrl(itemInCartDTO, request);

			itemsInCart.add(itemInCartDTO);
		}

		return ListItemsDTO.builder()
				.items(itemsInCart)
				.paging(new Paging(pageNumber, pageSize, pageNumber > 1, page.hasNext()))
				.build();
	}

	public ItemInCartDTO findById(long id) {
		Item item = itemRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format("Item with id: %s not found.", id)));

		ItemInCartDTO itemDTO = mapper.toItemInCart(item);
		Cart cart = cartService.getOrCreateCart();
		mapper.setCountToItem(cart, itemDTO);
		mapper.setImgUrl(itemDTO, request);

		return itemDTO;
	}

	public static String getBaseUrl(HttpServletRequest request) {
		return "/" + request.getServerName() + ":" + request.getServerPort();
	}
}
