package br.com.tamanhofamilia.idadmin.models.services.impl;


import br.com.tamanhofamilia.idadmin.models.entities.Generated;
import br.com.tamanhofamilia.idadmin.models.entities.Generator;
import br.com.tamanhofamilia.idadmin.models.repositories.GeneratedRepository;
import br.com.tamanhofamilia.idadmin.models.repositories.GeneratorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionHelperTest {
    @InjectMocks
    TransactionHelper transactionHelper;

    @Mock
    GeneratorRepository generatorRepo;
    @Mock
    GeneratedRepository generatedRepo;

    @Test
    void saveGenerated() {
        transactionHelper.save(new Generated());

        verify(generatedRepo).save(any(Generated.class));
    }

    @Test
    void saveGenetor() {
        transactionHelper.save(new Generator());

        verify(generatorRepo).save(any(Generator.class));
    }
}
