package dev.Andorvini;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;

public class Main {
    public static void main(String[] args) {

        // Variable Declaration
        String token = null;

        // Configuration File Load
        try {
            FileInputStream fileInputStream = new FileInputStream("src\\main\\resources\\configuration\\config.properties");
            Properties property = new Properties();

            property.load(fileInputStream);

            token = property.getProperty("bot_token");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Bot creation
        TelegramBot bot = new TelegramBot(token);

        // SQLite DB connection
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:src\\main\\resources\\database\\StonksBotDataBase.db");
        } catch (Exception e) {
            System.out.println("Couldnt connect to DB" + e.getMessage());
            e.printStackTrace();
        }

        // Inline Keyboards
        InlineKeyboardMarkup continueKeyboard = new InlineKeyboardMarkup(
            new InlineKeyboardButton("Продолжить").callbackData("continue")
        );

        // Bot Update Listener
        bot.setUpdatesListener(updates -> {

            for (Update update : updates) {

                // ChatId
                long chatId = 0;
                if (update.message() != null) {
                    chatId = update.message().chat().id();
                } else if (update.callbackQuery() != null) {
                    chatId = update.callbackQuery().message().chat().id();
                }

                // Slash commands
                if (update.message() != null) {
                    if (update.message().text().equals("/start")) {
                        bot.execute(new SendMessage(chatId, "Добро пожаловать в \uD83D\uDCB0StonksBot\uD83D\uDCB0").replyMarkup(continueKeyboard));
                    }
                }

                // Inline Callbacks
                if (update.callbackQuery() != null) {
                    if (update.callbackQuery().data().equals("continue")) {
                        bot.execute(new SendMessage(chatId, "Вашей основной задачей станет: \n<b>ПОДЧИНЕНИЕ МТЕЧ</b> \n<s>ДЛЯ ТОГО ЧТОБЫ ДЛИНК ПОЛНОСТЬЮ ПОДЧИНИЛО ЭТОТ МИР</s>").parseMode(HTML));
                    }
                }

            }

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

    }
}