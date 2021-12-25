package br.com.tamanhofamilia.idadmin.models.services.impl;

import br.com.tamanhofamilia.idadmin.models.entities.Generated;
import br.com.tamanhofamilia.idadmin.models.entities.GeneratedKey;
import br.com.tamanhofamilia.idadmin.models.entities.GeneratedStatus;
import br.com.tamanhofamilia.idadmin.models.entities.Generator;
import br.com.tamanhofamilia.idadmin.models.exceptions.*;
import br.com.tamanhofamilia.idadmin.models.repositories.GeneratedRepository;
import br.com.tamanhofamilia.idadmin.models.repositories.GeneratorRepository;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdGeneratorServiceTest {
    public static final String LOCK_OWNER = "chilindrina";
    public static final String OLD_LOCK_OWNER = "chavo";
    public static final long GENERATOR_ID = 1L;
    public static final String GENERATOR_OWNER = "af5c02a9-04e0-4a5d-bd58-1f9c5979b21c";

    @InjectMocks
    IdGeneratorService generatorService;

    @Mock
    GeneratorRepository generatorRepository;

    @Mock
    GeneratedRepository generatedRepository;

    @Mock
    TransactionHelper transactionHelper;

    void doBefore() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Jwt principal = mock(Jwt.class);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(principal.getClaimAsString("sub")).thenReturn(GENERATOR_OWNER);
    }

    @Test
    @DisplayName("Creates a generator")
    void createGenerator() {
        when(generatorRepository.save(any(Generator.class)))
                .thenAnswer(a -> a.getArgument(0, Generator.class));

        final Generator generator = generatorService.createGenerator(GENERATOR_OWNER, "Testing Name #1", 1L, 500L);

        assertEquals("Testing Name #1", generator.getName());
        assertEquals(1L, generator.getActualPosition());
        assertEquals(1L, generator.getRangeInitial());
        assertEquals(500L, generator.getRangeFinal());
        assertEquals(GENERATOR_OWNER, generator.getOwner());
    }

    @Test
    @DisplayName("Creates a generator with no owner info")
    void createGeneratorNoOwnerInfo() {
        when(generatorRepository.save(any(Generator.class)))
                .thenAnswer(a -> a.getArgument(0, Generator.class));

        final Generator generator = generatorService.createGenerator(null, "Testing Name #1", 1L, 500L);

        assertEquals("Testing Name #1", generator.getName());
        assertEquals(1L, generator.getActualPosition());
        assertEquals(1L, generator.getRangeInitial());
        assertEquals(500L, generator.getRangeFinal());
        assertEquals(GENERATOR_OWNER, generator.getOwner());
    }

    @Test
    @DisplayName("Number creation others Generator is forbidden")
    void nextDontWorkOnOthersGenerators() {
        doBefore();
        when(generatorRepository.getById(GENERATOR_ID))
                .thenReturn(new Generator(GENERATOR_ID, "Test Generator", OLD_LOCK_OWNER, GENERATOR_ID, 5000L, 2L, GENERATOR_ID, false));

        assertThrows(AccessDeniedException.class, ()-> generatorService.next(GENERATOR_ID, LOCK_OWNER));
    }

    @Test
    @DisplayName("Number creation on disabled Generator is forbidden")
    void nextDontWorkOnDisabledGenerators() {
        doBefore();
        when(generatorRepository.getById(GENERATOR_ID))
                .thenReturn(new Generator(GENERATOR_ID, "Test Generator", GENERATOR_OWNER, GENERATOR_ID, 5000L, 2L, GENERATOR_ID, false));

        assertThrows(DisabledGeneratorException.class, ()-> generatorService.next(GENERATOR_ID, LOCK_OWNER));
    }

    @Test
    @DisplayName("Number creation is forbidden if already reached the maximum")
    void nextDontWorkOnGeneratorsOverflow() {
        doBefore();
        when(generatorRepository.getById(GENERATOR_ID))
                .thenReturn(new Generator(GENERATOR_ID, "Test Generator", GENERATOR_OWNER, GENERATOR_ID, 5000L, 5001L, GENERATOR_ID, true));

        assertThrows(GeneratorOverflowException.class, ()-> generatorService.next(GENERATOR_ID, LOCK_OWNER));
    }

    @Test
    @DisplayName("Number creation in the simplest way - only generators")
    void nextFirstNumberNumberGen() {
        doBefore();
        when(generatorRepository.getById(GENERATOR_ID))
                .thenReturn(new Generator(GENERATOR_ID, "Test Generator", GENERATOR_OWNER, GENERATOR_ID, 5000L, 2L, GENERATOR_ID, true));

        Long nextNumber = generatorService.next(GENERATOR_ID, LOCK_OWNER);


        assertEquals(2L, nextNumber, "Returned number check ok");

        // Generator record update
        ArgumentCaptor<Generator> generatorCaptor = ArgumentCaptor.forClass(Generator.class);
        verify(transactionHelper).save(generatorCaptor.capture());
        assertEquals(3L, generatorCaptor.getValue().getActualPosition(), "Error to identify nextNumber just with generators");

        // Generated records creation
        ArgumentCaptor<Generated> captor = ArgumentCaptor.forClass(Generated.class);
        verify(transactionHelper).save(captor.capture());
        final Generated generated = captor.getValue();
        assertEquals(2L, generated.getId().getGenerated(), "Error to identify nextNumber just with generators");
        assertEquals(LOCK_OWNER, generated.getExternalId(), "Wrote wrong external id");
        assertEquals(GeneratedStatus.LOCKED, generated.getStatus(), "Wrote wrong status - it should be 'Locked'");
        assertEquals(GENERATOR_ID, generated.getId().getGeneratorId(), "The generator id is wrong !");
    }

    @Test
    @DisplayName("Number creation dealing with concurrency problem on changing 'Generator'")
    void nextFirstNumberConcurrencyProblem() {
        doBefore();
        when(generatorRepository.getById(GENERATOR_ID))
                .thenReturn(new Generator(GENERATOR_ID, "Test Generator", GENERATOR_OWNER, GENERATOR_ID, 5000L, 2L, GENERATOR_ID, true),
                        new Generator(GENERATOR_ID, "Test Generator", GENERATOR_OWNER, GENERATOR_ID, 5000L, 10L, 10L, true));
        when(transactionHelper.save(any(Generator.class)))
                .then(new GeneratorAnsIn2());


        Long nextNumber = generatorService.next(GENERATOR_ID, LOCK_OWNER);

        // Checks the numbre
        assertEquals(10L, nextNumber, "Number created");

        // Generator record update
        ArgumentCaptor<Generator> generatorCaptor = ArgumentCaptor.forClass(Generator.class);
        verify(transactionHelper, times(2)).save(generatorCaptor.capture());
        assertEquals(11L, generatorCaptor.getValue().getActualPosition(), "Error to identify nextNumber just with generators");

        //Geberated record creation
        ArgumentCaptor<Generated> generatedCaptor = ArgumentCaptor.forClass(Generated.class);
        verify(transactionHelper).save(generatedCaptor.capture());
        final Generated generated = generatedCaptor.getValue();
        assertEquals(nextNumber, generated.getId().getGenerated(), "Generated: Error to identify nextNumber just with generators");
        assertEquals(LOCK_OWNER, generated.getExternalId(), "Generated: Wrote wrong external id");
        assertEquals(GeneratedStatus.LOCKED, generated.getStatus(), "Generated: Wrote wrong status - it should be 'Locked'");
        assertEquals(GENERATOR_ID, generated.getId().getGeneratorId(), "Generated: The generator id is wrong !");
    }

    @Test
    @DisplayName("Reusing locked number")
    void nextFirstNumberReusingLockedNumbers() {
        doBefore();
        when(generatorRepository.getById(GENERATOR_ID))
                .thenReturn(new Generator(GENERATOR_ID, "Test Generator", GENERATOR_OWNER, GENERATOR_ID, 5000L, 3, GENERATOR_ID, true));

        when(generatedRepository.findAllByGeneratorIdAndStatus(GENERATOR_ID, GeneratedStatus.FREE))
                .thenReturn(Arrays.asList(
                        Generated.builder()
                                .id(new GeneratedKey(1L, 1L))
                                .externalId(OLD_LOCK_OWNER)
                                .status(GeneratedStatus.FREE)
                                .build(),
                        Generated.builder()
                                .id(new GeneratedKey(1L, 2L))
                                .externalId(OLD_LOCK_OWNER)
                                .status(GeneratedStatus.FREE)
                                .build())
                );
        when(transactionHelper.save(any(Generated.class))).then(new GeneratedAnsIn2());

        final long next = this.generatorService.next(GENERATOR_ID, LOCK_OWNER);

        assertEquals(2L, next);
        verify(transactionHelper, times(2)).save(any(Generated.class));
        verify(transactionHelper, never()).save(any(Generator.class));
    }

    @Test
    @DisplayName("Creating numbers having concurrency problems on everything")
    void nextFirstNumberReusingLockedNumbersCompltee() {
        doBefore();
        when(generatorRepository.getById(GENERATOR_ID))
                .thenReturn(new Generator(GENERATOR_ID, "Test Generator", GENERATOR_OWNER, GENERATOR_ID, 5000L, 3, GENERATOR_ID, true));

        when(generatedRepository.findAllByGeneratorIdAndStatus(GENERATOR_ID, GeneratedStatus.FREE))
                .thenReturn(Arrays.asList(
                        Generated.builder()
                                .id(new GeneratedKey(1L, 1L))
                                .externalId(OLD_LOCK_OWNER)
                                .status(GeneratedStatus.FREE)
                                .build(),
                        Generated.builder()
                                .id(new GeneratedKey(1L, 2L))
                                .externalId(OLD_LOCK_OWNER)
                                .status(GeneratedStatus.FREE)
                                .build())
                );
        when(transactionHelper.save(any(Generated.class))).then(invocationOnMock -> {
            final Generated argument = invocationOnMock.getArgument(0, Generated.class);
            if (argument.getId().getGenerated().longValue() < 3 )
                throw new StaleObjectStateException(Generated.class.getName(), argument.getId().getGeneratorId().longValue());
            return argument;
        });


        final long next = this.generatorService.next(GENERATOR_ID, LOCK_OWNER);

        assertEquals(3L, next);

        // Generator record update
        ArgumentCaptor<Generator> generatorCaptor = ArgumentCaptor.forClass(Generator.class);
        verify(transactionHelper).save(generatorCaptor.capture());
        assertEquals(4L, generatorCaptor.getValue().getActualPosition(), "Error to identify nextNumber just with generators");

        // Generated records creation
        ArgumentCaptor<Generated> captor = ArgumentCaptor.forClass(Generated.class);
        verify(transactionHelper, times(3)).save(captor.capture());
        final Generated generated = captor.getAllValues().get(2);
        assertEquals(3L, generated.getId().getGenerated(), "Error to identify nextNumber just with generators");
        assertEquals(LOCK_OWNER, generated.getExternalId(), "Wrote wrong external id");
        assertEquals(GeneratedStatus.LOCKED, generated.getStatus(), "Wrote wrong status - it should be 'Locked'");
        assertEquals(GENERATOR_ID, generated.getId().getGeneratorId(), "The generator id is wrong !");
    }

    @Test
    @DisplayName("Frees a nonexistent lock")
    void freeLockNonnexistent() {
        doBefore();
        when(generatorRepository.getById(GENERATOR_ID))
                .thenReturn(new Generator(GENERATOR_ID, "Test Generator", GENERATOR_OWNER, GENERATOR_ID, 5000L, 3, GENERATOR_ID, true));
        when(generatedRepository.findById(any( GeneratedKey.class )))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()->generatorService.freeLock(1L, 3L));
    }

    @Test
    @DisplayName("Frees a lock")
    void freeLock() throws IdAdminException {
        doBefore();
        when(generatorRepository.getById(GENERATOR_ID))
                .thenReturn(new Generator(GENERATOR_ID, "Test Generator", GENERATOR_OWNER, GENERATOR_ID, 5000L, 3, GENERATOR_ID, true));
        when(generatedRepository.findById(any( GeneratedKey.class )))
                .thenReturn(Optional.of(Generated.builder()
                                .id(new GeneratedKey(1L, 3L))
                                .externalId(LOCK_OWNER)
                                .status(GeneratedStatus.LOCKED)
                        .build()));

        generatorService.freeLock(1L, 3L);

        final ArgumentCaptor<Generated> generatedCaptor = ArgumentCaptor.forClass(Generated.class);
        verify(generatedRepository).save(generatedCaptor.capture());

        assertEquals(GeneratedStatus.FREE, generatedCaptor.getValue().getStatus());
    }

    @Test
    @DisplayName("Tries to free a number of unlocked generator")
    void freeLockOnUnLockedNumber() throws NotFoundException {
        doBefore();
        when(generatorRepository.getById(1L))
                .thenReturn(new Generator(1L, "Testing", GENERATOR_OWNER, 1L, 500L, 1L, 1L, true));
        when(generatedRepository.findById(any( GeneratedKey.class )))
                .thenReturn(Optional.of(Generated.builder()
                        .id(new GeneratedKey(1L, 3L))
                        .externalId(LOCK_OWNER)
                        .status(GeneratedStatus.UNDER_USE)
                        .build()));

        assertThrows(IncorrectStatusException.class, () -> generatorService.freeLock(1L, 3L));

    }

    @Test
    @DisplayName("Tries to free a number of unowned generator")
    void freeLockUnownedLock() throws NotFoundException {
        doBefore();
        when(generatorRepository.getById(1L))
                .thenReturn(new Generator(1L, "Testing", "609c1baa-08ff-4acf-b4c5-7873bc5b5ad4", 1L, 500L, 1L, 1L, true));

        assertThrows(AccessDeniedException.class, () -> generatorService.freeLock(1L, 3L) );
    }

    @Test
    @DisplayName("Behaviour expected if there is no such record when freeing a number")
    void useConfirmNonnexistent() {
        doBefore();
        when(generatorRepository.getById(GENERATOR_ID))
                .thenReturn(new Generator(GENERATOR_ID, "Test Generator", GENERATOR_OWNER, GENERATOR_ID, 5000L, 3, GENERATOR_ID, true));
        when(generatedRepository.findById(any( GeneratedKey.class )))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, ()->generatorService.useConfirm(1L, 3L));
    }

    @Test
    @DisplayName("Tries to confirm a number of unowned generator")
    void useConfirmUnownedLock() {
        doBefore();
        when(generatorRepository.getById(1L))
                .thenReturn(new Generator(1L, "Testing", "609c1baa-08ff-4acf-b4c5-7873bc5b5ad4", 1L, 500L, 1L, 1L, true));

        assertThrows(AccessDeniedException.class, () -> generatorService.useConfirm(1L, 3L) );
    }

    @Test
    @DisplayName("Tries to confirm a number of unowned generator")
    void useConfirmUnlockedNumber() {
        doBefore();
        when(generatorRepository.getById(1L))
                .thenReturn(new Generator(1L, "Testing", GENERATOR_OWNER, 1L, 500L, 5L, 1L, true));
        when(generatedRepository.findById(any( GeneratedKey.class )))
                .thenReturn(Optional.of(Generated.builder()
                        .id(new GeneratedKey(1L, 3L))
                        .externalId(LOCK_OWNER)
                        .status(GeneratedStatus.FREE)
                        .build()));

        assertThrows(IncorrectStatusException.class, () -> generatorService.useConfirm(1L, 3L) );
    }

    @Test
    @DisplayName("Behaviour expected if there is no such record when confirming a number")
    void useConfirm() throws IdAdminException {
        doBefore();
        when(generatorRepository.getById(GENERATOR_ID))
                .thenReturn(new Generator(GENERATOR_ID, "Test Generator", GENERATOR_OWNER, GENERATOR_ID, 5000L, 3, GENERATOR_ID, true));
        when(generatedRepository.findById(any( GeneratedKey.class )))
                .thenReturn(Optional.of(Generated.builder()
                        .id(new GeneratedKey(1L, 3L))
                        .externalId(LOCK_OWNER)
                        .status(GeneratedStatus.LOCKED)
                        .build()));

        generatorService.useConfirm(1L, 3L);

        final ArgumentCaptor<Generated> generatedCaptor = ArgumentCaptor.forClass(Generated.class);
        verify(generatedRepository).save(generatedCaptor.capture());

        assertEquals(GeneratedStatus.UNDER_USE, generatedCaptor.getValue().getStatus());

    }
}

class GeneratorAnsIn2 implements Answer<Generator> {
    private int start = 0;

    @Override
    public Generator answer(InvocationOnMock invocationOnMock) throws Throwable {
        if (start++ == 0) {
            throw new StaleObjectStateException(Generator.class.getName(), 1);
        }
        return new Generator(1L, "Test Generator", "af5c02a9-04e0-4a5d-bd58-1f9c5979b21c", 1L, 5000L, 11L, 11L, true);
    }

}

class GeneratedAnsIn2 implements Answer<Generated> {
     private int start = 0;

    @Override
    public Generated answer(InvocationOnMock invocationOnMock) throws Throwable {
        if (start++ == 0) {
            throw new StaleObjectStateException(Generated.class.getName(), 1);
        }
        return invocationOnMock.getArgument(0, Generated.class);
    }
}