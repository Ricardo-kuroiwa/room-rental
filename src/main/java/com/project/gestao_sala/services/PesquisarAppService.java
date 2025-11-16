package com.project.gestao_sala.services;

import com.project.gestao_sala.model.espaco.Espaco;
import com.project.gestao_sala.model.espaco.EspacoFiltroDTO;
import com.project.gestao_sala.repository.implementacao.EspacoFileRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PesquisarAppService {

    private final EspacoFileRepository espacoFileRepository;

    public PesquisarAppService(EspacoFileRepository espacoFileRepository) {
        this.espacoFileRepository = espacoFileRepository;
    }

    public List<Espaco> pesquisar(EspacoFiltroDTO filtros) {
        Espaco[] espacos = Optional.ofNullable(espacoFileRepository.listar()).orElse(new Espaco[0]);

        return Arrays.stream(espacos)
                .filter(espaco -> {
                    if (filtros == null) return true;

                    if (filtros.codigo() != null && espaco.getCodigo() != filtros.codigo()) {
                        return false;
                    }

                    if (filtros.nome() != null &&
                            !espaco.getNome().toLowerCase().contains(filtros.nome().toLowerCase())) {
                        return false;
                    }

                    if (filtros.tipo() != null &&
                            !espaco.getTipo().equalsIgnoreCase(filtros.tipo())) {
                        return false;
                    }

                    if (filtros.predio() != null &&
                            !espaco.getPredio().equalsIgnoreCase(filtros.predio())) {
                        return false;
                    }

                    if (filtros.ativo() != null &&
                            espaco.isAtivo() != filtros.ativo()) {
                        return false;
                    }

                    if (filtros.capacidade() != null &&
                            espaco.getCapacidade() <= filtros.capacidade()) {
                        return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }
}