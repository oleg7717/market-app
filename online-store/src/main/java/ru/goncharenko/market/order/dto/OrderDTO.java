package ru.goncharenko.market.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Setter
@Getter
@Accessors(fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
	private Long id;
	private List<ItemInOrderDTO> items;
	private Double totalSum;
}
