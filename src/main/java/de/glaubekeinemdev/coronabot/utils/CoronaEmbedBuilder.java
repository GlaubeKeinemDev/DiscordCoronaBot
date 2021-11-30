package de.glaubekeinemdev.coronabot.utils;

import de.glaubekeinemdev.discordutilities.utils.AbstractEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CoronaEmbedBuilder extends AbstractEmbedBuilder {

    public CoronaEmbedBuilder() {
    }

    public CoronaEmbedBuilder(final String title) {
        this.embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);
        embedBuilder.setColor(Color.decode("#900C3F"));
    }

    @Override
    public AbstractEmbedBuilder setTitle(String s) {
        this.embedBuilder.setTitle(s);
        return this;
    }

    public CoronaEmbedBuilder setFooter(final String footer) {
        embedBuilder.setFooter(footer);
        return this;
    }

    @Override
    public AbstractEmbedBuilder setDescription(String s) {
        embedBuilder.setDescription(s);
        return this;
    }

    @Override
    public AbstractEmbedBuilder setDefaultFooter(final Member member) {
        this.embedBuilder.setFooter(member.getUser().getAsTag() + " | " + getCurrentDate());
        this.embedBuilder.setThumbnail(member.getUser().getAvatarUrl());
        return this;
    }

    @Override
    public AbstractEmbedBuilder setImageUrl(final String url) {
        this.embedBuilder.setImage(url);
        return this;
    }

    @Override
    public AbstractEmbedBuilder addField(final String name, final String value) {
        this.embedBuilder.addField(name, value, false);
        return this;
    }

    @Override
    public AbstractEmbedBuilder setColor(final Color color) {
        this.embedBuilder.setColor(color);
        return this;
    }

    @Override
    public AbstractEmbedBuilder setThumbnail(final String url) {
        this.embedBuilder.setThumbnail(url);
        return this;
    }

    public String getCurrentDate() {
        return new SimpleDateFormat("dd.MM.yyyy - kk:mm").format(new Date());
    }

}
