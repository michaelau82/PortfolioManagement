package com.michael.portfolioManagement.domain;

public enum OptionType {
	CALL("C"), PUT("P");
	
	private String type;

	OptionType(final String type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 * @return
	 */
	public static OptionType fromString(final String type) {
		for (OptionType ot : OptionType.values()) {
			if (ot.type.equalsIgnoreCase(type)) {
				return ot;
			}
		}
		return null;
	}
}
