package dev.Andorvini;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.FileInputStream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.Properties;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;

public class Main {
    public static void main(String[] args) {

        // Variable Declaration
        String token = null;

        Connection connection = null;

        // Configuration File Load
        try {
            FileInputStream fileInputStream = new FileInputStream("config.properties");
            Properties property = new Properties();

            property.load(fileInputStream);

            token = property.getProperty("bot_token");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        // Bot creation
        TelegramBot bot = new TelegramBot(token);

        // SQLite DB connection
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:StonksBotDataBase.db");
        } catch (Exception e) {
            System.out.println("Couldnt connect to DB" + e.getMessage());
            e.printStackTrace();
        }

        // DataBase Initialization
        try {
            Statement statement = connection.createStatement();
            statement.closeOnCompletion();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //Inline Keyboards
        InlineKeyboardMarkup continueKeyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton("⏩Продолжить⏩").callbackData("continue")
        );

        InlineKeyboardMarkup continueKeyboard2 = new InlineKeyboardMarkup(
                new InlineKeyboardButton("⏩Продолжить⏩").callbackData("continue2")
        );

        InlineKeyboardMarkup continueKeyboard3 = new InlineKeyboardMarkup(
                new InlineKeyboardButton("⏩Продолжить⏩").callbackData("continue3")
        );

        InlineKeyboardMarkup startKeyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton("✅Начать✅").callbackData("start")
        );

        // Basic Keyboards
        Keyboard mainMenu = new ReplyKeyboardMarkup(
                new KeyboardButton[]{
                        new KeyboardButton("\uD83C\uDFE6Банк\uD83C\uDFE6"),
                        new KeyboardButton("\uD83D\uDCB2Баланс\uD83D\uDCB2")
                },
                new KeyboardButton[]{
                        new KeyboardButton(""),
                        new KeyboardButton("")
                }
        );

        // Bot Update Listener
        Connection finalConnection = connection;
        bot.setUpdatesListener(updates -> {

            for (Update update : updates) {

                // ChatId
                long chatId = 0;
                if (update.message() != null) {
                    chatId = update.message().chat().id();
                } else if (update.callbackQuery() != null) {
                    chatId = update.callbackQuery().message().chat().id();
                }

                // Adding user to DB
                try {
                    Statement statement = finalConnection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM stonksBot WHERE chatid = " + chatId + ";");

                    if (resultSet.getInt("chatID") == 0) {
                        statement.executeUpdate("INSERT INTO stonksBot(chatId,tutorialCompleted,money) VALUES ('" + chatId + "','false','1000');");
                        statement.closeOnCompletion();
                    } else {
                        statement.close();
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }

                // Slash commands
                if (update.message() != null) {

                    if (update.message().text().equals("/start")) {
                        bot.execute(new SendMessage(chatId, "Добро пожаловать в \uD83D\uDCB0StonksBot\uD83D\uDCB0")
                                .replyMarkup(continueKeyboard));
                    }

                }

                // Inline Callbacks
                if (update.callbackQuery() != null) {

                    if (update.callbackQuery().data().equals("continue")) {
                        bot.execute(new SendMessage(chatId, "Вашей основной задачей станет: \n<b>             ПОДЧИНЕНИЕ МТЕЧ</b>")
                                .parseMode(HTML)
                                .replyMarkup(continueKeyboard2));
                    }

                    if (update.callbackQuery().data().equals("continue2")) {
                        bot.execute(new SendMessage(chatId, "Данная цель будет достигаться самыми различными способами,от банального подкупа политиков и топ-менеджеров до их <s>УНИЧТОЖЕНИЯ</s> обезвреживания")
                                .replyMarkup(continueKeyboard3)
                                .parseMode(HTML));
                    }

                    if (update.callbackQuery().data().equals("continue3")) {
                        bot.execute(new SendMessage(chatId, "\uD83C\uDF89Поздравляем с прохожденем вступительной части!\uD83C\uDF89" +
                                "\nПриступайте к выполнению своей священной цели")
                                .replyMarkup(startKeyboard));
                        try {
                            Statement statement = finalConnection.createStatement();

                            statement.executeUpdate("UPDATE stonksBot SET tutorialCompleted = 'true' WHERE chatId = " + chatId +";");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

            }

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

    }
}