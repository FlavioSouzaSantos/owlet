package br.com.flavio.owlet.model;

import com.google.gson.annotations.SerializedName;

import java.net.URI;

public record DiscordWebhookMessage(String username, @SerializedName("avatar_url") URI avatarUrl, String content) {
}
