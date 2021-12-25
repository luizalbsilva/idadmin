package br.com.tamanhofamilia.idadmin.controllers.dto.creategenerator;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class NextNumberRequestDto {
    @Schema(description = "The identifier (to your system) of whom is the owner of the lock", maxLength = 50, example = "ed99f228-a9e3-49c4-8a57-e7bd61568136")
    private String owner;
}
