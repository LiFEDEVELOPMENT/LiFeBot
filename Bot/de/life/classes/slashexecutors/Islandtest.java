package de.life.classes.slashexecutors;

import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.life.LiFeBot;
import net.hypixel.api.reply.skyblock.SkyBlockProfileReply;

public class Islandtest {

	public Islandtest() {
		LiFeBot.API.getSkyBlockProfiles("1234").whenComplete((reply, error) -> {
			if (error != null) {
				error.printStackTrace();
				System.exit(0);
				return;
			}

			// Get all of the player's profiles.
			JsonObject profiles = reply.getProfiles().getAsJsonObject();
			if (profiles == null || profiles.entrySet().isEmpty()) {
				System.out.println("Player has no SkyBlock profiles");
				System.exit(0);
				return;
			}

			// Request each profile from the API & print the reply.
			Set<Entry<String, JsonElement>> profileEntries = profiles.entrySet();
			CompletableFuture<?>[] requests = new CompletableFuture<?>[profileEntries.size()];
			int i = 0;
			for (Entry<String, JsonElement> profile : profileEntries) {
				requests[i] = requestProfile(profile.getKey());
				i++;
			}

			// Only exit once all requests are completed.
			CompletableFuture.allOf(requests).whenComplete((ignored, profileError) -> {
				if (profileError != null) {
					profileError.printStackTrace();
				}
				System.exit(0);
			});
		});
	}

	private static CompletableFuture<SkyBlockProfileReply> requestProfile(String profileId) {
		return LiFeBot.API.getSkyBlockProfile(profileId).whenComplete((profileReply, ex) -> {
			if (ex != null) {
				ex.printStackTrace();
				return;
			}

			System.out.println(profileReply);
		});
	}
}
