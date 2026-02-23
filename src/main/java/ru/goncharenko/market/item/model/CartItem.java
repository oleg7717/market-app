package ru.goncharenko.market.item.model;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "cart_item")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
	@Id
	private Long id;

	@Column("cart_id")
	private Long cartId;

	@Column("item_id")
	private Long itemId;

	@Column("count")
	private Integer count;
}
