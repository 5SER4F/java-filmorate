package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Data
@AllArgsConstructor
public class Film {
    public static final LocalDate EARLIEST_DATE = LocalDate.ofInstant(Instant.ofEpochSecond(-2335564800L), ZoneId.systemDefault());
    private int id;
    @NotBlank
    private String name;
    @NotNull
    @Size(min = 1, max = 200)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    private Duration duration;

    public long getDuration() {
        return duration.toSeconds();
    }
}
