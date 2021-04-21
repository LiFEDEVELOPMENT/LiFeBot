package de.life.music.commands;

import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.apache.hc.core5.http.ParseException;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.IPlaylistItem;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;

import de.life.classes.EmbedMessageBuilder;
import de.life.interfaces.ServerCommand;
import de.life.music.MusicUtil;
import de.life.music.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class PlayCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, MessageChannel channel, Message message) {
		message.delete().queue();
		addQueue(m, channel, message.getContentDisplay());
	}

	public static void addQueue(Member m, MessageChannel channel, String message) {
		String[] args = message.split(" ");

		if (!joinChannel(m)) {
			return;
		}

		args = Arrays.copyOfRange(args, 1, args.length);
		String link = String.join(" ", args);

		if (!isUrl(link) && args.length <= 2) {
			EmbedMessageBuilder.sendMessage("Musik",
					"Zu dieser Suche habe ich leider nichts gefunden - Gib mir bitte noch ein Wort :)", Color.RED,
					MusicUtil.getMusicChannel(m.getGuild()), 10);
			return;
		}

		if (link.startsWith("https://open.spotify.com/track/")) {
			String[] temp = args[0].split("track");
			String[] temp2 = temp[1].split("si");
			String id = temp2[0].substring(1, temp2[0].length() - 1);

			link = trackInfo(id);
		}

		if (link.startsWith("https://open.spotify.com/playlist/")) {
			String[] temp = args[0].split("playlist");
			String[] temp2 = temp[1].split("si");
			String id = temp2[0].substring(1, temp2[0].length() - 1);

			playlistInfo(id, m, channel);
		}

		if (!isUrl(link)) {
			link = "ytsearch:" + link;
		}

		PlayerManager.getInstance().loadAndPlay(channel, link, m);
		MusicUtil.updateChannel(m, channel);
	}

	private static boolean joinChannel(Member m) {

		if (!m.getVoiceState().inVoiceChannel()) {
			return false;
		}

		if (m.getGuild().getSelfMember().getVoiceState().inVoiceChannel()
				&& m.getGuild().getSelfMember().getVoiceState().getChannel() != m.getVoiceState().getChannel()) {
			return false;
		}

		m.getGuild().getAudioManager().openAudioConnection(m.getVoiceState().getChannel());
		return true;
	}

	private static boolean isUrl(String url) {
		try {
			new URI(url);
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}

	static SpotifyApi spotifyApi = new SpotifyApi.Builder().setAccessToken(
			"BQBvjtU4RJBg9eLZD3aMB1YrooxQJZWxFrJQh3IWbizd0J7AQpagYHxo7ZAME_i23DFMM4sZo1_VRWvZJhmTRnoLxd5DVcOMRpmUnLVv5RvT5Kh7xuftireSdPmpurAjRZoRkirkVjoWWSZXl_QPu3RfU3F_C6Y")
			.build();

	private static String trackInfo(String id) {

		String link = "";
		GetTrackRequest getTrackRequest = spotifyApi.getTrack(id).build();
		String songName = "";
		String artistNames = "";

		try {

			Track track = getTrackRequest.execute();

			songName = track.getName();
			ArtistSimplified[] artists = track.getArtists();
			for (ArtistSimplified artist : artists) {
				artistNames.concat(artist.getName() + " ");
			}

		} catch (IOException | SpotifyWebApiException | ParseException e) {
			System.out.println("Error: " + e.getMessage());
		}

		link = songName + " " + artistNames;

		return link;
	}

	private static void playlistInfo(String id, Member m, MessageChannel channel) {
		String songName = "";
		String artistNames = "";

		GetPlaylistsItemsRequest getPlaylistsItemsRequest = spotifyApi.getPlaylistsItems(id).build();

		try {
			PlaylistTrack[] tracks = getPlaylistsItemsRequest.execute().getItems();

			for (PlaylistTrack track : tracks) {
				GetTrackRequest getTrackRequest = spotifyApi.getTrack(track.getTrack().getId()).build();
				
				Track currTrack = getTrackRequest.execute();
				songName = currTrack.getName();
				ArtistSimplified[] artists = currTrack.getArtists();
				for (ArtistSimplified artist : artists) {
					artistNames.concat(artist.getName() + " ");
				} 
				
				PlayerManager.getInstance().loadAndPlay(channel, "sytsearch: " + songName + " " + artistNames, m);
				MusicUtil.updateChannel(m, channel);
			}
			EmbedMessageBuilder.sendMessage("Musik", "Es wurden " + tracks.length + " Songs aus " PLIST + " zur Queue hinzugefügt!", Color.ORANGE, channel);
		} catch (IOException | SpotifyWebApiException | ParseException e) {
			e.printStackTrace();
		}
	}

}