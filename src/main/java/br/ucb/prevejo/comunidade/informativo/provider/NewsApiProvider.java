package br.ucb.prevejo.comunidade.informativo.provider;

import br.ucb.prevejo.shared.intefaces.HttpClient;
import br.ucb.prevejo.shared.util.DateAndTime;
import br.ucb.prevejo.shared.util.JsonNodeDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class NewsApiProvider implements NewsProvider {

    private final HttpClient client;

    public NewsApiProvider(HttpClient client) {
        this.client = client;
    }

    @Value("${api.newsapi.key}")
    private String key;

    @Override
    public Collection<News> searchInLastDay(String query) {
        Map<String, String> params = buildParamsMap();

        params.put("q", query);
        params.put("from", DateAndTime.now().toLocalDate().format(DateTimeFormatter.ISO_DATE));

        return parse(client.get(new JsonNodeDeserializer(), "https://newsapi.org/v2/everything", params));
    }

    private Map<String, String> buildParamsMap() {
        Map<String, String> params = new HashMap<>();

        params.put("apiKey", this.key);
        params.put("language", "pt");
        params.put("sortBy", "publishedAt");

        return params;
    }

    private Collection<News> parse(JsonNode node) {
        return StreamSupport.stream(node.get("articles").spliterator(), false)
                .map(jn -> parseNews(jn))
                .collect(Collectors.toList());
    }

    private News parseNews(JsonNode node) {
        String source = node.get("source").get("name").asText();
        String title = node.get("title").asText();
        String description = node.get("description").asText();
        String url = node.get("url").asText();
        LocalDateTime publishedDate = LocalDateTime.ofInstant(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(node.get("publishedAt").asText())), DateAndTime.currentZone());

        return new News(title, description, url, source, publishedDate);
    }

}
