package de.life.listener;

import java.awt.Color;

import de.life.classes.LogMessanger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class ReadyListener extends ListenerAdapter {

	Guild instance = null; // Fr Beta-Commands -> Sp�ter mit JDA instance = event.getJDA() zu ersetzen, um
							// Commands global verf�gbar zu machen

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
				.addOption(OptionType.STRING, "reason", "Wofür soll der User reportet werden?", false).queue();
		instance.upsertCommand("poll", "Managed eine Umfrage")
				.addSubcommands(new SubcommandData("start", "Startet eine Umfrage mit bis zu zehn Optionen")
						.addOption(OptionType.STRING, "frage", "Stelle eine Frage?", true)
						.addOption(OptionType.STRING, "antwort1", "Die erste Option", true)
						.addOption(OptionType.STRING, "antwort2", "Die zweite Option", true)
						.addOption(OptionType.STRING, "antwort3", "Die dritte Option", false)
						.addOption(OptionType.STRING, "antwort4", "Die vierte Option", false)
						.addOption(OptionType.STRING, "antwort5", "Die fuenfte Option", false)
						.addOption(OptionType.STRING, "antwort6", "Die sechste Option", false)
						.addOption(OptionType.STRING, "antwort7", "Die siebte Option", false)
						.addOption(OptionType.STRING, "antwort8", "Die achte Option", false)
						.addOption(OptionType.STRING, "antwort9", "Die neunte Option", false)
						.addOption(OptionType.STRING, "antwort10", "Die zehnte Option", false))
				.addSubcommands(new SubcommandData("stop", "Beendet eine Umfrage").addOption(OptionType.INTEGER,
						"pollid", "Bitte gebe die PollID der Umfrage an, welche gestoppt werden soll", true))
				.queue();
		instance.upsertCommand("hub", "Managed den/die Hubchannel des Servers")
				.addSubcommands(new SubcommandData("add", "Deklariert einen Sprachkanal als Hubchannel").addOption(
						OptionType.INTEGER, "channelid",
						"Die ID des Channels, der als Hubchannel deklariert werden soll.", true))
				.addSubcommands(new SubcommandData("remove", "Entfernt einen Hubchannel").addOption(OptionType.INTEGER,
						"channelid", "Die ID des zu entfernenden Channels"))
				.addSubcommands(new SubcommandData("list", "Zeigt eine Liste aller Hubchannels dieses Servers"));
		instance.upsertCommand("log", "Managed den/die Logchannel des Servers")
		.addSubcommands(new SubcommandData("add", "Deklariert einen Textkanal als Logchannel").addOption(
				OptionType.INTEGER, "channelid",
				"Die ID des Channels, der als Logchannel deklariert werden soll.", true))
		.addSubcommands(new SubcommandData("remove", "Entfernt einen Logchannel").addOption(OptionType.INTEGER,
				"channelid", "Die ID des zu entfernenden Channels"))
		.addSubcommands(new SubcommandData("list", "Zeigt eine Liste aller Logchannels dieses Servers"));
	}
}