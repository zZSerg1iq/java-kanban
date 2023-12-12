package http.client;

import excepton.KVExchangeException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class KVTaskClient {

    private final String token;
    private final String url;

    public KVTaskClient(String url) {
        this.url = url;
        token = getToken();
    }

    private String getToken() {
        String regUrl = url + "/register";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(regUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new KVExchangeException("Регистрация не удалась. Сервер возвращает: " + response.statusCode());
            }
            return response.body();

        } catch (IOException | InterruptedException e) {
            throw new KVExchangeException("При получении токена возникло исключение: " + e);
        }
    }


    public void put(String key, String json) {
        String regUrl = url + "/save/" + key + "?API_TOKEN=" + token;
        //System.out.println("Saving key '" + key + "' value: " + json);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(regUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 201) {
                throw new KVExchangeException("Сохранение не удалось. Сервер вернул: " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            throw new KVExchangeException("При сохранении данных на сервер возникло исключение: " + e);
        }
    }


    public String load(String key) {
        String loadUrl = url + "/load/" + key + "?API_TOKEN=" + token;
        //System.out.println("trying to get '" + key + "' ...");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(loadUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new KVExchangeException("Получение данных не удалось. Сервер возвращает: " + response.statusCode());
            }
            return response.body();

        } catch (IOException | InterruptedException e) {
            throw new KVExchangeException("При получении данных с сервера возникло исключение: " + e);
        }
    }


    public String info() {
        String loadUrl = url + "/info";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(loadUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new KVExchangeException("Получение информации о сохраненных данных не удалось. Сервер возвращает: " + response.statusCode());
            }
            return response.body();

        } catch (IOException | InterruptedException e) {
            throw new KVExchangeException("При получении информации о сохраненных данных возникло исключение: " + e);
        }
    }

}
