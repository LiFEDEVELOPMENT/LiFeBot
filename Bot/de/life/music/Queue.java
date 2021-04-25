package de.life.music;

import java.util.ArrayList;
import java.util.Collections;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class Queue {
	private ArrayList<AudioTrack> queue;

	public Queue() {
		this.queue = new ArrayList<AudioTrack>();
	}
	
	public ArrayList<AudioTrack> getQueue() {
		return this.queue;
	}

	public void add(AudioTrack track) {
		this.queue.add(track);
	}

	public AudioTrack next() {
		if (this.queue.size() == 0)
			return null;
		return this.queue.remove(0);	
	}

	public void shuffle() {
		Collections.shuffle(this.queue);
	}
	
	public void clear() {
		this.queue = new ArrayList<AudioTrack>();
	}

	public void jump(int amount) {
		for (int i = 0; i < amount; i++) {
			if (this.queue.isEmpty())
				return;
			this.queue.remove(0);
		}
	}
}