package ru.jbrain.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Bot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(Bot.class);
    private static final String BOT_TOKEN = System.getenv("TOKEN");

    public Bot() {
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId()).setParseMode(ParseMode.HTML);
        String text = update.getMessage().getText();
        if (text.equalsIgnoreCase("/start")) {
            message.setText("Введите термин который вас интересует");
        } else {
            try {
                message.setText(searchInFile(text));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getLocalizedMessage());
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
        log.debug("Ищем в файле");
        StringBuilder builder = new StringBuilder();
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream(text.toLowerCase() + ".txt");
        if (resourceAsStream != null) {
            log.debug("Считываем текст из файла");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream,"utf-8"));
            while (reader.ready()) {
                builder.append(reader.readLine()).append("\n");
            }
            return builder.toString();
        }
        return "Нет информации по данному термину";
    }
}
