package de.life.music;

import java.util.ArrayList;
import java.util.Collections;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class Queue {

	private ArrayList<AudioTrack> queue;
	private boolean looped = false;

	public Queue() {
		this.queue = new ArrayList<AudioTrack>();
	}

	public void add(AudioTrack track) {
		this.queue.add(track);
	}

	public AudioTrack next() {
		if (this.queue.size() == 0)
			return null;
		AudioTrack retTrack = queue.remove(0);
		if (looped)
			this.queue.add(retTrack);
		return retTrack;
	}

	public void shuffle() {
		Collections.shuffle(this.queue);
	}

	public boolean isLooped() {
		return this.looped;
	}

	public void setLooped(boolean looped) {
		this.looped = looped;
	}

	public ArrayList<AudioTrack> getQueue() {
		return this.queue;
	}

	public void clear() {
		this.queue = null;
	}

	public void jump(int amount) {
		for (int i = 0; i < amount; i++) {
			if (queue.isEmpty())
				return;
			this.queue.remove(0);
		}
	}

}