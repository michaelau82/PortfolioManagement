package com.michael.portfolioManagement.domain;

public enum PositionType {
	LONG("L"), SHORT("S");
	
	private String type;
	
	PositionType(final String type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	public static PositionType fromString(final String type) {
		for (PositionType pt : PositionType.values()) {
			if (pt.type.equalsIgnoreCase(type)) {
				return pt;
			}
		}
		return null;
	}

}
