package br.com.tamanhofamilia.idadmin.models.services;

import br.com.tamanhofamilia.idadmin.models.entities.Generator;
import br.com.tamanhofamilia.idadmin.models.exceptions.NotFoundException;
import org.springframework.security.access.annotation.Secured;

public interface IIdGeneratorService  {
    @Secured("ROLE_ADMIN")
    Generator createGenerator(String name, long initialRange, long finalRange);

    @Secured("ROLE_USER")
    Long next(long generatorId);

    @Secured("ROLE_USER")
    void freeLock(long generatorId, long number) throws NotFoundException;

    @Secured("ROLE_USER")
    void useConfirm(long generatorId, long number) throws NotFoundException;
}
