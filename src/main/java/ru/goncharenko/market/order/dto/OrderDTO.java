package ru.goncharenko.market.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
	private Long id;
	private List<ItemInOrderDTO> items;
	private Long totalSum;
	private String newOrder;

	public Long id() {
		return id;
	}

	public List<ItemInOrderDTO> items() {
		return items;
	}

	public Long totalSum() {
		return totalSum;
	}
}
