package com.s32xlevel.dictionary.util;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

// see https://tech.yandex.ru/dictionary/doc/dg/reference/lookup-docpage/
public class YandexTranslateAPI {

    private YandexTranslateAPI() {

    }

    private static final String URL = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=dict.1.1.20190402T164729Z.a83e6e680fd2e07b.ade13450ef476ede88606f1796340fcd293b2ce6";
    private static OkHttpClient client = new OkHttpClient();

    public static List<String> translateText(String text) throws IOException {
        Request request = new Request.Builder()
                .url(URL + "&lang=ru-en&text=" + URLEncoder.encode(text, "UTF-8"))
                .build();

        String json = client.newCall(request).execute().body().string();
        List<String> translates = new ArrayList<>();
        YandexTranslateResponse.Translation response = new Gson().fromJson(json, YandexTranslateResponse.class).getDef().get(0).getTr().get(0);
        List<YandexTranslateResponse.Synonym> syn = response.getSyn();

        translates.add(response.getText());
        for (int i = 0; i < syn.size(); i++) {
            translates.add(syn.get(i).getText());
        }
        return translates;
    }

    private class YandexTranslateResponse {
        private List<YandexTranslateResponse.Definition> def;

        public class Definition {

            private String text;
            private String pos;
            private List<YandexTranslateResponse.Translation> tr;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getPos() {
                return pos;
            }

            public void setPos(String pos) {
                this.pos = pos;
            }

            public List<YandexTranslateResponse.Translation> getTr() {
                return tr;
            }

            public void setTr(List<YandexTranslateResponse.Translation> tr) {
                this.tr = tr;
            }
        }

        public class Translation {

            private String text;
            private String pos;
            private List<YandexTranslateResponse.Synonym> syn;
            private List<YandexTranslateResponse.Meaning> mean;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getPos() {
                return pos;
            }

            public void setPos(String pos) {
                this.pos = pos;
            }

            public List<YandexTranslateResponse.Synonym> getSyn() {
                return syn;
            }

            public void setSyn(List<YandexTranslateResponse.Synonym> syn) {
                this.syn = syn;
            }

            public List<YandexTranslateResponse.Meaning> getMean() {
                return mean;
            }

            public void setMean(List<YandexTranslateResponse.Meaning> mean) {
                this.mean = mean;
            }
        }

        public class Synonym {
            private String text;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }
        }

        public class Meaning {
            private String text;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }
        }

        public List<YandexTranslateResponse.Definition> getDef() {
            return def;
        }

        public void setDef(List<YandexTranslateResponse.Definition> def) {
            this.def = def;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        private int code;
    }
}
