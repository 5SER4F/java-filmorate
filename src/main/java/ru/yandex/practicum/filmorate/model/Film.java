package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Data
@AllArgsConstructor
public class Film {
    public static final LocalDate EARLIEST_DATE = LocalDate.ofInstant(Instant.ofEpochSecond(-2335564800L),
            ZoneId.systemDefault());
    private int id;
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;

    public long getDuration() {
        return duration.toSeconds();
    }
}
