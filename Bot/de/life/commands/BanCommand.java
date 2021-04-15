package de.life.commands;

import java.awt.Color;

import de.life.classes.EmbedMessageBuilder;
import de.life.classes.LogMessanger;
import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class BanCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {

		GuildChannel gchannel = channel.getJDA().getGuildChannelById(channel.getId());

		Member target = message.getMentionedMembers().get(0);
		String reason = "";

		if (message.getMentionedMembers().size() < 1) {
			EmbedMessageBuilder.sendMessage("Ban", "Bitte gib einen Nutzer an!", Color.RED, channel, 10);
			return;
		}

		if (message.getContentDisplay().split(" ").length > 2) {
			String[] args = message.getContentDisplay().split(" ");

			args[0] = null;
			args[1] = null;

			reason = args[2];

			for (int i = 3; i < args.length; i++) {
				reason = reason + " " + args[i];
			}
		}

		if (reason == "") {
			reason = "Keine Begründung angegeben.";
		}

		if (!m.hasPermission(gchannel, Permission.BAN_MEMBERS) || !m.canInteract(target)) {
			EmbedMessageBuilder.sendMessage("Ban", "Du hast nicht die Berechtigung, dieses Mitglied zu bannen!",
					"Dir fehlt: Permission.BAN_MEMBERS (oder du kannst nicht mit dieser Person interargieren)",
					Color.RED, channel, 10);

			LogMessanger.sendLog(m.getGuild().getIdLong(), "Ban",
					m.getAsMention() + " wollte " + target.getAsMention() + " bannen. Begründung:\n" + reason,
					Color.CYAN);

			return;
		}

		if (!m.getGuild().getSelfMember().hasPermission(gchannel, Permission.BAN_MEMBERS)
				|| m.getGuild().getSelfMember().canInteract(target)) {
			EmbedMessageBuilder.sendMessage("Ban", "Der Bot hat nicht die Berechtigung, dieses Mitglied zu bannen!",
					"Ihm fehlt: Permission.BAN_MEMBERS (oder er kannst nicht mit dieser Person interargieren)",
					Color.RED, channel, 10);

			LogMessanger.sendLog(m.getGuild().getIdLong(), "Ban",
					m.getAsMention() + " wollte " + target.getAsMention() + " bannen. Begründung:\n" + reason,
					Color.CYAN);

			return;
		}

		try {
			m.getGuild().ban(target, 0, reason).queue();
		} catch (HierarchyException e) {

			EmbedMessageBuilder.sendMessage("Ban", "Der Bot hat nicht die Berechtigung, dieses Mitglied zu bannen!",
					"Ihm fehlt: Permission.BAN_MEMBERS (oder er kannst nicht mit dieser Person interargieren)",
					Color.RED, channel, 10);

			LogMessanger.sendLog(m.getGuild().getIdLong(), "Ban",
					m.getAsMention() + " wollte " + target.getAsMention() + " bannen. Begründung:\n" + reason,
					Color.CYAN);

			return;

		}

		LogMessanger.sendLog(m.getGuild().getIdLong(), "Ban",
				m.getAsMention() + " hat " + target.getAsMention() + " gebannt. Begründung:\n" + reason, Color.CYAN);

	}
}