package ru.goncharenko.market.order.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.goncharenko.market.order.enums.OrderStatus;

@Table(name = "orders")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
	@Id
	private Long id;

	@Column("total_sum")
	private Double totalSum;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;
}
