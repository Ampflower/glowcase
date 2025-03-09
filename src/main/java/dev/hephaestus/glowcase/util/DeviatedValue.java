package dev.hephaestus.glowcase.util;

import java.util.function.Supplier;

public interface DeviatedValue<T> {
	T mean();
	T stdDev();

	T get(Supplier<Double> random);
}
