package net.ishchenko.dedupbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pengrad.telegrambot.model.Update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUpdateStorage implements IUpdateStorage {

    private final String updatesDir;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public FileUpdateStorage(String updatesDir) {
        this.updatesDir = updatesDir;
    }

    @Override
    public Update save(Update update) throws IOException {
        String filename = update.message().document().fileId() + ".json";
        File outputDir = new File(updatesDir, update.message().chat().id() + "");
        File outputFile = new File(outputDir, filename);
        Update existingUpdate = null;
        if (outputFile.exists()) {
            try (FileInputStream in = new FileInputStream(outputFile)) {
                try (InputStreamReader reader = new InputStreamReader(in, "UTF-8")) {
                    existingUpdate = gson.fromJson(reader, Update.class);
                }
            }
        }
        outputFile.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            try (OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8")) {
                gson.toJson(update, osw);
            }
        }
        return existingUpdate;
    }

}
