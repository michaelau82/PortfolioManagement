package com.michael.portfolioManagement.consumer;

@FunctionalInterface
public interface IConsumer<T> {
	void accept(T t);
}
