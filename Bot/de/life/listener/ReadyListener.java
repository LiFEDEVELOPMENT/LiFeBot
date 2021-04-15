package de.life.listener;

import java.awt.Color;

import de.life.classes.LogMessanger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyListener extends ListenerAdapter {

	public void onReady(ReadyEvent event) {
		System.out.println("Bot online.");
		for (Guild guild : event.getJDA().getGuilds()) {
			LogMessanger.sendLog(guild.getIdLong(), "Bot Status", "ONLINE!", Color.GREEN);

		}
	}
}