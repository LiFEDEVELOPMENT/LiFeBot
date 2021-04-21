package de.life.commands;

import java.util.Arrays;

import de.life.GlobalVariables;
import de.life.interfaces.ServerCommand;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class SQLCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		if (m.getUser().getIdLong() != GlobalVariables.userIDFelix && m.getUser().getIdLong() != GlobalVariables.userIDLinus)
			return;

		String[] args = message.getContentDisplay().split(" ");
		SQLite.onUpdate(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
	}
}