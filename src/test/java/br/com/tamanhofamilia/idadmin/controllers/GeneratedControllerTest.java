package br.com.tamanhofamilia.idadmin.controllers;

import br.com.tamanhofamilia.idadmin.models.services.IIdGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeneratedControllerTest {
    @InjectMocks
    GeneratedController controller;

    @Mock
    IIdGeneratorService service;

    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .build();
    }

    @Test
    void confirmNumber() throws Exception {
        mockMvc.perform(
                        put("/api/v1/generator/1/next/2/confirm")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
        ;
        verify(this.service).useConfirm(1,2);
    }

    @Test
    void freeNumber() throws Exception {
        mockMvc.perform(
                        put("/api/v1/generator/1/next/2/free")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
        ;
        verify(this.service).freeLock(1,2);
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