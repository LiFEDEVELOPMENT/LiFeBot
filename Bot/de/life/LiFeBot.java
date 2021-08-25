package de.life;

import java.awt.Color;
import java.util.Scanner;
import java.util.UUID;

import javax.security.auth.login.LoginException;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import de.life.classes.LogMessanger;
import de.life.listener.AutotriggerListener;
import de.life.listener.ButtonListener;
import de.life.listener.CommandListener;
import de.life.listener.PrivateMessageReactionListener;
import de.life.listener.ReactionListener;
import de.life.listener.ReadyListener;
import de.life.listener.SlashListener;
import de.life.listener.VoiceListener;
import de.life.listener.ZitatListener;
import de.life.sql.SQLManager;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.apache.ApacheHttpClient;

/**
 * 
 * @author Linus Potocnik
 *
 */
public class LiFeBot {

	public static LiFeBot INSTANCE;
	public static final HypixelAPI API = new HypixelAPI(
			new ApacheHttpClient(UUID.fromString(System.getenv("APIToken"))));

	public ShardManager shardMan;
	private CommandManager cmdMan;
	public DefaultShardManagerBuilder builder;
	EventWaiter waiter;

	public static void main(String[] args) {
		try {
			new LiFeBot();
		} catch (LoginException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public LiFeBot() throws LoginException, IllegalArgumentException {
		INSTANCE = this;

		waiter = new EventWaiter();

		SQLite.connect();
		SQLManager.onCreate();

		builder = DefaultShardManagerBuilder.create(GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.DIRECT_MESSAGES,
				GatewayIntent.DIRECT_MESSAGE_REACTIONS);
		builder.disableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_BANS, GatewayIntent.GUILD_EMOJIS,
				GatewayIntent.GUILD_WEBHOOKS, GatewayIntent.GUILD_INVITES, GatewayIntent.GUILD_PRESENCES,
				GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGE_TYPING);
		builder.disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE, CacheFlag.ONLINE_STATUS);
		builder.enableCache(CacheFlag.VOICE_STATE);

		builder.setToken(System.getenv("BotToken"));
		builder.setStatus(OnlineStatus.ONLINE);

		builder.addEventListeners(new CommandListener());
		builder.addEventListeners(new VoiceListener());
		builder.addEventListeners(new ReactionListener());
		builder.addEventListeners(new AutotriggerListener());
		builder.addEventListeners(new PrivateMessageReactionListener());
		builder.addEventListeners(new ReadyListener());
		builder.addEventListeners(new ZitatListener());
		builder.addEventListeners(new SlashListener());
		builder.addEventListeners(new ButtonListener());
		builder.addEventListeners(waiter);
		builder.setActivity(Activity.playing("invite.lifebot.life | !commands"));

		shardMan = builder.build();

		this.cmdMan = new CommandManager();

		shutdown();
	}

	public JDA getJDA() {
		return shardMan.getShards().get(0);
	}

	public ShardManager getShardMan() {
		return shardMan;
	}

	public void shutdown() {
		Scanner sc = new Scanner(System.in);
		try {
			while (sc.hasNextLine()) {
				String input = sc.nextLine();
				if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("stop")
						|| input.equalsIgnoreCase("shutdown")) {

					for (Guild g : getJDA().getGuilds()) {
						LogMessanger.sendLog(g.getIdLong(), "Bot Status", "OFFLINE!", Color.RED);
					}

					if (shardMan != null) {
						shardMan.setStatus(OnlineStatus.OFFLINE);
						shardMan.shutdown();
						SQLite.disconnect();
						System.out.println("Bot offline.");
					}
					sc.close();
				}
			}
		} catch (IllegalStateException e) {
		}
	}

	public CommandManager getCmdMan() {
		return cmdMan;
	}

	public EventWaiter getWaiter() {
		return waiter;
	}
}