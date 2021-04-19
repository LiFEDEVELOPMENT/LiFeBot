package de.life.music;

import java.util.HashMap;

import net.dv8tion.jda.api.entities.Guild;

public class QueueManager {

	private static QueueManager INSTANCE;
	private HashMap<Long, Queue> queueMap;

	public QueueManager() {
		queueMap = new HashMap<Long, Queue>();
	}

	public static QueueManager getInstance() {
		if (INSTANCE == null)
			INSTANCE = new QueueManager();
		return INSTANCE;
	}

	public Queue getQueue(Guild guild) {
		this.queueMap.putIfAbsent(guild.getIdLong(), new Queue());
		return this.queueMap.get(guild.getIdLong());
	}

}