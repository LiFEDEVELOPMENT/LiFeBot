/**
 * 
 */
package de.life.music;

import java.util.Arrays;
import java.util.Collections;

import javax.annotation.Nonnull;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 * Contains and manages a variable number of
 * {@link com.sedmelluq.discord.lavaplayer.track.AudioTrack Tracks} in a simple
 * and accessible way.
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
	private boolean lastTrack = false;
	private int pointer = 0;

	/**
	 * Creates a new QueueObject with a variable number of
	 * {@link com.sedmelluq.discord.lavaplayer.track.AudioTrack Tracks}.
	 * 
	 * @param type   The {@link de.life.music.QueueObjectType Type} of the
	 *               QueueObject. Defines the behavior of the QueueObject.
	 * @param tracks The {@link com.sedmelluq.discord.lavaplayer.track.AudioTrack
	 *               Track(s)} this QueueObject should contain.
	 */
	public QueueObject(@Nonnull QueueObjectType type, @Nonnull AudioTrack[] tracks, String title) {
		this.type = type;
		this.tracks = tracks;
		this.title = title;
		this.quantity = tracks.length;

		if (type.equals(QueueObjectType.TRACK) && this.quantity > 1)
			throw new IllegalArgumentException(
					"You cannot provide more than one track for a QueueObject of the type track. If you wish to queue multiple tracks in a single QueueObject, use the type QueueObjectType.PLAYLIST.");

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

	/**
	 * Creates a new {@link de.life.queueobject QueueObject} with exactly one
	 * {@link com.sedmelluq.discord.lavaplayer.track.AudioTrack Track}.
	 * 
	 * @param type   The {@link de.life.music.QueueObjectType Type} of the
	 *               QueueObject. Defines the behavior of the QueueObject.
	 * @param tracks The {@link com.sedmelluq.discord.lavaplayer.track.AudioTrack
	 *               Track} this QueueObject should contain.
	 */
	public QueueObject(@Nonnull AudioTrack track, String title) {
		AudioTrack[] t = { track };
		this.type = QueueObjectType.TRACK;
		this.tracks = t;
		this.title = title;
		this.quantity = 1;

		this.duration = track.getDuration();
	}

	public QueueObject(@Nonnull QueueObjectType type, @Nonnull AudioTrack[] tracks) {
		this(type, tracks, "Error: No title provided");
	}

	public QueueObject(@Nonnull AudioTrack track) {
		this(track, "Error: No title provided");
	}

	/**
	 * 
	 * @return The next {@link com.sedmelluq.discord.lavaplayer.track.AudioTrack
	 *         Track} of the queue. If end of the queue is reached, it returns null.
	 */
	public AudioTrack next() {
		if (lastTrack)
			return null;
		pointer++;
		if (pointer == quantity) {
			lastTrack = true;
		}
		return tracks[pointer - 1];
	}

	public int getPointer() {
		return pointer;
	}

	@Nonnull
	public QueueObjectType getType() {
		return type;
	}

	@Nonnull
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

	/**
	 * Shuffles the QueueObject if it's not of the
	 * {@link de.life.music.QueueObjectType Type} TRACK.
	 */
	public void shuffle() {
		if (type.equals(QueueObjectType.TRACK) || quantity == 1)
			throw new IllegalStateException(
					"You cannot shuffle a queue object containing just one track. Make sure, the type of the QueueObject is QueueObjectType.PLAYLIST or QueueObjectType.ALBUM and the QueueObject contains more than one song to use this method.");
		Collections.shuffle(Arrays.asList(tracks));
	}

	/**
	 * Skips the specified amount of
	 * {@link com.sedmelluq.discord.lavaplayer.track.AudioTrack Tracks} in the
	 * QueueObject
	 * 
	 * @param amount The amount of
	 *               {@link com.sedmelluq.discord.lavaplayer.track.AudioTrack
	 *               Tracks} to be skipped.
	 */
	public int jump(int amount) {
		pointer += amount;
		int result = pointer - quantity;
		if (pointer >= quantity) {
			lastTrack = true;
			pointer = quantity;
		}
		return result;
	}

}