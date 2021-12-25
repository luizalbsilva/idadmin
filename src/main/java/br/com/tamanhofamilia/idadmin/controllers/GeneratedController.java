package br.com.tamanhofamilia.idadmin.controllers;

import br.com.tamanhofamilia.idadmin.controllers.dto.creategenerator.GeneratorRequestDto;
import br.com.tamanhofamilia.idadmin.controllers.dto.creategenerator.GeneratorResponseDto;
import br.com.tamanhofamilia.idadmin.controllers.dto.creategenerator.NextNumberRequestDto;
import br.com.tamanhofamilia.idadmin.controllers.dto.creategenerator.NextNumberResponseDto;
import br.com.tamanhofamilia.idadmin.models.entities.Generator;
import br.com.tamanhofamilia.idadmin.models.exceptions.IdAdminException;
import br.com.tamanhofamilia.idadmin.models.exceptions.NotFoundException;
import br.com.tamanhofamilia.idadmin.models.services.IIdGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/generator")
@Tag(description = "Id generation and listings", name = "Generated Administration")
public class GeneratedController {
    @Autowired
    private IIdGeneratorService service;

    @Autowired
    private Converter<Generator, GeneratorResponseDto> converter;

    @Operation(description = "Requests a new number for an specific generator, identified by id")
    @PostMapping("/{id}/next")
    public ResponseEntity<NextNumberResponseDto> nextNumber(
            @Parameter(description =  "Generator identifier. You got this id during when you create your generator.") @PathVariable("id") long generatorId,
            @RequestBody NextNumberRequestDto body) {
        return ResponseEntity.ok(new NextNumberResponseDto(service.next(generatorId, body.getOwner())));
    }

    @Operation(description = "Frees a number, so it can be used again")
    @PutMapping("/{id}/next/{number}/free")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void freeNumber(
            @Parameter(description =  "Generator identifier. You got this id during when you create your generator.") @PathVariable("id") long generatorId,
            @Parameter(description =  "Generated number") @PathVariable("number") long generated ) throws IdAdminException {
        service.freeLock(generatorId, generated);
    }

    @Operation(description = "Confirms the use of the number")
    @PutMapping("/{id}/next/{number}/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirmNumber(
            @Parameter(description =  "Generator identifier. You got this id during when you create your generator.") @PathVariable("id") long generatorId,
            @Parameter(description =  "Generated number") @PathVariable("number") long generated
            ) throws IdAdminException {
        service.useConfirm(generatorId, generated);
    }
}
