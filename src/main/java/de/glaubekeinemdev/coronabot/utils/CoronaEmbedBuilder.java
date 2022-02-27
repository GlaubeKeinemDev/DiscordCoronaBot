package de.glaubekeinemdev.coronabot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CoronaEmbedBuilder {

    private final EmbedBuilder embedBuilder;

    public CoronaEmbedBuilder() {
        this.embedBuilder = new EmbedBuilder();
    }

    public CoronaEmbedBuilder(final String title) {
        this.embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);
    }

    public CoronaEmbedBuilder setTitle(String s) {
        this.embedBuilder.setTitle(s);
        return this;
    }

    public CoronaEmbedBuilder setFooter(final String footer) {
        embedBuilder.setFooter(footer);
        return this;
    }

    public CoronaEmbedBuilder setDescription(String s) {
        embedBuilder.setDescription(s);
        return this;
    }

    public CoronaEmbedBuilder setDefaultFooter(final Member member) {
        this.embedBuilder.setFooter(member.getUser().getAsTag() + " | " + getCurrentDate());
        this.embedBuilder.setThumbnail(member.getUser().getAvatarUrl());
        return this;
    }

    public CoronaEmbedBuilder setAuthor(final String name, final String url, final String iconUrl) {
        this.embedBuilder.setAuthor(name, url, iconUrl);
        return this;
    }

    public CoronaEmbedBuilder setImageUrl(final String url) {
        this.embedBuilder.setImage(url);
        return this;
    }

    public CoronaEmbedBuilder addField(final String name, final String value) {
        this.embedBuilder.addField(name, value, false);
        return this;
    }

    public CoronaEmbedBuilder addField(final String name, final String value, final boolean inline) {
        this.embedBuilder.addField(name, value, inline);
        return this;
    }

    public CoronaEmbedBuilder setColor(final Color color) {
        this.embedBuilder.setColor(color);
        return this;
    }

    public CoronaEmbedBuilder setThumbnail(final String url) {
        this.embedBuilder.setThumbnail(url);
        return this;
    }

    public MessageEmbed build() {
        return this.embedBuilder.build();
    }

    public String getCurrentDate() {
        return new SimpleDateFormat("dd.MM.yyyy - kk:mm").format(new Date());
    }

}
