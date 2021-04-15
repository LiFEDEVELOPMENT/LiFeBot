package de.life.commands;

import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import de.life.interfaces.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class MemberInfoCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		message.delete().queue();

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm:ss");
		GuildChannel gchannel = channel.getJDA().getGuildChannelById(channel.getId());
		Member mentioned = m.getGuild().getMember(m.getUser());
		if (message.getMentionedMembers().size() > 0)
			mentioned = message.getMentionedMembers().get(0);

		if (!m.getGuild().getSelfMember().hasPermission(gchannel, Permission.MESSAGE_EMBED_LINKS)) {
			channel.sendMessage("Dazu hat der Bot leider keine Berechtigung").complete().delete().queueAfter(5,
					TimeUnit.SECONDS);

			return;
		}

		Field nickname = new Field("Nickname", mentioned.getEffectiveName(), true);
		Field username = new Field("Discord-Username", mentioned.getUser().getAsTag(), true);
		Field creationDate = new Field("Account erstellt am", mentioned.getTimeCreated().format(dtf), true);
		Field joinDate = new Field("Server beigetreten am", mentioned.getTimeJoined().format(dtf), true);

		EmbedBuilder userBuilder = new EmbedBuilder();
		userBuilder.addField(nickname);
		userBuilder.addField(username);
		userBuilder.addBlankField(true);
		userBuilder.addField(creationDate);
		userBuilder.addField(joinDate);
		userBuilder.setThumbnail(mentioned.getUser().getAvatarUrl());
		userBuilder.setColor(Color.decode("#8adcff"));
		userBuilder.setFooter("Memberinfo angefragt von " + m.getUser().getAsTag());
		channel.sendMessage(userBuilder.build()).queue();

	}
}