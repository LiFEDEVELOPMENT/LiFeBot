package de.life.commands;

import de.life.GlobalVariables;
import de.life.classes.EmbedMessageBuilder;
import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ListGuildsCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		if (m.getUser().getIdLong() != GlobalVariables.userIDFelix
				&& m.getUser().getIdLong() != GlobalVariables.userIDLinus)
			return;

		String s = "";
		int i = m.getJDA().getGuilds().size();
		for (Guild g : m.getJDA().getGuilds()) {
			s = s.concat(g.getName() + " | Members: " + g.getMemberCount() + " | " + g.getIdLong() + "\n");
			i--;
			if (s.length() > 1600) {
				s.concat("Und weitere " + i + " Guilds");
				break;
			}
		}
		EmbedMessageBuilder.sendMessage("Guilds", s, channel);
	}
}