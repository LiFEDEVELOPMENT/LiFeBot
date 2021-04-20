package de.life.commands;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import de.life.classes.EmbedMessageBuilder;
import de.life.classes.LogMessanger;
import de.life.interfaces.ServerCommand;
import de.life.listener.VoiceListener;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class PrivateChannelCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");

		if (args.length == 1) {
			createChannel(m, channel, message);
			return;
		}
		switch (args[1].toLowerCase()) {
		case "lockall":
			lockAll(m, channel, message);
			break;
		case "lock":
			lock(m, channel, message);
			break;
		case "unlockall":
			unlockAll(m, channel, message);
			break;
		case "unlock":
			unlock(m, channel, message);
			break;
		case "userlimit":
			userlimit(m, channel, message);
			break;
		default:
			break;
		}
	}

	private void createChannel(Member m, MessageChannel channel, Message message) {
		message.delete().queue();
		VoiceChannel hub = null;

		ResultSet set = SQLite.onQuery(
				"SELECT * FROM channel WHERE guildid = '" + message.getGuild().getIdLong() + "' AND type = 'hub'");

		try {
			if (set.next()) {

				hub = m.getGuild().getVoiceChannelById(set.getLong("channelid"));
			} else {
				EmbedMessageBuilder.sendMessage(
						"Es gibt keinen Hub-Channel auf diesem Server. Bitte einen Serveradministrator, einen mit !hub add <Channel-ID> zu erstellen.",
						Color.CYAN, message.getChannel(), 10);
				return;
			}
		} catch (SQLException e) {

		}

		if (!m.getVoiceState().inVoiceChannel()) {
			channel.sendMessage("Du befindest dich in keinem Voicechannel.").complete().delete().queueAfter(5,
					TimeUnit.SECONDS);

			return;
		}

		if (m.getVoiceState().getChannel().getName().substring(3).equalsIgnoreCase(m.getEffectiveName())) {
			channel.sendMessage("Du befindest dich bereits in einem temporären Voicechannel.").complete().delete()
					.queueAfter(5, TimeUnit.SECONDS);

			return;
		}
		
		VoiceListener.INSTANCE.onJoin(hub, m);
	}

	private void lockAll(Member m, MessageChannel channel, Message message) {
		message.delete().queue();
		Role everyone = m.getGuild().getRoles().get((m.getGuild().getRoles().size()) - 1);

		if (!m.getVoiceState().inVoiceChannel()) {
			channel.sendMessage("Du befindest dich in keinem Voicechannel.").complete().delete().queueAfter(5,
					TimeUnit.SECONDS);

			return;
		}

		if (!m.getVoiceState().getChannel().getName().substring(3).equalsIgnoreCase(m.getEffectiveName())) {
			channel.sendMessage("Du musst dich in einem dir gehörenden temporären Voicechannel befinden.").complete()
					.delete().queueAfter(5, TimeUnit.SECONDS);

			return;
		}

		for (PermissionOverride or : m.getVoiceState().getChannel().getMemberPermissionOverrides()) {
			m.getVoiceState().getChannel().getManager().removePermissionOverride(or.getPermissionHolder());
		}

		m.getVoiceState().getChannel().getManager()
				.putPermissionOverride(everyone, null,
						EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL,
								Permission.MANAGE_PERMISSIONS, Permission.MANAGE_WEBHOOKS, Permission.VIEW_CHANNEL,
								Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VOICE_STREAM,
								Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS,
								Permission.VOICE_MOVE_OTHERS, Permission.VOICE_USE_VAD, Permission.PRIORITY_SPEAKER))
				.putPermissionOverride(m,
						EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT, Permission.PRIORITY_SPEAKER,
								Permission.VOICE_SPEAK, Permission.VOICE_STREAM, Permission.VOICE_MOVE_OTHERS,
								Permission.VOICE_USE_VAD),
						EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL,
								Permission.MANAGE_PERMISSIONS, Permission.MANAGE_WEBHOOKS, Permission.VOICE_MUTE_OTHERS,
								Permission.VOICE_DEAF_OTHERS))
				.queue();

		channel.sendMessage("Du hast deinen Channel für jeden gesperrt!").queue();

		for (Member member : m.getVoiceState().getChannel().getMembers()) {
			if (!member.equals(m)) {
				member.getGuild().kickVoiceMember(member).queue();
			}
		}

		LogMessanger.sendLog(m.getGuild().getIdLong(), m.getVoiceState().getChannel().getName() + ":",
				m.getAsMention() + " hat seinen Channel für jeden gesperrt.", Color.CYAN);
	}

	private void lock(Member m, MessageChannel channel, Message message) {
		message.delete().queue();

		if (!m.getVoiceState().inVoiceChannel()) {
			EmbedMessageBuilder.sendMessage("Fehler", m.getAsMention() + ", du bist in keinem Voicechannel!", Color.RED,
					channel, 5);

			return;
		}

		if (!m.getVoiceState().getChannel().getName().substring(3).equalsIgnoreCase(m.getEffectiveName())) {
			EmbedMessageBuilder.sendMessage("Fehler",
					m.getAsMention() + ", du musst dich in einem dir gehörendem Voicechannel befinden!", Color.RED,
					channel, 5);

			return;
		}

		if (message.getMentionedMembers().size() < 1) {
			EmbedMessageBuilder.sendMessage("Fehler",
					m.getAsMention() + ", gib bitte mindestens einen User an, den du sperren möchtest!", Color.RED,
					channel, 5);

			return;
		}

		if (message.getMentionedMembers().size() == 1) {
			executeLock(m, channel, message.getMentionedMembers().get(0));
		} else if (message.getMentionedMembers().size() == 2) {
			executeLock(m, channel, message.getMentionedMembers().get(0), message.getMentionedMembers().get(1));
		} else if (message.getMentionedMembers().size() == 3) {
			executeLock(m, channel, message.getMentionedMembers().get(0), message.getMentionedMembers().get(1),
					message.getMentionedMembers().get(2));
		} else if (message.getMentionedMembers().size() > 3) {
			EmbedMessageBuilder.sendMessage("Fehler", "Du kannst nur drei Nutzer auf gleichzeitig sperren!", Color.RED,
					channel);
			executeLock(m, channel, message.getMentionedMembers().get(0), message.getMentionedMembers().get(1),
					message.getMentionedMembers().get(2));
		}
	}

	private void unlockAll(Member m, MessageChannel channel, Message message) {
		message.delete().queue();

		Role everyone = m.getGuild().getRoles().get((m.getGuild().getRoles().size()) - 1);

		if (!m.getVoiceState().inVoiceChannel()) {
			channel.sendMessage("Du befindest dich in keinem Voicechannel.").complete().delete().queueAfter(5,
					TimeUnit.SECONDS);

			return;
		}

		if (!m.getVoiceState().getChannel().getName().substring(3).equalsIgnoreCase(m.getEffectiveName())) {
			channel.sendMessage("Du musst dich in einem dir gehörenden temporären Voicechannel befinden.").complete()
					.delete().queueAfter(5, TimeUnit.SECONDS);

			return;
		}

		for (PermissionOverride or : m.getVoiceState().getChannel().getMemberPermissionOverrides()) {
			m.getVoiceState().getChannel().getManager().removePermissionOverride(or.getPermissionHolder());
		}

		m.getVoiceState().getChannel().getManager()
				.putPermissionOverride(everyone,
						EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT, Permission.PRIORITY_SPEAKER,
								Permission.VOICE_SPEAK, Permission.VOICE_STREAM, Permission.VOICE_USE_VAD),
						EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL,
								Permission.MANAGE_PERMISSIONS, Permission.MANAGE_WEBHOOKS, Permission.VOICE_MOVE_OTHERS,
								Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS))
				.queue();

		channel.sendMessage("Du hast deinen Channel für jeden freigeschaltet!").queue();

		LogMessanger.sendLog(m.getGuild().getIdLong(), m.getVoiceState().getChannel().getName() + ":",
				m.getAsMention() + " hat seinen Channel für jeden freigegeben.", Color.CYAN);
	}

	private void unlock(Member m, MessageChannel channel, Message message) {
		message.delete().queue();

		if (!m.getVoiceState().inVoiceChannel()) {
			EmbedMessageBuilder.sendMessage("Fehler", m.getAsMention() + ", du bist in keinem Voicechannel!", Color.RED,
					channel, 5);

			return;
		}

		if (!m.getVoiceState().getChannel().getName().substring(3).equalsIgnoreCase(m.getEffectiveName())) {
			EmbedMessageBuilder.sendMessage("Fehler",
					m.getAsMention() + ", du musst dich in einem dir gehörendem Voicechannel befinden!", Color.RED,
					channel, 5);

			return;
		}

		if (message.getMentionedMembers().size() < 1) {
			EmbedMessageBuilder.sendMessage("Fehler",
					m.getAsMention() + ", gib bitte mindestens einen User an, den du freischalten möchtest!", Color.RED,
					channel, 5);

			return;
		}

		if (message.getMentionedMembers().size() == 1) {
			executeUnlock(m, channel, message.getMentionedMembers().get(0));
		} else if (message.getMentionedMembers().size() == 2) {
			executeUnlock(m, channel, message.getMentionedMembers().get(0), message.getMentionedMembers().get(1));
		} else if (message.getMentionedMembers().size() == 3) {
			executeUnlock(m, channel, message.getMentionedMembers().get(0), message.getMentionedMembers().get(1),
					message.getMentionedMembers().get(2));
		} else if (message.getMentionedMembers().size() > 3) {
			EmbedMessageBuilder.sendMessage("Fehler", "Du kannst nur drei Nutzer auf gleichzeitig freischalten!",
					Color.RED, channel);
			executeUnlock(m, channel, message.getMentionedMembers().get(0), message.getMentionedMembers().get(1),
					message.getMentionedMembers().get(2));
		}
	}

	private void userlimit(Member m, MessageChannel channel, Message message) {
		message.delete().queue();
		String[] args = message.getContentDisplay().split(" ");

		if (!m.getVoiceState().inVoiceChannel()) {
			EmbedMessageBuilder.sendMessage("Fehler", m.getAsMention() + ", du bist in keinem Voicechannel!", Color.RED,
					channel, 5);

			return;
		}

		if (!m.getVoiceState().getChannel().getName().substring(3).equalsIgnoreCase(m.getEffectiveName())) {
			EmbedMessageBuilder.sendMessage("Fehler",
					m.getAsMention() + ", du musst dich in einem dir gehörendem Voicechannel befinden!", Color.RED,
					channel, 5);

			return;
		}

		if (args.length < 3) {
			EmbedMessageBuilder.sendMessage("Fehler", m.getAsMention() + ", gib bitte eine Nutzeranzahl an!", Color.RED,
					channel, 5);

			return;
		}
		try {
			Integer userlimit = Integer.parseInt(args[2]);
			userlimit = Math.abs(userlimit);
			if (userlimit > 99)
				userlimit = 99;
			m.getVoiceState().getChannel().getManager().setUserLimit(userlimit).queue();
			if (userlimit == 0) {
				EmbedMessageBuilder.sendMessage("Userlimit",
						m.getAsMention() + ", das Userlimit deines privaten Kanals wurde entfernt!", Color.GREEN,
						channel, 5);
				return;
			}
			EmbedMessageBuilder.sendMessage("Userlimit",
					m.getAsMention() + ", das Userlimit deines privaten Kanals wurde auf " + userlimit + " gesetzt!",
					Color.GREEN, channel, 5);
		} catch (NumberFormatException e) {
			EmbedMessageBuilder.sendMessage("Fehler", m.getAsMention() + ", gib bitte eine Nutzeranzahl an!", Color.RED,
					channel, 5);
		}
	}

	public static void executeLock(Member channelAdmin, MessageChannel commandChannel, Member member) {

		channelAdmin.getVoiceState().getChannel().getManager()
				.putPermissionOverride(member, null,
						EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL,
								Permission.MANAGE_PERMISSIONS, Permission.MANAGE_WEBHOOKS, Permission.VIEW_CHANNEL,
								Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VOICE_STREAM,
								Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS,
								Permission.VOICE_MOVE_OTHERS, Permission.VOICE_USE_VAD, Permission.PRIORITY_SPEAKER))
				.queue();

		LogMessanger.sendLog(channelAdmin.getGuild().getIdLong(),
				channelAdmin.getVoiceState().getChannel().getName() + ":",
				channelAdmin.getAsMention() + " hat seinen Channel für " + member.getAsMention() + " gesperrt!",
				Color.YELLOW);

		EmbedMessageBuilder.sendMessage(
				channelAdmin.getAsMention() + ", du hast " + member.getAsMention() + " für den Kanal geperrt!",
				Color.YELLOW, commandChannel);

		if (member.getVoiceState().getChannel() == null)
			return;
		if (member.getVoiceState().getChannel().equals(channelAdmin.getVoiceState().getChannel())) {
			member.getGuild().kickVoiceMember(member).queue();
		}
	}

	public static void executeLock(Member channelAdmin, MessageChannel commandChannel, Member member,
			Member memberTwo) {

		channelAdmin.getVoiceState().getChannel().getManager()
				.putPermissionOverride(member, null,
						EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL,
								Permission.MANAGE_PERMISSIONS, Permission.MANAGE_WEBHOOKS, Permission.VIEW_CHANNEL,
								Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VOICE_STREAM,
								Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS,
								Permission.VOICE_MOVE_OTHERS, Permission.VOICE_USE_VAD, Permission.PRIORITY_SPEAKER))
				.putPermissionOverride(memberTwo, null,
						EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL,
								Permission.MANAGE_PERMISSIONS, Permission.MANAGE_WEBHOOKS, Permission.VIEW_CHANNEL,
								Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VOICE_STREAM,
								Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS,
								Permission.VOICE_MOVE_OTHERS, Permission.VOICE_USE_VAD, Permission.PRIORITY_SPEAKER))
				.queue();

		LogMessanger.sendLog(channelAdmin.getGuild().getIdLong(),
				channelAdmin.getVoiceState().getChannel().getName() + ":",
				channelAdmin.getAsMention() + " hat seinen Channel für " + member.getAsMention() + " und "
						+ memberTwo.getAsMention() + " gesperrt!",
				Color.YELLOW);

		EmbedMessageBuilder.sendMessage(channelAdmin.getAsMention() + ", du hast " + member.getAsMention() + " und "
				+ memberTwo.getAsMention() + " für den Kanal gesperrt!", Color.YELLOW, commandChannel);

		if (member.getVoiceState().getChannel() != null) {
			if (member.getVoiceState().getChannel().equals(channelAdmin.getVoiceState().getChannel())) {
				member.getGuild().kickVoiceMember(member).queue();
			}
		}

		if (memberTwo.getVoiceState().getChannel() == null)
			return;
		if (memberTwo.getVoiceState().getChannel().equals(channelAdmin.getVoiceState().getChannel())) {
			memberTwo.getGuild().kickVoiceMember(memberTwo).queue();
		}

	}

	public static void executeLock(Member channelAdmin, MessageChannel commandChannel, Member member, Member memberTwo,
			Member memberThree) {

		channelAdmin.getVoiceState().getChannel().getManager()
				.putPermissionOverride(member, null,
						EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL,
								Permission.MANAGE_PERMISSIONS, Permission.MANAGE_WEBHOOKS, Permission.VIEW_CHANNEL,
								Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VOICE_STREAM,
								Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS,
								Permission.VOICE_MOVE_OTHERS, Permission.VOICE_USE_VAD, Permission.PRIORITY_SPEAKER))
				.putPermissionOverride(memberTwo, null,
						EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL,
								Permission.MANAGE_PERMISSIONS, Permission.MANAGE_WEBHOOKS, Permission.VIEW_CHANNEL,
								Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VOICE_STREAM,
								Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS,
								Permission.VOICE_MOVE_OTHERS, Permission.VOICE_USE_VAD, Permission.PRIORITY_SPEAKER))
				.putPermissionOverride(memberThree, null,
						EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL,
								Permission.MANAGE_PERMISSIONS, Permission.MANAGE_WEBHOOKS, Permission.VIEW_CHANNEL,
								Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VOICE_STREAM,
								Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS,
								Permission.VOICE_MOVE_OTHERS, Permission.VOICE_USE_VAD, Permission.PRIORITY_SPEAKER))
				.queue();

		LogMessanger
				.sendLog(channelAdmin.getGuild().getIdLong(), channelAdmin.getVoiceState().getChannel().getName() + ":",
						channelAdmin.getAsMention() + " hat seinen Channel für " + member.getAsMention() + ", "
								+ memberTwo.getAsMention() + " und " + memberThree.getAsMention() + " gesperrt!",
						Color.YELLOW);

		EmbedMessageBuilder.sendMessage(
				channelAdmin.getAsMention() + ", du hast " + member.getAsMention() + ", " + memberTwo.getAsMention()
						+ " und " + memberThree.getAsMention() + " für den Kanal gesperrt!",
				Color.YELLOW, commandChannel);

		if (member.getVoiceState().getChannel() != null) {
			if (member.getVoiceState().getChannel().equals(channelAdmin.getVoiceState().getChannel())) {
				member.getGuild().kickVoiceMember(member).queue();
			}
		}
		if (memberTwo.getVoiceState().getChannel() != null) {
			if (memberTwo.getVoiceState().getChannel().equals(channelAdmin.getVoiceState().getChannel())) {
				memberTwo.getGuild().kickVoiceMember(memberTwo).queue();
			}
		}
		if (memberThree.getVoiceState().getChannel() == null)
			return;
		if (memberThree.getVoiceState().getChannel().equals(channelAdmin.getVoiceState().getChannel())) {
			memberThree.getGuild().kickVoiceMember(memberThree).queue();
		}

	}

	public static void executeUnlock(Member channelAdmin, MessageChannel commandChannel, Member member) {

		channelAdmin.getVoiceState().getChannel().getManager().putPermissionOverride(member,
				EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT, Permission.PRIORITY_SPEAKER,
						Permission.VOICE_SPEAK, Permission.VOICE_STREAM, Permission.VOICE_MOVE_OTHERS,
						Permission.VOICE_USE_VAD),
				EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS,
						Permission.MANAGE_WEBHOOKS, Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS))
				.queue();

		LogMessanger.sendLog(channelAdmin.getGuild().getIdLong(),
				channelAdmin.getVoiceState().getChannel().getName() + ":",
				channelAdmin.getAsMention() + " hat seinen Channel für " + member.getAsMention() + " freigegeben!",
				Color.YELLOW);

		EmbedMessageBuilder.sendMessage(
				channelAdmin.getAsMention() + ", du hast " + member.getAsMention() + " für den Kanal freigeschaltet!",
				Color.YELLOW, commandChannel);
	}

	public static void executeUnlock(Member channelAdmin, MessageChannel commandChannel, Member member,
			Member memberTwo) {

		channelAdmin.getVoiceState().getChannel().getManager().putPermissionOverride(member,
				EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT, Permission.PRIORITY_SPEAKER,
						Permission.VOICE_SPEAK, Permission.VOICE_STREAM, Permission.VOICE_MOVE_OTHERS,
						Permission.VOICE_USE_VAD),
				EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS,
						Permission.MANAGE_WEBHOOKS, Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS))
				.putPermissionOverride(memberTwo,
						EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT, Permission.PRIORITY_SPEAKER,
								Permission.VOICE_SPEAK, Permission.VOICE_STREAM, Permission.VOICE_MOVE_OTHERS,
								Permission.VOICE_USE_VAD),
						EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL,
								Permission.MANAGE_PERMISSIONS, Permission.MANAGE_WEBHOOKS, Permission.VOICE_MUTE_OTHERS,
								Permission.VOICE_DEAF_OTHERS))
				.queue();

		LogMessanger.sendLog(channelAdmin.getGuild().getIdLong(),
				channelAdmin.getVoiceState().getChannel().getName() + ":",
				channelAdmin.getAsMention() + " hat seinen Channel für " + member.getAsMention() + " und "
						+ memberTwo.getAsMention() + " freigegeben!",
				Color.YELLOW);

		EmbedMessageBuilder.sendMessage(channelAdmin.getAsMention() + ", du hast " + member.getAsMention() + " und "
				+ memberTwo.getAsMention() + " für den Kanal freigeschaltet!", Color.YELLOW, commandChannel);

	}

	public static void executeUnlock(Member channelAdmin, MessageChannel commandChannel, Member member,
			Member memberTwo, Member memberThree) {

		channelAdmin.getVoiceState().getChannel().getManager().putPermissionOverride(member,
				EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT, Permission.PRIORITY_SPEAKER,
						Permission.VOICE_SPEAK, Permission.VOICE_STREAM, Permission.VOICE_MOVE_OTHERS,
						Permission.VOICE_USE_VAD),
				EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS,
						Permission.MANAGE_WEBHOOKS, Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS))
				.putPermissionOverride(memberTwo,
						EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT, Permission.PRIORITY_SPEAKER,
								Permission.VOICE_SPEAK, Permission.VOICE_STREAM, Permission.VOICE_MOVE_OTHERS,
								Permission.VOICE_USE_VAD),
						EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL,
								Permission.MANAGE_PERMISSIONS, Permission.MANAGE_WEBHOOKS, Permission.VOICE_MUTE_OTHERS,
								Permission.VOICE_DEAF_OTHERS))
				.putPermissionOverride(memberThree,
						EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT, Permission.PRIORITY_SPEAKER,
								Permission.VOICE_SPEAK, Permission.VOICE_STREAM, Permission.VOICE_MOVE_OTHERS,
								Permission.VOICE_USE_VAD),
						EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL,
								Permission.MANAGE_PERMISSIONS, Permission.MANAGE_WEBHOOKS, Permission.VOICE_MUTE_OTHERS,
								Permission.VOICE_DEAF_OTHERS))
				.queue();

		LogMessanger.sendLog(channelAdmin.getGuild().getIdLong(),
				channelAdmin.getVoiceState().getChannel().getName() + ":",
				channelAdmin.getAsMention() + " hat seinen Channel für " + member.getAsMention() + ", "
						+ memberTwo.getAsMention() + " und " + memberThree.getAsMention() + " freigegeben!",
				Color.YELLOW);

		EmbedMessageBuilder.sendMessage(
				channelAdmin.getAsMention() + ", du hast " + member.getAsMention() + ", " + memberTwo.getAsMention()
						+ " und " + memberThree.getAsMention() + " für den Kanal freigeschaltet!",
				Color.YELLOW, commandChannel);

	}

}