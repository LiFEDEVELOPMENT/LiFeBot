package de.life.listener;

import de.life.classes.slashexecutors.Report;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;

public class SlashListener extends ListenerAdapter {
	public void onSlashCommand(SlashCommandEvent event) {

		if (event.getName().equals("report")) {
			new Report(event.getUser(), event.getOption("user").getAsUser(), event.getGuild(), event.getChannel(),
					(event.getOption("reason") != null ? event.getOption("reason").getAsString() : null));
			event.deferReply(true).complete()
					.sendMessage(event.getOption("user").getAsUser().getAsMention() + " wurde erfolgreich reportet!")
					.queue();
		}



		

		if (event.getName().equals("ttt")) {
			event.reply("Click the button to say hello").addActionRow(Button.primary("hello", "Click Me"), // Button
																											// with only
																											// a label
					Button.success("emoji", Emoji.fromMarkdown("<:minn:245267426227388416>"))) // Button with only an
																								// emoji
					.queue();
		} else if (event.getName().equals("info")) {
			event.reply("Click the buttons for more info").addActionRow( // link buttons don't send events, they just
																			// open a 0link in the browser when clicked
					Button.link("https://github.com/DV8FromTheWorld/JDA", "GitHub")
							.withEmoji(Emoji.fromMarkdown("<:github:849286315580719104>")), // Link Button with label
																							// and emoji
					Button.link("https://ci.dv8tion.net/job/JDA/javadoc/", "Javadocs")) // Link Button with only a label
					.queue();
		}
	}

	public void onButtonClick(ButtonClickEvent event) {
		if (event.getComponentId().equals("hello")) {
			event.reply("Hello :)").queue();
		}
	}
}
