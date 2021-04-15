package de.life;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.security.auth.login.LoginException;

import de.life.classes.RPSManager;
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
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class LiFeBot {

	public static LiFeBot INSTANCE;

	public ShardManager shardMan;
	private CommandManager cmdMan;
	private Thread loop;
	Calendar cal = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY - HH:mm-ss");
	public DefaultShardManagerBuilder builder;

	public static void main(String[] args) {

		try {
			new LiFeBot();
			new RPSManager();
		} catch (LoginException | IllegalArgumentException e) {
			e.printStackTrace();
		}

	}

	public LiFeBot() throws LoginException, IllegalArgumentException {
		INSTANCE = this;

		SQLite.connect();
		SQLManager.onCreate();

		builder = DefaultShardManagerBuilder.create(GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.DIRECT_MESSAGES,
				GatewayIntent.DIRECT_MESSAGE_REACTIONS);
		builder.disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS);
		builder.enableCache(CacheFlag.VOICE_STATE);
		builder.setToken(GlobalVariables.botToken);

		builder.setStatus(OnlineStatus.ONLINE);

		this.cmdMan = new CommandManager();

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

		new Thread(() -> {

			String line = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			try {
				while ((line = reader.readLine()) != null) {

					if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("stop")
							|| line.equalsIgnoreCase("shutdown")) {
						if (shardMan != null) {

							shardMan.setStatus(OnlineStatus.OFFLINE);
							shardMan.shutdown();

							SQLite.disconnect();

							System.out.println("Bot offline.");
						}

						if (loop != null) {
							loop.interrupt();
						}

						reader.close();
					} else {
						System.out.println("Use 'exit', 'stop' or 'shutdown' to shut down.");
					}

				}
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}).start();

	}

	public CommandManager getCmdMan() {
		return cmdMan;
	}

}