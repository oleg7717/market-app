package ru.goncharenko.market.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemInOrderDTO {
	long id;
	String title;
	long price;
	int count;
}
