package net.ishchenko.dedupbot;

import com.pengrad.telegrambot.model.Update;

import java.io.IOException;

public interface IUpdateStorage {
    Update save(Update update) throws IOException;
}

