package de.life.listener;

import de.life.classes.slashexecutors.Announce;
import de.life.classes.slashexecutors.Poll;
import de.life.classes.slashexecutors.Report;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashListener extends ListenerAdapter {

	@Override
	public void onSlashCommand(SlashCommandEvent event) {

		if (event.getName().equals("report")) {
			new Report(event.getUser(), event.getOption("user").getAsUser(), event.getGuild(), event.getChannel(),
					(event.getOption("reason") != null ? event.getOption("reason").getAsString() : null));
			event.deferReply(true).complete()
					.sendMessage(event.getOption("user").getAsUser().getAsMention() + " wurde erfolgreich reportet!")
					.queue();
		}

		if (event.getName().equals("poll")) {
			if (event.getSubcommandName().equals("start")) {
				new Poll().pollStart(event);
			} else if (event.getSubcommandName().equals("stop")) {
				new Poll().pollStop(event);
			}
		}

		if (event.getName().equals("announce")) {
			new Announce().announce(event);
		}
	}
}