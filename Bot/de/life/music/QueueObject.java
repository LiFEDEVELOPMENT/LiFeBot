/**
 * 
 */
package de.life.music;

import java.util.Arrays;
import java.util.Collections;

import javax.annotation.Nonnull;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 * An object containing a song, playlist or album.
 * 
 * @author Linus Potocnik
 *
 */
public class QueueObject {

	private final QueueObjectType type;
	private final AudioTrack[] tracks;
	private final String title;
	private final int quantity;
	private final long duration;
	private boolean lastTrack = true;
	private int pointer = 0;

	/**
	 * @param type   The {@link de.life.music.QueueObjectType type} of the
	 *               QueueObject. Defines the behavior of the QueueObject.
	 * @param tracks The track/tracks this QueueObject should contain.
	 */
	public QueueObject(@Nonnull QueueObjectType type, @Nonnull AudioTrack[] tracks, String title) {
		this.type = type;
		this.tracks = tracks;
		this.title = title;
		this.quantity = tracks.length;

		if (type.equals(QueueObjectType.TRACK) && this.quantity > 1) {
			throw new IllegalArgumentException(
					"You cannot provide more than one track for a QueueObject of the type track. If you wish to queue multiple tracks in a single QueueObject, use the type QueueObjectType.PLAYLIST.");
		} else if (this.quantity > 1) {
			lastTrack = false;
		}

		long i = -1l;
		if (this.quantity == 1) {
			i = tracks[0].getDuration();
		} else if (this.quantity > 1) {
			for (AudioTrack t : tracks) {
				i += t.getDuration();
			}
		}
		this.duration = i;
	}

	public QueueObject(@Nonnull QueueObjectType type, @Nonnull AudioTrack[] tracks) {
		this(type, tracks, "Error: No title provided");
	}

	/**
	 * 
	 * @return The next {@link com.sedmelluq.discord.lavaplayer.track.AudioTrack
	 *         Track} of the queue.
	 */
	public AudioTrack getTrack() {
		pointer++;
		return tracks[pointer - 1];
	}

	public int getPointer() {
		return pointer;
	}

	public void setPointer(int pointer) {
		this.pointer = pointer;
	}

	public QueueObjectType getType() {
		return type;
	}

	public AudioTrack[] getTracks() {
		return tracks;
	}

	public String getTitle() {
		return title;
	}

	public int getQuantity() {
		return quantity;
	}

	public long getDuration() {
		return duration;
	}

	public void setLastTrack(boolean lastTrack) {
		this.lastTrack = lastTrack;
	}

	/**
	 * 
	 * @return Wether or not the current track is the last track of this
	 *         {@link de.life.music.QueueObject QueueObject}.
	 */
	public boolean isLastTrack() {
		return lastTrack;
	}

	/**
	 * Shuffles the QueueObject if it's not of the type QueueObjectType.TRACK
	 */
	public void shuffleQueue() {
		if (type.equals(QueueObjectType.TRACK) || quantity == 1)
			throw new IllegalStateException(
					"You cannot shuffle a queue object containing just one track. Make sure, the type of the QueueObject is QueueObjectType.PLAYLIST or QueueObjectType.ALBUM and the QueueObject contains more than one song to use this method.");
		Collections.shuffle(Arrays.asList(tracks));
	}

}