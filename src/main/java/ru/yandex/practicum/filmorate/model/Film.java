package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Data
public class Film {
    public static final LocalDate EARLIEST_DATE = LocalDate.ofInstant(Instant.ofEpochSecond(-2_335_564_800L),
            ZoneId.systemDefault());
    private Long id;
    @NotBlank
    private final String name;
    @NotNull
    @Size(min = 1, max = 200)
    private final String description;
    @NotNull
    @Past
    private final LocalDate releaseDate;
    @Min(1)
    private final long duration;
    private final Set<Long> likes = new HashSet<>();
    private List<Genre> genres = Collections.emptyList();
    private RatingMPA mpa;

    public Film(Long id, String name, String description, LocalDate releaseDate, long duration, RatingMPA mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Map<String, Object> toMap() {
        return Map.of(
                "name", name,
                "description", description,
                "release_date", releaseDate,
                "duration", duration,
                "rating_MPA_Id", mpa.getId()
        );
    }
}
