package ca.magex.crm.api.transform;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface RequestHandler<A, B, C, R> {

	R apply(A a, B b, C c);

	default <V> RequestHandler<A, B, C, V> andThen(Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (A a, B b, C c) -> after.apply(apply(a, b, c));
	}

}