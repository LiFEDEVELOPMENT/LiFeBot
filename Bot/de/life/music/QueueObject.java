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
	private boolean lastTrack = true;
	private boolean shuffled = false;
	private int pointer = 0;

	/**
	 * @param type   The type of the QueueObject. Defines the behavior of the
	 *               OueueObject.
	 * @param tracks The track/tracks this queue object should contain.
	 */
	public QueueObject(@Nonnull QueueObjectType type, @Nonnull AudioTrack[] tracks) {
		this.type = type;
		this.tracks = tracks;
		if (type.equals(QueueObjectType.TRACK) && tracks.length > 1) {
			throw new IllegalArgumentException(
					"You cannot provide more than one track for a QueueObject of the type track. If you wish to queue multiple tracks in a single QueueObject, use the type QueueObjectType.PLAYLIST.");
		} else if (tracks.length > 1) {
			lastTrack = false;
		}
	}

	/**
	 * 
	 * @return The next Track of the queue.
	 */
	public AudioTrack getTrack() {
		pointer++;
		return tracks[pointer - 1];
	}

	/**
	 * Shuffles the QueueObject if it's not of the type QueueObjectType.TRACK
	 */
	public void shuffleQueue() {
		if (type.equals(QueueObjectType.TRACK) || tracks.length == 1)
			throw new IllegalStateException(
					"You cannot shuffle a queue object containing just one Track. Make sure, the type of the QueueObject is QueueObjectType.PLAYLIST or QueueObjectType.ALBUM and the QueueObject contains more than one song to use this method.");
		Collections.shuffle(Arrays.asList(tracks));
	}

}