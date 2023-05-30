package com.example.weather.service;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class SpeechToTextService {


    public String convertVoiceToText(File voiceFile) {
        // Преобразование голосового файла в текст с использованием Google Cloud Speech-to-Text

        // Создание клиента Google Cloud Speech-to-Text
        try (SpeechClient speechClient = SpeechClient.create()) {
            // Загрузка голосового файла
            Path path = Paths.get(voiceFile.getFilePath());
            byte[] audioData = Files.readAllBytes(path);
            ByteString audioBytes = ByteString.copyFrom(audioData);

            // Конфигурация распознавания речи
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("ru-RU")
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

            // Запрос на распознавание речи
            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            StringBuilder transcriptBuilder = new StringBuilder();
            for (SpeechRecognitionResult result : results) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                String transcript = alternative.getTranscript();
                transcriptBuilder.append(transcript);
            }

            return transcriptBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}