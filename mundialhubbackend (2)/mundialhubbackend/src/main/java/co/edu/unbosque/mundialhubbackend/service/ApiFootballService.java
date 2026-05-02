package co.edu.unbosque.mundialhubbackend.service;

import com.google.gson.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente HTTP que encapsula todas las llamadas a API-Football v3.
 * Base URL: https://v3.football.api-sports.io
 * Header:   x-apisports-key: {API_KEY}
 *
 * Nunca se llama directamente desde el Controller — solo el Service lo usa.
 * Maneja errores HTTP 4xx y 5xx retornando listas vacías para no tumbar
 * la experiencia del usuario (RNF-04).
 */
@Component
public class ApiFootballService {

    private static final String BASE_URL = "https://v3.football.api-sports.io";

    // ID de la Copa del Mundo en API-Football
    public static final int WORLD_CUP_LEAGUE_ID = 1;
    public static final int WORLD_CUP_SEASON    = 2026;

//    @Value("${api.football.key}")
    private String apiKey = "3b2bd5d3f48ab0b0425202d3eec0cb6e";

    private final HttpClient  httpClient;
    private final Gson        gson;

    public ApiFootballService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.gson = new GsonBuilder().create();
    }

    // ─── Métodos públicos ────────────────────────────────────────────────────

    /**
     * Trae todos los partidos de la Copa del Mundo 2026.
     * TTL recomendado: 24 h para partidos futuros, 30 s para partidos en vivo.
     */
    public List<JsonObject> fetchAllFixtures() {
        String url = BASE_URL + "/fixtures?league=" + WORLD_CUP_LEAGUE_ID
                + "&season=" + WORLD_CUP_SEASON;
        return executeAndParseResponse(url);
    }

    /**
     * Trae los partidos en vivo del Mundial en este momento.
     * Actualización: cada 30 s (RNF-08).
     */
    public List<JsonObject> fetchLiveFixtures() {
        String url = BASE_URL + "/fixtures?live=" + WORLD_CUP_LEAGUE_ID;
        return executeAndParseResponse(url);
    }

    /**
     * Trae un partido específico con sus eventos incluidos.
     * Se usa para la vista de partido en detalle / en vivo (HU-05).
     */
    public JsonObject fetchFixtureById(Long fixtureId) {
        String url = BASE_URL + "/fixtures?id=" + fixtureId;
        List<JsonObject> results = executeAndParseResponse(url);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Trae partidos por fecha (YYYY-MM-DD) y liga.
     * Útil para el calendario diario (HU-04).
     */
    public List<JsonObject> fetchFixturesByDate(String date) {
        String url = BASE_URL + "/fixtures?league=" + WORLD_CUP_LEAGUE_ID
                + "&season=" + WORLD_CUP_SEASON
                + "&date=" + date;
        return executeAndParseResponse(url);
    }

    /**
     * Trae partidos de un equipo en la temporada actual.
     * Usado para construir la agenda personal (HU-06).
     */
    public List<JsonObject> fetchFixturesByTeam(Long teamId) {
        String url = BASE_URL + "/fixtures?league=" + WORLD_CUP_LEAGUE_ID
                + "&season=" + WORLD_CUP_SEASON
                + "&team=" + teamId;
        return executeAndParseResponse(url);
    }

    /**
     * Trae los eventos (goles, tarjetas, cambios) de un partido específico.
     */
    public List<JsonObject> fetchEventsByFixture(Long fixtureId) {
        String url = BASE_URL + "/fixtures/events?fixture=" + fixtureId;
        return executeAndParseResponse(url);
    }

    // ─── Privados ────────────────────────────────────────────────────────────

    /**
     * Ejecuta la petición GET, valida la respuesta y extrae el array "response".
     * Si el proveedor falla (4xx, 5xx, timeout), retorna lista vacía sin lanzar
     * excepción — el Service decide qué mostrar al usuario (RNF-04).
     */
    private List<JsonObject> executeAndParseResponse(String url) {
        List<JsonObject> results = new ArrayList<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(8))
                    .header("x-apisports-key", apiKey)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("[ApiFootballClient] HTTP " + response.statusCode()
                        + " en: " + url);
                return results;
            }

            JsonObject root = gson.fromJson(response.body(), JsonObject.class);
            JsonArray  arr  = root.getAsJsonArray("response");

            if (arr == null || arr.isEmpty()) return results;

            for (JsonElement el : arr) {
                results.add(el.getAsJsonObject());
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("[ApiFootballClient] Error de red: " + e.getMessage());
        }
        return results;
    }
}