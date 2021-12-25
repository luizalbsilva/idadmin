package br.com.tamanhofamilia.idadmin.controllers.dto.creategenerator;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NextNumberResponseDto {
    @Schema(description = "The generated number. It won't be used anymore until you free the number again.")
    private long number;
}
