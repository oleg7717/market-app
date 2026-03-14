package ru.goncharenko.market.core;

import java.util.Objects;
import java.util.function.Function;

public class ObjectsUtil {
	public static <T, R> Function<T, R> requireNonNullResult(Function<? super T, ? extends R> function) {
		return t -> Objects.requireNonNull(function.apply(t));
	}
}
