package ru.goncharenko.market.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.goncharenko.market.user.enums.UserStatus;
import ru.goncharenko.market.user.repository.UserRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements ReactiveUserDetailsService {
	private final UserRepository userRepository;

	@Override
	public Mono<UserDetails> findByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUserName(username)
				.switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found: " + username)))
				.flatMap(user -> Mono.just(
						org.springframework.security.core.userdetails.User.builder()
								.username(user.getUserName())
								.password(user.getPassword())
								.disabled(user.getStatus() != UserStatus.ACTIVE)
								.authorities(Collections.emptyList())
								.build()
				));
	}
}
