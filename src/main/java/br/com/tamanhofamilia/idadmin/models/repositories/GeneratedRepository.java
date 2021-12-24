package br.com.tamanhofamilia.idadmin.models.repositories;

import br.com.tamanhofamilia.idadmin.models.entities.Generated;
import br.com.tamanhofamilia.idadmin.models.entities.GeneratedKey;
import br.com.tamanhofamilia.idadmin.models.entities.GeneratedStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneratedRepository extends JpaRepository<Generated, GeneratedKey> {
    List<Generated> findAllByGeneratorIdAndStatus(long generatorId, GeneratedStatus status);
}
