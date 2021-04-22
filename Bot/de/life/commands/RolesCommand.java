package de.life.commands;

import java.awt.Color;

import de.life.classes.EmbedMessageBuilder;
import de.life.classes.LogMessanger;
import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class RolesCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");

		if (args.length < 2 || message.getMentionedMembers().size() == 0 || message.getMentionedRoles().size() == 0) {
			EmbedMessageBuilder.sendMessage("Roles",
					"Bitte benutze den Command mit der Syntax '!role add/remove @User @Role'", Color.RED, channel, 10);
			return;
		}

		if (!m.hasPermission(Permission.MANAGE_ROLES)) {
			EmbedMessageBuilder.sendMessage("Roles", "Dazu hast du nicht die Berechtigung",
					"Dir fehlt: Permission.MANAGE_ROLES", Color.RED, channel, 10);
			return;
		}

		try {
			switch (args[1]) {
			case "add":
				addRole(message.getMentionedMembers().get(0), message.getMentionedRoles().get(0));
				EmbedMessageBuilder.sendMessage("Roles",
						"Du hast " + message.getMentionedMembers().get(0).getAsMention() + " die Rolle "
								+ message.getMentionedRoles().get(0).getAsMention() + " hinzugefügt",
						m.getEffectiveName(), channel);

				LogMessanger.sendLog(m.getGuild().getIdLong(), "Roles",
						m.getAsMention() + " hat " + message.getMentionedMembers().get(0).getAsMention() + " die Rolle "
								+ message.getMentionedRoles().get(0).getAsMention() + " hinzugefügt");
				break;
			case "remove":
				removeRole(message.getMentionedMembers().get(0), message.getMentionedRoles().get(0));
				EmbedMessageBuilder.sendMessage("Roles",
						"Du hast " + message.getMentionedMembers().get(0).getAsMention() + " die Rolle "
								+ message.getMentionedRoles().get(0).getAsMention() + " entfernt",
						m.getEffectiveName(), channel);

				LogMessanger.sendLog(m.getGuild().getIdLong(), "Roles",
						m.getAsMention() + " hat " + message.getMentionedMembers().get(0).getAsMention() + " die Rolle "
								+ message.getMentionedRoles().get(0).getAsMention() + " entfernt");
				break;
			default:
				EmbedMessageBuilder.sendMessage("Roles",
						"Bitte benutze den Command mit der Syntax '!role add/remove @User @Role'", Color.RED, channel,
						10);
				return;
			}
		} catch (HierarchyException e) {
			EmbedMessageBuilder.sendMessage("Roles", "Dazu hat der Bot nicht die Berechtigung", Color.RED, channel, 10);
			return;
		}
	}

	private void addRole(Member m, Role role) throws HierarchyException {
		m.getGuild().addRoleToMember(m, role).queue();
	}

	private void removeRole(Member m, Role role) throws HierarchyException {
		m.getGuild().removeRoleFromMember(m, role).queue();
	}
}