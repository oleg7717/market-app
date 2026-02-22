package ru.goncharenko.market.order.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "order_item")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
	@Id
	private Long id;

	@Column("item_id")
	private Long itemId;

	@Column("order_id")
	private Long orderId;

	@Column("count")
	private Integer count;
}
