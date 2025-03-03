package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.validation.IsValidRelease;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Film {
    private int id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @IsValidRelease
    private LocalDate releaseDate;
    @Positive
    private int duration;
}
