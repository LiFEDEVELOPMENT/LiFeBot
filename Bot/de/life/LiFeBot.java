package de.life;

import java.util.Scanner;

import javax.security.auth.login.LoginException;

import de.life.listener.AutotriggerListener;
import de.life.listener.CommandListener;
import de.life.listener.PrivateMessageReactionListener;
import de.life.listener.ReactionListener;
import de.life.listener.ReadyListener;
import de.life.listener.VoiceListener;
import de.life.listener.ZitatListener;
import de.life.sql.SQLManager;
import de.life.sql.SQLite;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class LiFeBot {

	public static LiFeBot INSTANCE;

	public ShardManager shardMan;
	private CommandManager cmdMan;
	public DefaultShardManagerBuilder builder;

	public static void main(String[] args) {

		try {
			new LiFeBot();
		} catch (LoginException | IllegalArgumentException e) {
			e.printStackTrace();
		}

	}

	public LiFeBot() throws LoginException, IllegalArgumentException {
		INSTANCE = this;
		this.cmdMan = new CommandManager();

		SQLite.connect();
		SQLManager.onCreate();

		builder = DefaultShardManagerBuilder.create(GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.DIRECT_MESSAGES,
				GatewayIntent.DIRECT_MESSAGE_REACTIONS);
		builder.disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS);
		builder.enableCache(CacheFlag.VOICE_STATE);

		builder.setToken(GlobalVariables.botToken);
		builder.setStatus(OnlineStatus.ONLINE);

		builder.addEventListeners(new CommandListener());
		builder.addEventListeners(new VoiceListener());
		builder.addEventListeners(new ReactionListener());
		builder.addEventListeners(new AutotriggerListener());
		builder.addEventListeners(new PrivateMessageReactionListener());
		builder.addEventListeners(new ReadyListener());
		builder.addEventListeners(new ZitatListener());

		shardMan = builder.build();

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
}