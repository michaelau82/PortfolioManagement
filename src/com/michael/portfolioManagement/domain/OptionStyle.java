package com.michael.portfolioManagement.domain;

public enum OptionStyle {
	AMERICAN_STYLE("A"), EUROPEAN_STYLE("E");
	
	private String style;

	OptionStyle(final String type) {
		this.style = type;
	}
	
	/**
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}
	
	/**
	 * @param style
	 * @return
	 */
	public static OptionStyle fromString(final String style) {
		for (OptionStyle os : OptionStyle.values()) {
			if (os.style.equalsIgnoreCase(style)) {
				return os;
			}
		}
		return null;
	}
}
