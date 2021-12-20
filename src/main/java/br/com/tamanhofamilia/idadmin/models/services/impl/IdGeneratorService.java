package br.com.tamanhofamilia.idadmin.models.services.impl;

import br.com.tamanhofamilia.idadmin.models.entities.Generator;
import br.com.tamanhofamilia.idadmin.models.exceptions.NotFoundException;
import br.com.tamanhofamilia.idadmin.models.repositories.GeneratorRepository;
import br.com.tamanhofamilia.idadmin.models.services.IIdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class IdGeneratorService implements IIdGeneratorService {
    @Autowired
    private GeneratorRepository generatorRepository;

    @Override
    public Generator createGenerator(String name, long initialRange, long finalRange) {
        Generator generator = new Generator();
        generator.setName(name);
        generator.setRangeInitial(initialRange);
        generator.setRangeFinal(finalRange);
        generator.setEnabled(true);
        generator.setActualPosition(initialRange);

        Jwt usr = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        generator.setOwner(usr.getClaimAsString("sub"));
        return generatorRepository.save(generator);
    }

    @Override
    public Long next(long generatorId) {
        return null;
    }

    @Override
    public void freeLock(long generatorId, long number) throws NotFoundException {

    }

    @Override
    public void useConfirm(long generatorId, long number) throws NotFoundException {

    }
}
