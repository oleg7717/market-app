package ru.goncharenko.market.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.goncharenko.market.core.response.ApiResponse;
import ru.goncharenko.market.core.response.Paging;

import java.util.List;

@Setter
@Getter
@Builder
public class ListItemsDTO implements ApiResponse {
	private List<ItemInCartDTO> items;
	private Paging paging;
}

