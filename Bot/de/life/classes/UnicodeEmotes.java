package de.life.classes;

public enum UnicodeEmotes {
	
	WHITE_CHECK("U+2705"),
	X("U+2757"),
	ROCK("U+270C"),
	PAPER("U+270A"),
	SCISSORS("U+1F590"),
	FREE("U+1F193"),
	UNO_REVERSE("UnoReverse:842028317829758986"),
	DOUBLE_ARROW_LEFT("U+23EE"),
	ARROW_LEFT("U+25C0"),
	ARROW_RIGHT("U+25B6"),
	DOUBLE_ARROW_RIGHT("U+23ED"),
	ONE("U+31U+FE0FU+20E3"),
	TWO("U+32U+FE0FU+20E3"),
	THREE("U+33U+FE0FU+20E3"),
	FOUR("U+34U+FE0FU+20E3"),
	FIVE("U+35U+FE0FU+20E3"),
	SIX("U+36U+FE0FU+20E3"),
	SEVEN("U+37U+FE0FU+20E3"),
	EIGHT("U+38U+FE0FU+20E3"),
	NINE("U+39U+FE0FU+20E3"),
	TEN("U+1F51F"),
	HOURGLASS("U+23F3"),
	SPEAKER("U+1F50A"),
	THUMBS_UP("U+2705"),
	THUMBS_DOWN("U+274C");
	
	private final String unicode;
	
	UnicodeEmotes(String unicode) {
		this.unicode = unicode;
	}
	
	public String getUnicode() {
		return this.unicode;
	}

}