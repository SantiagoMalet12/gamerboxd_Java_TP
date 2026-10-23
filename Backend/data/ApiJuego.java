package data;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ApiJuego {
    private static final String API_KEY = "018d48659af84265982427914211cf95";
    private static final String BASE_URL = "https://api.rawg.io/api/games";
    
    private final HttpClient client;

    public ApiJuego() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    
    public List<Integer> obtenerIdsJuegosPopulares(int cantidadTotal) {
        List<Integer> ids = new ArrayList<>();
        int page = 1;
        int pageSize = 40; // max permitido por RAWG por pagina

        try {
            while (ids.size() < cantidadTotal) {
                String url = BASE_URL + "?key=" + API_KEY + "&page=" + page + "&page_size=" + pageSize;
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .timeout(Duration.ofSeconds(20))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    System.err.println("error  (HTTP " + response.statusCode() + "): " + response.body());
                    break;
                }

                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                if (!json.has("results")) {
                    break;
                }

                JsonArray results = json.getAsJsonArray("results");
                if (results.size() == 0) {
                    break;
                }

                for (JsonElement elem : results) {
                    JsonObject item = elem.getAsJsonObject();
                    if (item.has("id")) {
                        ids.add(item.get("id").getAsInt());
                        if (ids.size() >= cantidadTotal) {
                            break;
                        }
                    }
                }

                page++;
            }
        } catch (Exception e) {
            System.err.println("error " + e.getMessage());
            e.printStackTrace();
        }

        return ids;
    }

// obtiene detalle, el objeto de la api esta encapsulado
    public JsonObject obtenerDetalleJuego(int idJuego) {
        String url = BASE_URL + "/" + idJuego + "?key=" + API_KEY;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .timeout(Duration.ofSeconds(20))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return JsonParser.parseString(response.body()).getAsJsonObject();
            } else {
                System.err.println("No se pudo obtener detalle de juego ID " + idJuego + " (HTTP " + response.statusCode() + ")");
            }
        } catch (Exception e) {
            System.err.println("Excepción al consultar detalle de juego ID " + idJuego + ": " + e.getMessage());
        }
        return null;
    }
}
