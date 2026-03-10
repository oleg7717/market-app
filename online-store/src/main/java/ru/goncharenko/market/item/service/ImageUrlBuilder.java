package ru.goncharenko.market.item.service;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import ru.goncharenko.market.item.dto.CartDTO;
import ru.goncharenko.market.item.dto.ItemInCartDTO;
import ru.goncharenko.market.item.dto.ListItemsDTO;

import java.util.List;

@Service
public class ImageUrlBuilder {
	public String buildImageUrl(String imagePath, ServerWebExchange exchange) {
		String host = exchange.getRequest().getURI().getHost();
		int port = exchange.getRequest().getURI().getPort();

		String serverUri = "/" + host;
		if (port != -1 && port != 80 && port != 443) {
			serverUri = serverUri + ":" + port;
		}

		return serverUri + imagePath;
	}

	public ListItemsDTO enrichWithImageUrls(ListItemsDTO listItemsDTO, ServerWebExchange exchange) {
		List<List<ItemInCartDTO>> enrichedGroups = listItemsDTO.getItems().stream()
				.map(group -> group.stream()
						.map(item -> {
							if (item.id() != -1) {
								return newItemInCartDTO(item, exchange);
							}
							return item;
						})
						.toList())
				.toList();

		return ListItemsDTO.builder()
				.items(enrichedGroups)
				.paging(listItemsDTO.getPaging())
				.build();
	}

	public CartDTO enrichCartDTOWithImageUrls(CartDTO cartDTO, ServerWebExchange exchange) {
		List<ItemInCartDTO> enrichedItems = cartDTO.getItems().stream()
				.map(item -> newItemInCartDTO(item, exchange))
				.toList();

		return new CartDTO(enrichedItems, cartDTO.getTotal());
	}

	private ItemInCartDTO newItemInCartDTO(ItemInCartDTO item, ServerWebExchange exchange) {
		return new ItemInCartDTO(
				item.id(),
				item.title(),
				item.description(),
				buildImageUrl(item.imgPath(), exchange),
				item.price(),
				item.count());
	}
}