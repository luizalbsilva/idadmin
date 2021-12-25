package br.com.tamanhofamilia.idadmin.controllers.dto.creategenerator;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GeneratorResponseDto {
    @Schema(description = "Identificator for the Generator")
    private Long id;

    @Schema(description = "Generator's name. Just for visualization")
    private String name;

    @Schema(description = "Initial range number.")
    private Long rangeInitial;

    @Schema(description = "Final rage number.")
    private Long rangeFinal;

    @Schema(description = "Actual position (next number to be sent)")
    private Long actualPosition;
}
