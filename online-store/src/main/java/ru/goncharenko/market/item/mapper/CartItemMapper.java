package ru.goncharenko.market.item.mapper;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import ru.goncharenko.market.item.dto.CartContext;
import ru.goncharenko.market.item.dto.CartDTO;
import ru.goncharenko.market.item.dto.ItemInCartDTO;
import ru.goncharenko.market.item.model.CartItem;
import ru.goncharenko.market.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CartItemMapper {
	public ItemInCartDTO toItemInCart(Item item) {
		if (item == null) {
			return null;
		}

		return ItemInCartDTO.builder()
				.id(item.getId())
				.title(item.getTitle())
				.description(item.getDescription())
				.imgPath(item.getImgPath())
				.price(item.getPrice())
				.build();
	}

	public CartDTO buildCartDTO(CartContext context, ServerWebExchange exchange) {
		List<CartItem> items = context.getItems();
		Map<Long, Item> itemMap = context.getItemMap();

		List<ItemInCartDTO> itemDTOs = buildItemDTOs(items, itemMap, exchange);
		Double total = calculateTotal(itemDTOs);

		return new CartDTO(
				itemDTOs,
				total
		);
	}

	private Double calculateTotal(List<ItemInCartDTO> items) {
		return (double) items.stream()
				.mapToLong(itemICart -> (long) (itemICart.price() * itemICart.count()))
				.sum();
	}

	public List<ItemInCartDTO> buildItemDTOs(List<CartItem> items, Map<Long, Item> itemMap, ServerWebExchange exchange) {
		return items.stream()
				.map(item -> createCartItemDTO(item, itemMap.get(item.getItemId()), exchange))
				.collect(Collectors.toList());
	}

	private ItemInCartDTO createCartItemDTO(CartItem cartItem, Item item, ServerWebExchange exchange) {
		return new ItemInCartDTO(
				item.getId(),
				item.getTitle(),
				item.getDescription(),
				getImgUrl(exchange) + item.getImgPath(),
				item.getPrice(),
				cartItem.getCount()
		);
	}

	public static String getImgUrl(ServerWebExchange exchange) {
		String host = exchange.getRequest().getURI().getHost();
		int port = exchange.getRequest().getURI().getPort();

		String serverUri = "/" + host;
		if (port != -1 && port != 80 && port != 443) {
			serverUri = serverUri + ":" + port;
		}

		return serverUri;
	}
}
