package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.*;
import lombok.*;
import ru.yandex.practicum.filmorate.validation.IsValidRelease;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {
    private Integer id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @IsValidRelease
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private Mpa mpa;
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();
    @Builder.Default
    private Set<Integer> likedByUserIds = new HashSet<>();

    public Film(int id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Integer getLikesSize() {
        return likedByUserIds.size();
    }
}
