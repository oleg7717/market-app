package ru.goncharenko.market.order.model;

import jakarta.persistence.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "orders")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
	@Id
	private Long id;

	private Long totalSum;

	private String status;
}
