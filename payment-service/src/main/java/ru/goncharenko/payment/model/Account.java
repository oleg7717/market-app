package ru.goncharenko.payment.model;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "accounts")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
	@Id
	private Long id;

	@Column("user_name")
	private String userName;

	@Column("balance")
	private String balance;
}
