package de.life;

import java.util.concurrent.ConcurrentHashMap;

import de.life.commands.AnnounceCommand;
import de.life.commands.AutotriggerCommand;
import de.life.commands.BanCommand;
import de.life.commands.BotNewsCommand;
import de.life.commands.ClearCommand;
import de.life.commands.CoinflipCommand;
import de.life.commands.ColorCommand;
import de.life.commands.CommandsCommand;
import de.life.commands.HubsCommand;
import de.life.commands.IconUpdateCommand;
import de.life.commands.KickCommand;
import de.life.commands.ListGuildsCommand;
import de.life.commands.LogsCommand;
import de.life.commands.MemberInfoCommand;
import de.life.commands.MemesCommand;
import de.life.commands.PingCommand;
import de.life.commands.PollCommand;
import de.life.commands.PrivateChannelCommand;
import de.life.commands.RPSHerausfordernCommand;
import de.life.commands.ReactCommand;
import de.life.commands.RolesCommand;
import de.life.commands.SQLCommand;
import de.life.commands.TTTHerausfordernCommand;
import de.life.commands.ZitateCommand;
import de.life.interfaces.ServerCommand;
import de.life.music.commands.PauseCommand;
import de.life.music.commands.PlayCommand;
import de.life.music.commands.QueueCommand;
import de.life.music.commands.ResumeCommand;
import de.life.music.commands.ShuffleCommand;
import de.life.music.commands.SkipCommand;
import de.life.music.commands.StopCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class CommandManager {

	private final ConcurrentHashMap<String, ServerCommand> commands;

	public CommandManager() {
		this.commands = new ConcurrentHashMap<>();

		this.commands.put("announce", new AnnounceCommand());
		this.commands.put("trigger", new AutotriggerCommand());
		this.commands.put("triggers", new AutotriggerCommand());
		this.commands.put("autotrigger", new AutotriggerCommand());
		this.commands.put("autotriggers", new AutotriggerCommand());
		this.commands.put("ban", new BanCommand());
		this.commands.put("bicmac", new BanCommand());
		this.commands.put("botnews", new BotNewsCommand());
		this.commands.put("clear", new ClearCommand());
		this.commands.put("c", new ClearCommand());
		this.commands.put("coinflip", new CoinflipCommand());
		this.commands.put("flip", new CoinflipCommand());
		this.commands.put("cf", new CoinflipCommand());
		this.commands.put("commands", new CommandsCommand());
		this.commands.put("help", new CommandsCommand());
		this.commands.put("color", new ColorCommand());
		this.commands.put("hub", new HubsCommand());
		this.commands.put("hubs", new HubsCommand());
		this.commands.put("icon", new IconUpdateCommand());
		this.commands.put("kick", new KickCommand());
		this.commands.put("guilds", new ListGuildsCommand());
		this.commands.put("log", new LogsCommand());
		this.commands.put("logs", new LogsCommand());
		this.commands.put("memberinfo", new MemberInfoCommand());
		this.commands.put("userinfo", new MemberInfoCommand());
		this.commands.put("meme", new MemesCommand());
		this.commands.put("memes", new MemesCommand());
		this.commands.put("pause", new PauseCommand());
		this.commands.put("play", new PlayCommand());
		this.commands.put("ping", new PingCommand());
		this.commands.put("poll", new PollCommand());
		this.commands.put("pv", new PrivateChannelCommand());
		this.commands.put("pvc", new PrivateChannelCommand());
		this.commands.put("privatevoice", new PrivateChannelCommand());
		this.commands.put("privatechannel", new PrivateChannelCommand());
		this.commands.put("privatevoicechannel", new PrivateChannelCommand());
		this.commands.put("q", new QueueCommand());
		this.commands.put("queue", new QueueCommand());
		this.commands.put("react", new ReactCommand());
		this.commands.put("resume", new ResumeCommand());
		this.commands.put("rockpaperscissors", new RPSHerausfordernCommand());
		this.commands.put("role", new RolesCommand());
		this.commands.put("roles", new RolesCommand());
		this.commands.put("rps", new RPSHerausfordernCommand());
//		this.commands.put("settings", new SettingsCommand());
		this.commands.put("shuffle", new ShuffleCommand());
		this.commands.put("next", new SkipCommand());
		this.commands.put("skip", new SkipCommand());
		this.commands.put("sql", new SQLCommand());
		this.commands.put("leave", new StopCommand());
		this.commands.put("stop", new StopCommand());
		this.commands.put("ttt", new TTTHerausfordernCommand());
//		this.commands.put("warn", new WarnCommand());
//		this.commands.put("warns", new WarnCommand());
//		this.commands.put("warning", new WarnCommand());
//		this.commands.put("warnings", new WarnCommand());
		this.commands.put("zitat", new ZitateCommand());
		this.commands.put("zitate", new ZitateCommand());
	}

	public void perform(String command, Member m, MessageChannel channel, Message message) {
		ServerCommand cmd;
		if ((cmd = this.commands.get(command.toLowerCase())) != null)
			cmd.performCommand(m, channel, message);
	}
}