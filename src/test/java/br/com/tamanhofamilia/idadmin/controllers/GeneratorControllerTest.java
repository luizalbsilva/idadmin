package br.com.tamanhofamilia.idadmin.controllers;

import br.com.tamanhofamilia.idadmin.controllers.dto.creategenerator.GeneratorResponseDto;
import br.com.tamanhofamilia.idadmin.models.entities.Generator;
import br.com.tamanhofamilia.idadmin.models.services.IIdGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GeneratorControllerTest {
    @InjectMocks
    GeneratorController controller;

    @Mock
    IIdGeneratorService service;

    @Mock
    Converter<Generator, GeneratorResponseDto> converter;

    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .build();
    }

    @Test
    void createGenerator() throws Exception {
        mockMvc.perform(
                        post("/api/v1/generator")
                                .accept(MediaType.APPLICATION_JSON)
                                .content("{\"name\": \"My Test\", \"start\": 1, \"end\": 100}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
        ;
    }

    @Test
    void nextNumber() throws Exception {
        mockMvc.perform(
                        post("/api/v1/generator/1/next")
                                .accept(MediaType.APPLICATION_JSON)
                                .content("{\"owner\": \"6671a32b-c7a6-43f5-83e6-043717864708\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
        ;
    }
}