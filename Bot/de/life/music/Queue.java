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

	public AudioTrack next() {
		if (this.queue.size() == 0)
			return null;
		return queue.remove(0);
	}

	public void shuffle() {
		Collections.shuffle(queue);
	}

	public boolean getLooped() {
		return looped;
	}

	public void setLooped(boolean looped) {
		this.looped = looped;
	}

}