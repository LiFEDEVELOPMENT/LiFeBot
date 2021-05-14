package de.life.classes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;

import de.life.LiFeBot;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Role;

public class CategoryFinder {

	final static JDA jda = LiFeBot.INSTANCE.getJDA();

	public static Category getReportCategory(Long guildid) {

		Long catID = null;
		Category cat = null;
		Role everyone = jda.getGuildById(guildid).getRoles().get((jda.getGuildById(guildid).getRoles().size()) - 1);
		ResultSet set = SQLite
				.onQuery("SELECT * FROM categories WHERE guildid = '" + guildid + "' AND type = 'report'");

		try {
			if (set.next())
				catID = set.getLong("categoryid");
		} catch (SQLException e) {
		}

		if (catID == null || jda.getCategoryById(catID) == null) {
			SQLite.onUpdate("DELETE FROM categories WHERE categoryid = '" + catID + "'");
			cat = jda.getCategoryById(jda.getGuildById(guildid).createCategory("Reports").complete().getIdLong());
			cat.getManager()
					.putPermissionOverride(everyone, null, EnumSet.of(Permission.VIEW_CHANNEL,
							Permission.MANAGE_CHANNEL, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_WEBHOOKS,
							Permission.CREATE_INSTANT_INVITE, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS,
							Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_ADD_REACTION,
							Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_MENTION_EVERYONE,
							Permission.MESSAGE_MANAGE, Permission.MESSAGE_HISTORY, Permission.MESSAGE_TTS,
							Permission.VOICE_STREAM, Permission.VOICE_USE_VAD, Permission.PRIORITY_SPEAKER,
							Permission.VOICE_MUTE_OTHERS, Permission.VOICE_DEAF_OTHERS, Permission.VOICE_MOVE_OTHERS))
					.queue();
			SQLite.onUpdate("INSERT INTO categories (guildid, categoryid, type) VALUES ('" + guildid + "', '"
					+ cat.getIdLong() + "', 'report')");
		} else {
			cat = jda.getCategoryById(catID);
		}

		return cat;
	}
}