package com.example.weather.telegabot;

import com.example.weather.models.Message;
import com.example.weather.service.ExchangeService;
import com.example.weather.service.MessageService;
import com.example.weather.service.SpeechToTextService;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;
    private final ExchangeService exchangeService;
    private final MessageService messageService;
    private final SpeechToTextService speechToTextService;

    static final String HELP_TEXT = "Приветствую тебя в боте SDU.\n\n" +
            "Вот список комманд:\n\n" +
            "Команда /start начать бота\n\n" +
            "Команда /exchange актуальные курсы\n\n" +
            "Для конвертации можете написать в формате {кол-во валюта}\n\n" +
            "Команда /help помощник";

    static final String ERROR_TEXT = "Error occurred: ";



    public TelegramBot(BotConfig config, ExchangeService service, MessageService messageService, SpeechToTextService speechToTextService) {
        this.config = config;
        this.exchangeService = service;
        this.messageService = messageService;
        this.speechToTextService = speechToTextService;

        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "get a welcome message"));
        listofCommands.add(new BotCommand("/exchange", "get exchange"));
        listofCommands.add(new BotCommand("/help", "info how to use this bot"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            //сохранение сообщения в базу данных
            saveMessage(messageText, chatId);

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                case "/help":
                    prepareAndSendMessage(chatId, HELP_TEXT);
                    break;

                case "/exchange":
                    prepareAndSendMessage(chatId, exchangeService.getAllExchange());
                    break;

                default:
                    if(messageText.contains("$")){
                        prepareAndSendMessage(chatId, exchangeService.convertUSDToKZT(Double.parseDouble(messageText.split(" ")[0])));
                    }else if(messageText.contains("тенге")){
                        prepareAndSendMessage(chatId, exchangeService.convertKZTToUSD(Double.parseDouble(messageText.split(" ")[0])));

                    }
            }
        }else if (update.hasMessage() && update.getMessage().hasAudio()){
            System.out.println("********************");
            long chatId = update.getMessage().getChatId();
            // Получение аудиофайла из сообщения
            Voice audio = update.getMessage().getVoice();

            // Выполнение преобразования голоса в текст
            try {
                File voiceFile = downloadVoiceFile(audio);

                String text = speechToTextService.convertVoiceToText(voiceFile);

                // Отправка текстового сообщения обратно в чат
                prepareAndSendMessage(chatId, "Ваше сообщение: " + text);

                //сохранение сообщения в базу данных
                saveMessage(text, chatId);


            } catch (Exception e) {
                // Обработка ошибок
                e.printStackTrace();
            }
        }
    }

    private File downloadVoiceFile(org.telegram.telegrambots.meta.api.objects.Voice voiceMessage) {
        // Скачивание голосового файла
        try {
            GetFile getFile = new GetFile();
            getFile.setFileId(voiceMessage.getFileId());
            return execute(getFile);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!" + " :)";
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        executeMessage(message);
    }


    private void executeMessage(SendMessage message){
        try {
            execute(message);
        } catch (TelegramApiException e) {

        }
    }

    private void prepareAndSendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    private void saveMessage(String messageText, long chatId){
        Message message = new Message();
        //сохранение сообщения в базу данных
        message.setChat_id(chatId);
        message.setText(messageText);
        messageService.saveMessage(message);
    }
}

