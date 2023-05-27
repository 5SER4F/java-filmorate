package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    public static final LocalDate EARLIEST_DATE = LocalDate.ofInstant(Instant.ofEpochSecond(-2_335_564_800L),
            ZoneId.systemDefault());
    private long id;
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
    private Set<Long> likes = new HashSet<>();

    public boolean addLike(Long userId) {
        return likes.add(userId);
    }

    public boolean removeLike(Long userId) {
        return likes.remove(userId);
    }

}
