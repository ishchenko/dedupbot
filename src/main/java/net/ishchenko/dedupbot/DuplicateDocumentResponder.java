package net.ishchenko.dedupbot;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

public class DuplicateDocumentResponder implements Function<Update, Stream<SendMessage>> {

    private static final Logger log = LoggerFactory.getLogger(DuplicateDocumentResponder.class);

    private final IUpdateStorage storage;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM HH:mm").withLocale(Locale.forLanguageTag("ru-RU"));

    public DuplicateDocumentResponder(IUpdateStorage storage) {
        this.storage = storage;
    }

    @Override
    public Stream<SendMessage> apply(Update update) {
        Update oldUpdate = null;
        try {
            oldUpdate = storage.save(update);
        } catch (IOException e) {
            log.error("Could not save update", e);
        }
        if (oldUpdate != null) {
            String text = String.format(
                    "Эта картинка уже была, постил @%s в %s",
                    oldUpdate.message().from().username(),
                    formatter.format(Instant.ofEpochSecond(oldUpdate.message().date()).atZone(ZoneId.systemDefault()))
            );
            SendMessage message = new SendMessage(update.message().chat().id(), text).replyToMessageId(update.message().messageId());
            return Stream.of(message);
        }
        return Stream.empty();
    }

}
