package com.example.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientDto {
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z -]*$")
    private String first_name;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z '-]*$")
    private String last_name;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @Size(min = 3)
    private String city;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth_date;
}
