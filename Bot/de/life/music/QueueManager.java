package de.life.music;

import java.util.HashMap;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.entities.Guild;

public class QueueManager {
	private static QueueManager INSTANCE;
	private HashMap<Long, Queue> queueMap;

	public QueueManager() {
		queueMap = new HashMap<Long, Queue>();
	}

	/**
	 * This is used to always keep track of the current {@link de.life.music.Queue
	 * Queues} of the bot instance.
	 * 
	 * @return The instance of the QueueManager of this bot instance.
	 */
	public static QueueManager getInstance() {
		if (INSTANCE == null)
			INSTANCE = new QueueManager();
		return INSTANCE;
	}

	/**
	 * 
	 * @param guild The {@link net.dv8tion.jda.api.entities.Guild Guild} you want to
	 *              retrieve the {@link de.life.music.Queue Queue} of.
	 * @return The {@link de.life.music.Queue Queue} of the given
	 *         {@link net.dv8tion.jda.api.entities.Guild Guild}.
	 */
	@Nonnull
	public Queue getQueue(@Nonnull Guild guild) {
		return this.queueMap.putIfAbsent(guild.getIdLong(), new Queue());
	}

	/**
	 * Clears the {@link de.life.music.Queue Queue} of a specified
	 * {@link net.dv8tion.jda.api.entities.Guild Guild}.
	 * 
	 * @param guild The {@link net.dv8tion.jda.api.entities.Guild Guild} whose
	 *              {@link de.life.music.Queue Queue} should be cleared.
	 */
	public void clear(Guild guild) {
		this.queueMap.put(guild.getIdLong(), new Queue());
	}
}