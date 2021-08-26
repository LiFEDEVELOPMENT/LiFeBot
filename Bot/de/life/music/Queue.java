package de.life.music;

import java.util.ArrayList;
import java.util.Collections;

import javax.annotation.Nonnull;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 * This class handles the queuing, managing and retrieving of
 * {@link com.sedmelluq.discord.lavaplayer.track.AudioTrack Tracks} in the
 * queue. Each {@link net.dv8tion.jda.api.entities.Guild Guild} needs a seperate
 * queue.
 * 
 * @author Linus Potocnik
 *
 */
public class Queue {

	private static ArrayList<QueueObject> queue = new ArrayList<QueueObject>();

	/**
	 * This method is used to retrieve
	 * {@link com.sedmelluq.discord.lavaplayer.track.AudioTrack Track} of the queue.
	 * 
	 * @return The next {@link com.sedmelluq.discord.lavaplayer.track.AudioTrack
	 *         Track} of the current queue. Returns null if there is none.
	 */
	public AudioTrack next() {
		if (queue.isEmpty())
			return null;

		AudioTrack returnTrack;
		if ((returnTrack = queue.get(0).next()) != null)
			return returnTrack;
		queue.remove(0);
		return this.next();
	}

	/**
	 * Adds a {@link de.life.music.QueueObject QueueObject} to the current queue.
	 * 
	 * @param o The {@link de.life.music.QueueObject QueueObject} that should be
	 *          added.
	 */
	public void add(@Nonnull QueueObject o) {
		queue.add(o);
	}

	/**
	 * Shuffles the current queue. TODO: Shuffle inner objects
	 */
	public void shuffle() {
		Collections.shuffle(queue);
	}

	/**
	 * Replaces the current queue with a new, empty queue.
	 */
	public void empty() {
		queue = new ArrayList<QueueObject>();
	}

	/**
	 * Skips the specified amount of
	 * {@link com.sedmelluq.discord.lavaplayer.track.AudioTrack Tracks} in the
	 * queue.
	 * 
	 * @param amount The amount of
	 *               {@link com.sedmelluq.discord.lavaplayer.track.AudioTrack
	 *               Tracks} to be skipped.
	 */
	public void jump(int amount) {
		if (queue.isEmpty())
			return;
		int result;
		if ((result = queue.get(0).jump(amount)) < 0) {
			queue.remove(0);
			this.jump((result * -1));
		}
	}
}
