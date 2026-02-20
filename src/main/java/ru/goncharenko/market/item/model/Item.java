package ru.goncharenko.market.item.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Item {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "title", length = 50, nullable = false, unique = true)
	private String title;

	@Column(name = "description")
	private String description;

	@Column(name = "img_path")
	private String imgPath;

	@Positive
	@Column(name = "price")
	private long price;

/*	@OneToOne(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
	private CartItem itemInCart;*/

	public Item(long id) {
		this.id = id;
	}
}
