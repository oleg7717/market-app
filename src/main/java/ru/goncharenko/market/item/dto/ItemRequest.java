package ru.goncharenko.market.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.goncharenko.market.core.types.ActionEnum;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
	private Long id;
	private ActionEnum action;
}
