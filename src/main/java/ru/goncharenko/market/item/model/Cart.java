package ru.goncharenko.market.item.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "carts")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
	@Id
	private Long id;

 	@Column("user_name")
	private String userName;
}
