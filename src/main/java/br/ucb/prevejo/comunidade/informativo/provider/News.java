package br.ucb.prevejo.comunidade.informativo.provider;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class News implements Comparable<News> {

    private String title;
    private String description;
    private String url;
    private String source;
    private LocalDateTime publishedDate;

    @Override
    public int compareTo(News o) {
        return publishedDate.compareTo(o.publishedDate);
    }
}
