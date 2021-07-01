package de.life.listener;

import java.awt.Color;

import de.life.classes.LogMessanger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class ReadyListener extends ListenerAdapter {

	Guild instance = null; //F�r Beta-Commands -> Sp�ter mit JDA instance = event.getJDA() zu ersetzen, um Commands global verf�gbar zu machen

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
				.addOption(OptionType.STRING, "reason", "Weswegen m�chtest du den User reporten?", false).queue();
		instance.upsertCommand("poll", "Managed eine Umfrage")
				.addSubcommands(new SubcommandData("start", "Startet eine Umfrage mit bis zu zehn Antwortm�glichkeiten")
						.addOption(OptionType.STRING, "frage", "Was m�chtest du fragen?", true)
						.addOption(OptionType.STRING, "antwort1", "Die erste Antwortm�glichkeit", true)
						.addOption(OptionType.STRING, "antwort2", "Die zweite Antwortm�glichkeit", true)
						.addOption(OptionType.STRING, "antwort3", "Die dritte Antwortm�glichkeit", false)
						.addOption(OptionType.STRING, "antwort4", "Die vierte Antwortm�glichkeit", false)
						.addOption(OptionType.STRING, "antwort5", "Die f�nfte Antwortm�glichkeit", false)
						.addOption(OptionType.STRING, "antwort6", "Die sechste Antwortm�glichkeit", false)
						.addOption(OptionType.STRING, "antwort7", "Die siebte Antwortm�glichkeit", false)
						.addOption(OptionType.STRING, "antwort8", "Die achte Antwortm�glichkeit", false)
						.addOption(OptionType.STRING, "antwort9", "Die neunte Antwortm�glichkeit", false)
						.addOption(OptionType.STRING, "antwort10", "Die zehnte Antwortm�glichkeit", false))
				.addSubcommands(new SubcommandData("stop", "Beendet eine Umfrage").addOption(OptionType.INTEGER,
						"pollid", "Welche Umfrage m�chtest du stoppen?", true))
				.queue();
	}
}