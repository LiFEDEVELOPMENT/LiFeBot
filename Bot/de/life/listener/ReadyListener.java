package de.life.listener;

import java.awt.Color;

import de.life.classes.LogMessanger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class ReadyListener extends ListenerAdapter {

	Guild instance = null; //Für Beta-Commands -> Später mit JDA instance = event.getJDA() zu ersetzen, um Commands global verfügbar zu machen

	@Override
	public void onReady(ReadyEvent event) {
		System.out.println("LiFe-Bot online! Schreibe stop, exit oder shutdown, um den Bot herunterzufahren.");
		for (Guild guild : event.getJDA().getGuilds()) {
			LogMessanger.sendLog(guild.getIdLong(), "Bot Status", "ONLINE!", Color.GREEN);

			if (guild.getName().equals("DevBuild"))
				instance = guild;
		}

		instance.upsertCommand("report", "Reportet den angegebenen User")
				.addOption(OptionType.USER, "user", "Der zu reportende User", true)
				.addOption(OptionType.STRING, "reason", "Weswegen möchtest du den User reporten?", false).queue();
		instance.upsertCommand("poll", "Managed eine Umfrage")
				.addSubcommands(new SubcommandData("start", "Startet eine Umfrage mit bis zu zehn Antwortmöglichkeiten")
						.addOption(OptionType.STRING, "frage", "Was möchtest du fragen?", true)
						.addOption(OptionType.STRING, "antwort1", "Die erste Antwortmöglichkeit", true)
						.addOption(OptionType.STRING, "antwort2", "Die zweite Antwortmöglichkeit", true)
						.addOption(OptionType.STRING, "antwort3", "Die dritte Antwortmöglichkeit", false)
						.addOption(OptionType.STRING, "antwort4", "Die vierte Antwortmöglichkeit", false)
						.addOption(OptionType.STRING, "antwort5", "Die fünfte Antwortmöglichkeit", false)
						.addOption(OptionType.STRING, "antwort6", "Die sechste Antwortmöglichkeit", false)
						.addOption(OptionType.STRING, "antwort7", "Die siebte Antwortmöglichkeit", false)
						.addOption(OptionType.STRING, "antwort8", "Die achte Antwortmöglichkeit", false)
						.addOption(OptionType.STRING, "antwort9", "Die neunte Antwortmöglichkeit", false)
						.addOption(OptionType.STRING, "antwort10", "Die zehnte Antwortmöglichkeit", false))
				.addSubcommands(new SubcommandData("stop", "Beendet eine Umfrage").addOption(OptionType.INTEGER,
						"pollid", "Welche Umfrage möchtest du stoppen?", true))
				.queue();
	}
}