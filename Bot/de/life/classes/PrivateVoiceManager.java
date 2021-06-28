package de.life.classes;

import java.util.EnumSet;
import java.util.List;

import de.life.sql.SQLite;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class PrivateVoiceManager {

	public static boolean lockChannel(Guild guild, Member channelAdmin, List<Member> target) {
		if (!inVoiceChannel(channelAdmin))
			return false;
		for (Member m : target) {
			channelAdmin.getVoiceState().getChannel().createPermissionOverride(m)
					.setDeny(Permission.ALL_VOICE_PERMISSIONS).queue();
			if (m.getVoiceState().getChannel().equals(channelAdmin.getVoiceState().getChannel()))
				m.getGuild().kickVoiceMember(m).queue();
			return true;
		}

		return false;
	}

	public static boolean lockAll(Guild guild, Member channelAdmin) {
		Role everyone = channelAdmin.getGuild().getRoles().get((channelAdmin.getGuild().getRoles().size()) - 1);

		if (!inVoiceChannel(channelAdmin))
			return false;

		for (PermissionOverride permissionOverride : channelAdmin.getVoiceState().getChannel()
				.getMemberPermissionOverrides()) {
			channelAdmin.getVoiceState().getChannel().getManager()
					.removePermissionOverride(permissionOverride.getPermissionHolder());
		}

		channelAdmin.getVoiceState().getChannel().getManager()
				.putPermissionOverride(everyone, null,
						EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL,
								Permission.MANAGE_PERMISSIONS, Permission.MANAGE_WEBHOOKS, Permission.VIEW_CHANNEL,
								Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VOICE_STREAM,
								Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS,
								Permission.VOICE_MOVE_OTHERS, Permission.VOICE_USE_VAD, Permission.PRIORITY_SPEAKER))
				.putPermissionOverride(channelAdmin,
						EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT, Permission.PRIORITY_SPEAKER,
								Permission.VOICE_SPEAK, Permission.VOICE_STREAM, Permission.VOICE_MOVE_OTHERS,
								Permission.VOICE_USE_VAD),
						EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL,
								Permission.MANAGE_PERMISSIONS, Permission.MANAGE_WEBHOOKS, Permission.VOICE_MUTE_OTHERS,
								Permission.VOICE_DEAF_OTHERS))
				.queue();

		for (Member member : channelAdmin.getVoiceState().getChannel().getMembers()) {
			if (!member.equals(channelAdmin)) {
				member.getGuild().kickVoiceMember(member).queue();
			}
		}

		return true;
	}

	public static boolean unlockChannel(Guild guild, Member channelAdmin, List<Member> target) {
		if (!inVoiceChannel(channelAdmin))
			return false;
		for (Member m : target) {
			channelAdmin.getVoiceState().getChannel().createPermissionOverride(m)
					.setAllow(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK,
							Permission.VOICE_STREAM, Permission.VOICE_USE_VAD)
					.setDeny(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS,
							Permission.MANAGE_WEBHOOKS, Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS)
					.queue();
			return true;

		}
		return false;
	}

	public static boolean unlockAll(Guild guild, Member channelAdmin) {
		Role everyone = channelAdmin.getGuild().getRoles().get((channelAdmin.getGuild().getRoles().size()) - 1);

		if (!inVoiceChannel(channelAdmin))
			return false;

		for (PermissionOverride permissionOverride : channelAdmin.getVoiceState().getChannel()
				.getMemberPermissionOverrides()) {
			channelAdmin.getVoiceState().getChannel().getManager()
					.removePermissionOverride(permissionOverride.getPermissionHolder());
		}

		channelAdmin.getVoiceState().getChannel().getManager()
				.putPermissionOverride(everyone,
						EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK,
								Permission.VOICE_STREAM, Permission.VOICE_USE_VAD),
						EnumSet.of(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL,
								Permission.MANAGE_PERMISSIONS, Permission.MANAGE_WEBHOOKS, Permission.VOICE_MOVE_OTHERS,
								Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS))
				.queue();

		return true;
	}

	public static boolean createChannel(Guild guild, Category category, Member channelAdmin) {
		Role everyone = channelAdmin.getGuild().getRoles().get((channelAdmin.getGuild().getRoles().size()) - 1);

		if (!channelAdmin.getVoiceState().inVoiceChannel() || channelAdmin.getVoiceState().getChannel().getName()
				.equals("Temp | " + channelAdmin.getEffectiveName()))
			return false;

		VoiceChannel vc = (VoiceChannel) guild.createVoiceChannel("Temp | " + channelAdmin.getEffectiveName(), category)
				.complete().createPermissionOverride(channelAdmin)
				.setAllow(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT, Permission.PRIORITY_SPEAKER,
						Permission.VOICE_SPEAK, Permission.VOICE_STREAM, Permission.VOICE_MOVE_OTHERS,
						Permission.VOICE_USE_VAD)
				.setDeny(Permission.CREATE_INSTANT_INVITE, Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS,
						Permission.MANAGE_WEBHOOKS, Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS)
				.complete().getChannel();
		vc.createPermissionOverride(everyone).setDeny(Permission.ALL_VOICE_PERMISSIONS).queue();

		SQLite.onUpdate("INSERT INTO channel (guildid,channelid,type) VALUES ('" + channelAdmin.getGuild().getIdLong()
				+ "','" + vc.getIdLong() + "','temporary')");

		guild.moveVoiceMember(channelAdmin, vc).queue();
		return true;
	}

	public static boolean userLimit(Guild guild, Member channelAdmin, int userlimit) {
		if (!inVoiceChannel(channelAdmin))
			return false;

		userlimit = Math.abs(userlimit);
		if (userlimit > 99)
			userlimit = 99;
		channelAdmin.getVoiceState().getChannel().getManager().setUserLimit(userlimit).queue();

		return true;
	}

	private static boolean inVoiceChannel(Member m) {
		return m.getVoiceState().inVoiceChannel()
				&& m.getVoiceState().getChannel().getName().equals("Temp | " + m.getEffectiveName());
	}

}