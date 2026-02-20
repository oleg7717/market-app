package ru.goncharenko.market.order.dto;

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

	public String title() {
		return title;
	}

	public long id() {
		return id;
	}

	public long price() {
		return price;
	}

	public int count() {
		return count;
	}
}
