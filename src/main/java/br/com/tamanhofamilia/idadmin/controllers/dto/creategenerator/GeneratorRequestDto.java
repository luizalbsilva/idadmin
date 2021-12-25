package br.com.tamanhofamilia.idadmin.controllers.dto.creategenerator;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GeneratorRequestDto {
    @Schema(description = "Owner of the generator; the only one who may ask for numbers", defaultValue = "This api's user"
            , nullable = true, example = "527c8326-38e7-4f3a-b6bf-9fc5db71cef8")
    private String owner;
    @Schema(description = "name of the generator.", example = "My awsome generator", maxLength = 50)
    private String name;
    @Schema(description = "First number to be generated", example = "1")
    private long start;
    @Schema(description = "Last number to be generated", example = "200")
    private long end;
}
