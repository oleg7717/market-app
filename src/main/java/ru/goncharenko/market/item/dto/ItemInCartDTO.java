package ru.goncharenko.market.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemInCartDTO {
	long id;
	String title;
	String description;
	String imgPath;
	long price;
	int count;
}
