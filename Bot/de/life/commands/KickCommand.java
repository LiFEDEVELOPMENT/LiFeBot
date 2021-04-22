package de.life.commands;

import java.awt.Color;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import de.life.classes.EmbedMessageBuilder;
import de.life.classes.LogMessanger;
import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class KickCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		GuildChannel gchannel = channel.getJDA().getGuildChannelById(channel.getId());
		Member target = message.getMentionedMembers().get(0);
		String reason = "";

		if (message.getMentionedMembers().size() < 1) {
			channel.sendMessage("Gib bitte einen Nutzer an!").complete().delete().queueAfter(3, TimeUnit.SECONDS);
			return;
		}

		if (message.getContentDisplay().split(" ").length > 2) {
			String[] args = message.getContentDisplay().split(" ");
			args = Arrays.copyOfRange(args, 2, args.length);
			reason = String.join(" ", args);
		}

		if (reason == "")
			reason = "Keine Begründung angegeben.";

		if (!m.hasPermission(gchannel, Permission.KICK_MEMBERS) || !m.canInteract(target)) {
			EmbedMessageBuilder.sendMessage("Kick", "Du hast nicht die Berechtigung, dieses Mitglied zu kicken!",
					"Dir fehlt: Permission.KICK_MEMBERS (oder du kannst nicht mit dieser Person interargieren)",
					Color.RED, channel, 10);
			return;
		}

		try {
			m.getGuild().kick(target, reason).queue();
		} catch (HierarchyException e) {
			EmbedMessageBuilder.sendMessage("Kick", "Der Bot hat nicht die Berechtigung, dieses Mitglied zu kicken!",
					"Ihm fehlt: Permission.KICK_MEMBERS (oder er kannst nicht mit dieser Person interargieren)",
					Color.RED, channel, 10);

			LogMessanger.sendLog(m.getGuild().getIdLong(), "Kick",
					m.getAsMention() + " wollte " + target.getAsMention() + " kicken. Begründung:\n" + reason,
					Color.CYAN);
			return;

		}

		LogMessanger.sendLog(m.getGuild().getIdLong(), "Kick",
				m.getAsMention() + " hat " + target.getAsMention() + " gekickt. Begründung:\n" + reason, Color.CYAN);
	}
}