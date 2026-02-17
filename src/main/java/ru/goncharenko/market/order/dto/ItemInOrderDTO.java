package ru.goncharenko.market.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class ItemInOrderDTO {
	long id;
	String title;
	long price;
	int count;
}
