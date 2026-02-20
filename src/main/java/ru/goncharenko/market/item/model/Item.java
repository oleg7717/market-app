package ru.goncharenko.market.item.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "items")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Item {
	@Id
	private long id;

	@Column("title")
	private String title;

	@Column("description")
	private String description;

	@Column("img_path")
	private String imgPath;

	@Column("price")
	private long price;

	public Item(long id) {
		this.id = id;
	}
}
