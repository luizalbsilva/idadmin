package br.com.tamanhofamilia.idadmin.models.repositories;

import br.com.tamanhofamilia.idadmin.models.entities.Generated;
import br.com.tamanhofamilia.idadmin.models.entities.GeneratedKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneratedRepository extends JpaRepository<Generated, GeneratedKey> {
}
