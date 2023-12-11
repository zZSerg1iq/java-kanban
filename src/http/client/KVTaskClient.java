package http.client;

import adaper.LocalDateTimeAdapter;
import adaper.StatusAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import enums.Status;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class KVTaskClient {

    private final String token;
    private final String url;
    private final Gson gson;


    public KVTaskClient(String url) {
        this.url = url;
        token = getToken();
        System.out.println(token);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .create();
    }

    private String getToken() {
        String token = null;
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
            token = response.body();
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }

        return token;
    }


    public void put(String key, String json) {
        String regUrl = url + "/save/" + key + "?API_TOKEN=" + token;

       // System.out.println("Saving key '"+key+"' value: "+json);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(regUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + url + "', возникла ошибка.\n");
        }
    }

/*    public String load(String key) {
        try {
            String loadUrl = url + "/load/" + key + "?API_TOKEN=" + token;
            URI uri = URI.create(loadUrl);
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Ошибка запроса, статус запроса: " + response.statusCode());
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Не удалось загрузить данные с KVServer.", e);
        }
    }*/

    public String load(String key) {
        String loadUrl = url + "/load/" + key + "?API_TOKEN=" + token;

        //System.out.println(loadUrl);
       // System.out.println("trying to get '"+key+"' ...");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(loadUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
            //System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + url + "', возникла ошибка.\n");
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
            String responce = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return responce;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
            //System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + url + "', возникла ошибка.\n");
        }
    }

}
