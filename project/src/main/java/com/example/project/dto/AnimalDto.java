package com.example.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnimalDto {
    private Long id;

    @Pattern(regexp = "^(?![\\s.]+$)[a-zA-Z\\s.-]*$")
    private String name;

    @NotBlank
    @Size(min = 3)
    @Pattern(regexp = "^[A-Za-z ]*$", message = "Species must contain only characters and be at least of size 3")
    private String species;

    @Pattern(regexp = "^[A-Za-z -]*$")
    private String breed;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth_date;

    @NotNull
    private ClientDto ownerDto;

}
