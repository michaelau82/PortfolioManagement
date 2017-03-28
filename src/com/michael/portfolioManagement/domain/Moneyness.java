package com.michael.portfolioManagement.domain;

public enum Moneyness {
	IN_THE_MONEY("I"), OUT_OF_MONEY("O"), AT_THE_MONEY("A");
	
	private String moneyness;

	Moneyness(final String moneyness) {
		this.moneyness = moneyness;
	}

	/**
	 * @return the moneyness
	 */
	public String getMoneyness() {
		return moneyness;
	}
	
	/**
	 * @param moneyness
	 * @return
	 */
	public static Moneyness fromString(final String moneyness) {
		for (Moneyness money : Moneyness.values()) {
			if (money.moneyness.equalsIgnoreCase(moneyness)) {
				return money;
			}
		}
		return null;
	}
}
