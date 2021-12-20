package br.com.tamanhofamilia.idadmin.models.repositories;

import br.com.tamanhofamilia.idadmin.models.entities.Generator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneratorRepository extends JpaRepository<Generator, Long> {
}
