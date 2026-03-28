package ru.goncharenko.market.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.goncharenko.market.user.enums.UserStatus;

@Table(name = "users")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@Id
	private Long id;

	@Column("user_name")
	private String userName;

	private UserStatus status;

	@Column("password")
	private String password;

}
