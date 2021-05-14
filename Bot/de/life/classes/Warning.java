package de.life.classes;

import java.time.OffsetDateTime;

import de.life.LiFeBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class Warning {

	private final static JDA jda = LiFeBot.INSTANCE.getJDA();
	private final Guild guild;
	private final User moderator;
	private final User victim;
	private final String reason;	
	private final OffsetDateTime time;
	
	public Warning(Long guildid, Long moderatorid, Long victimid, String reason, String time) {
		this.guild = jda.getGuildById(guildid);
		this.moderator = (User) jda.retrieveUserById(moderatorid);
		this.victim = (User) jda.retrieveUserById(victimid);
		this.reason = reason;
		this.time = OffsetDateTime.parse(time);
	}

	public Guild getGuild() {
		return guild;
	}

	public User getModerator() {
		return moderator;
	}

	public User getVictim() {
		return victim;
	}

	public String getReason() {
		return reason;
	}

	public OffsetDateTime getTime() {
		return time;
	}	
}