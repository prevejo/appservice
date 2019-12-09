package br.ucb.prevejo.transporte.areaintegracao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface AreaIntegracaoRepository extends JpaRepository<AreaIntegracao, Integer>, JpaSpecificationExecutor {

    List<AreaIntegracaoDTO> findByIdIn(Collection<Integer> ids);

}
