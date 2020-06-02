package ru.jbrain.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.meta.generics.WebhookBot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {
    private StringBuilder builder = new StringBuilder();
    private static final String BOT_TOKEN = System.getenv("TOKEN");

    public Bot() {
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId());
        String text = update.getMessage().getText();
        if (text.equalsIgnoreCase("/start")) {
            message.setText("Введите термин который вас интересует: \n" + builder.toString()).setParseMode(ParseMode.HTML);
        } else {
            try {
                message.setText(searchInFile(text)).setParseMode(ParseMode.HTML);
                execute(message);
            } catch (IOException | TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public String getBotUsername() {
        return "@JbrainInterviewBot";
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }


    private String searchInFile(String text) throws IOException {
        boolean isThat = false;
        StringBuilder builder = new StringBuilder();
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream(text.toLowerCase() + ".txt");
        if (resourceAsStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream,"utf-8"));
            while (reader.ready()) {
                builder.append(reader.readLine()).append("\n");
            }
            return builder.toString();
        }
        return "Нет информации по данному термину";
    }
}
