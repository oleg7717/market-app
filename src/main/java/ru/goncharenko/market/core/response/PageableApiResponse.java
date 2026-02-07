package ru.goncharenko.market.core.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.goncharenko.market.core.types.SortEnum;
import ru.goncharenko.market.item.dto.ItemInCartDTO;

import java.util.List;

@Setter
@Getter
@Builder
public class PageableApiResponse implements ApiResponse {
	private List<List<ItemInCartDTO>> items;
	private String search;
	private SortEnum sort;
	private Paging paging;
}

