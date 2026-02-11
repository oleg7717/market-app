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

	public long id() {
		return id;
	}

	public String title() {
		return title;
	}

	public String description() {
		return description;
	}

	public String imgPath() {
		return imgPath;
	}

	public long price() {
		return price;
	}

	public int count() {
		return count;
	}
}
