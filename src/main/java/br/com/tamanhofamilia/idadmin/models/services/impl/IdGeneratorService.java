package br.com.tamanhofamilia.idadmin.models.services.impl;

import br.com.tamanhofamilia.idadmin.models.entities.Generated;
import br.com.tamanhofamilia.idadmin.models.entities.GeneratedKey;
import br.com.tamanhofamilia.idadmin.models.entities.GeneratedStatus;
import br.com.tamanhofamilia.idadmin.models.entities.Generator;
import br.com.tamanhofamilia.idadmin.models.exceptions.*;
import br.com.tamanhofamilia.idadmin.models.repositories.GeneratedRepository;
import br.com.tamanhofamilia.idadmin.models.repositories.GeneratorRepository;
import br.com.tamanhofamilia.idadmin.models.services.IIdGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class IdGeneratorService implements IIdGeneratorService {
    @Autowired
    private GeneratorRepository generatorRepository;

    @Autowired
    private GeneratedRepository generatedRepository;

    @Autowired
    private TransactionHelper transactionHelper;

    private String getLoggedUser() {
        Jwt usr = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return usr.getClaimAsString("sub");
    }

    @Override
    @Transactional
    public Generator createGenerator(String owner, String name, long initialRange, long finalRange) {
        Generator generator = new Generator();
        generator.setName(name);
        generator.setRangeInitial(initialRange);
        generator.setRangeFinal(finalRange);
        generator.setEnabled(true);
        generator.setActualPosition(initialRange);
        if (owner == null)
            generator.setOwner(getLoggedUser());
        else
            generator.setOwner(owner);
        return generatorRepository.save(generator);
    }

    @Override
    public long next(long generatorId, String lockTo) {
        Generator generator = loadAndCheckGenerator(generatorId);

        if (!generator.isEnabled()) throw new DisabledGeneratorException();
        final List<Generated> allByGeneratorIdAndStatus = generatedRepository.findAllByGeneratorIdAndStatus(generatorId, GeneratedStatus.FREE);
        if (!allByGeneratorIdAndStatus.isEmpty()) {
            for (Generated generated: allByGeneratorIdAndStatus) {
                try {
                    generated.setStatus(GeneratedStatus.LOCKED);
                    generated.setExternalId(lockTo);
                    transactionHelper.save(generated);
                    log.debug("Reusing generated object: {}", generated);
                    return generated.getId().getGenerated();
                } catch (StaleObjectStateException e) {
                    log.debug("Ignoring object -- alreary updated: {}", generated);
                }
            }
        }
        return generateFromGenerator(generatorId, lockTo, generator);
    }

    private Generator loadAndCheckGenerator(long generatorId) {
        Generator generator = generatorRepository.getById(generatorId);
        if (! generator.getOwner().equals(getLoggedUser()))
            throw new AccessDeniedException("Not the owner");
        return generator;
    }

    private long generateFromGenerator(long generatorId, String lockTo, Generator generator) {
        long retVal;
        while(true) {
            try {
                if (generator.getActualPosition() > generator.getRangeFinal()) throw new GeneratorOverflowException();
                retVal = generator.getActualPosition();
                generator.setActualPosition(retVal + 1);
                transactionHelper.save(generator);
                break;
            } catch (StaleObjectStateException e) {
                log.debug("Ignoring object -- alreary updated: {}", generator);
                generator = generatorRepository.getById(generatorId);
                if (!generator.isEnabled()) throw new DisabledGeneratorException();
            }
        }
        final Generated generated = transactionHelper.save(
                Generated.builder()
                        .id(new GeneratedKey(generatorId, retVal))
                        .status(GeneratedStatus.LOCKED)
                        .externalId(lockTo)
                        .build());
        log.debug("Reusing generated object: {}", generated);
        return retVal;
    }

    @Override
    public void freeLock(long generatorId, long number) throws IdAdminException {
        loadAndCheckGenerator(generatorId);
        final Optional<Generated> generatedOptional = generatedRepository.findById(new GeneratedKey(generatorId, number));
        if (! generatedOptional.isPresent()) throw new NotFoundException();

        final Generated generated = generatedOptional.get();
        if (generated.getStatus() != GeneratedStatus.LOCKED) throw new IncorrectStatusException();
        generated.setStatus(GeneratedStatus.FREE);
        generated.setExternalId(null);
        generatedRepository.save(generated);

    }

    @Override
    public void useConfirm(long generatorId, long number) throws IdAdminException {
        loadAndCheckGenerator(generatorId);
        final Optional<Generated> generatedOptional = generatedRepository.findById(new GeneratedKey(generatorId, number));
        if (! generatedOptional.isPresent()) throw new NotFoundException();

        final Generated generated = generatedOptional.get();
        if (generated.getStatus() != GeneratedStatus.LOCKED) throw new IncorrectStatusException();
        generated.setStatus(GeneratedStatus.UNDER_USE);
        generatedRepository.save(generated);
    }
}


@Component
class TransactionHelper {
    @Autowired
    GeneratedRepository generatedRepository;
    @Autowired
    GeneratorRepository generatorRepository;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Generator save(Generator generator) {
        return generatorRepository.save(generator);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Generated save(Generated generated) {
        return generatedRepository.save(generated);
    }
}