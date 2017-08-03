package net.ishchenko.dedupbot;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.TreeMap;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        TelegramBot bot = TelegramBotAdapter.build(args[0]);

        bot.setUpdatesListener(updates -> {
            log.info("Recieved {} updates", updates.size());
            updates.stream()
                    .filter(u -> u.message() != null && u.message().document() != null)
                    .flatMap(new DuplicateDocumentResponder(new FileUpdateStorage("updates")))
                    .forEach(m -> bot.execute(m, newLoggingCallBack()));

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

    }

    private static Callback<SendMessage, SendResponse> newLoggingCallBack() {
        return new Callback<SendMessage, SendResponse>() {
            @Override
            public void onResponse(SendMessage request, SendResponse response) {
                log.info(
                        "Sent message ok: {}, errorCode: {}, desc: {}, request parameters: {}",
                        response.isOk(),
                        response.errorCode(),
                        response.description(),
                        new TreeMap<>(request.getParameters())
                );
            }

            @Override
            public void onFailure(SendMessage request, IOException e) {
                log.warn("Could not send message with parameters" + new TreeMap<>(request.getParameters()), e);
            }
        };
    }

}
