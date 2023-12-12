package http.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import excepton.KVExchangeException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KVServer {
    public static void main(String[] args) throws IOException {
        new KVServer().start();
    }


    public static final int PORT = 8078;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
        server.createContext("/clear", this::clear);
        server.createContext("/stop", this::stop);
        server.createContext("/info", this::info);
    }

    private void clear(HttpExchange exchange) {
        try {
            exchange.sendResponseHeaders(200, 0);
            sendText(exchange, "KVServer очищен");
            data.clear();
        } catch (IOException e) {
            throw new KVExchangeException("При обработке запроса по очистке сервера возникла ошибка: " + e);
        } finally {
            exchange.close();
        }

    }


    private void load(HttpExchange exchange) {
        try {
            String key = exchange.getRequestURI().getPath().substring("/load/".length());

            if ((exchange.getRequestMethod()).equals("GET")) {
                if (!hasAuth(exchange)) {
                    System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                    exchange.sendResponseHeaders(403, 0);
                    return;
                }

                if (data.containsKey(key)) {
                    exchange.sendResponseHeaders(200, 0);
                    sendText(exchange, data.get(key));
                } else {
                    exchange.sendResponseHeaders(200, 0);
                    sendText(exchange, "Данные по ключу не найдены");
                }

            } else {
                System.out.println("/load ждёт GET-запрос, а получил: " + exchange.getRequestMethod());
                exchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            throw new KVExchangeException("При обработке запроса загрузки возникло исключение: " + e);
        } finally {
            exchange.close();
        }
    }

    private void save(HttpExchange exchange) {
        try {
            System.out.println("\n/save");
            if (!hasAuth(exchange)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                exchange.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(exchange.getRequestMethod())) {
                String key = exchange.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    exchange.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(exchange);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    exchange.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                // System.out.println("Значение для ключа '" + key + "' успешно добавлено/обновлено!");
                exchange.sendResponseHeaders(201, 0);
            } else {
                System.out.println("/save ждёт POST-запрос, а получил: " + exchange.getRequestMethod());
                exchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            throw new KVExchangeException("При обработке запроса загрузки возникло исключение: " + e);
        } finally {
            exchange.close();
        }
    }

    private void register(HttpExchange exchange) {
        try {
            System.out.println("\n/register");
            if ("GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, 0);
                sendText(exchange, apiToken);
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            throw new KVExchangeException("При обработке запроса загрузки возникло исключение: " + e);
        } finally {
            exchange.close();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        System.out.println("API_TOKEN: " + apiToken);
        server.start();
    }

    public void stop(HttpExchange h) {
        try {
            sendText(h, "KVServer остановлен");
            System.exit(0);
        } catch (IOException e){}
        finally {
            h.close();
        }
    }

    public void info(HttpExchange exchange) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            exchange.sendResponseHeaders(200, 0);
            data.forEach((s, s2) -> stringBuilder.append(s).append(": ").append(s2).append("\n"));
            String result = stringBuilder.length() > 0 ? stringBuilder.toString() : "Данные отсутствуют";
            sendText(exchange, result);
        } catch (IOException e) {
            throw new KVExchangeException("При обработке запроса загрузки возникло исключение: " + e);
        } finally {
            exchange.close();
        }
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.getResponseBody().write(resp);
        exchange.close();
    }


}