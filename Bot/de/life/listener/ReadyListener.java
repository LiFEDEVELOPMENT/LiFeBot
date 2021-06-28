package de.life.listener;

import java.awt.Color;

import de.life.classes.LogMessanger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class ReadyListener extends ListenerAdapter {

	@Override
	public void onReady(ReadyEvent event) {
		System.out.println("Bot online.");
		for (Guild guild : event.getJDA().getGuilds()) {
			LogMessanger.sendLog(guild.getIdLong(), "Bot Status", "ONLINE!", Color.GREEN);

			if (guild.getName().equals("DevBuild")) {
				guild.upsertCommand("report", "Reportet den angegebenen User")
						.addOption(OptionType.USER, "user", "Der zu reportende User", true)
						.addOption(OptionType.STRING, "reason", "Weswegen möchtest du den User reporten?", false)
						.queue();
			}
		}
	}
}