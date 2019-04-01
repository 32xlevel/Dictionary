package com.s32xlevel.dictionary.util;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;

public class YandexTranslateAPI {

    private YandexTranslateAPI() {

    }

    private static final String URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20190323T155359Z.d61b1e6ec9c257cd.2e2e095ce68ff4fa10bafe21579e4fb1af687999";
    private static OkHttpClient client = new OkHttpClient();

    public static String[] translateText(String text) throws IOException {
        Request request = new Request.Builder()
                .url(URL + "&lang=ru-en&text=" + text)
                .build();

        String json = client.newCall(request).execute().body().string();
        return new Gson().fromJson(json, YandexTranslateResponse.class).getText();
    }

    private class YandexTranslateResponse {
        private int code;
        private String lang;
        private String[] text;

        public int getCode() {
            return code;
        }

        public String getLang() {
            return lang;
        }

        public String[] getText() {
            return text;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public void setText(String[] text) {
            this.text = text;
        }
    }
}
