package de.life.music;

import javax.annotation.Nonnull;

/**
 * Contains the possible states of a {@link de.life.music.QueueObject
 * QueueObject}.
 * 
 * @author Linus Potocnik
 *
 */
public enum QueueObjectType {

	/** Placeholder for future QueueObjectTypes. */
	UNKNOWN(-1),
	/** Playlist Type */
	PLAYLIST(0),
	/** Album Type */
	ALBUM(1),
	/** Track Type */
	TRACK(2);

	private final int key;

	QueueObjectType(int key) {
		this.key = key;
	}

	/**
	 * The raw style integer key
	 *
	 * @return The raw style key
	 */
	public int getKey() {
		return key;
	}

	/**
	 * Returns the object type associated with the provided key
	 *
	 * @param key The key to convert
	 *
	 * @return The object type or {@link #UNKNOWN}
	 */
	@Nonnull
	public static QueueObjectType fromKey(int key) {
		for (QueueObjectType type : values()) {
			if (type.key == key)
				return type;
		}
		return UNKNOWN;
	}
}
