package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.List;

public interface RatingMPAStorage {

    RatingMPA get(Long id);

    List<RatingMPA> findAll();

    boolean contain(Long id);

}
