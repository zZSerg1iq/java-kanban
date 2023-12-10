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
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .create();
    }

    private String getToken(){
        String token = null;
        String regUrl = url+"/register";

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


    public void put(String key, String json){
        String regUrl = url+"/save/"+key+"?API_TOKEN="+token;

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(regUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println( response.body() );

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + url + "', возникла ошибка.\n");
        }
    }

    public String load(String key){
        String regUrl = url+"/load/"+key+"?API_TOKEN="+token;

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(regUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //    System.out.println(response.body());

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + url + "', возникла ошибка.\n");
        }
        return null;
    }
}
