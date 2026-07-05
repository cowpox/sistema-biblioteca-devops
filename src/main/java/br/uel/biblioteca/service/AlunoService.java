package br.uel.biblioteca.service;

import br.uel.biblioteca.dao.AlunoDAO;
import br.uel.biblioteca.model.Aluno;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AlunoService {

    private final AlunoDAO alunoDAO;

    public AlunoService(AlunoDAO alunoDAO) {
        this.alunoDAO = alunoDAO;
    }

    public Aluno cadastrar(Aluno aluno) {
        alunoDAO.buscarPorMatricula(aluno.getMatricula()).ifPresent(existente -> {
            throw new IllegalArgumentException(
                    "Ja existe aluno cadastrado com esta matricula: " + aluno.getMatricula());
        });
        normalizarCamposOpcionais(aluno);
        if (aluno.getCpf() != null) {
            alunoDAO.buscarPorCpf(aluno.getCpf()).ifPresent(existente -> {
                throw new IllegalArgumentException(
                        "Ja existe aluno cadastrado com este CPF.");
            });
        }
        aluno.setAtivo(true);
        return alunoDAO.salvar(aluno);
    }

    private void normalizarCamposOpcionais(Aluno aluno) {
        if (aluno.getCpf() != null && aluno.getCpf().isBlank()) {
            aluno.setCpf(null);
        }
        if (aluno.getEmail() != null && aluno.getEmail().isBlank()) {
            aluno.setEmail(null);
        }
        if (aluno.getEndereco() != null && aluno.getEndereco().isBlank()) {
            aluno.setEndereco(null);
        }
    }

    @Transactional(readOnly = true)
    public List<Aluno> listarTodos() {
        return alunoDAO.listarTodos();
    }

    @Transactional(readOnly = true)
    public Optional<Aluno> buscarPorId(Long id) {
        return alunoDAO.buscarPorId(id);
    }

    @Transactional(readOnly = true)
    public List<Aluno> buscarPorTermo(String termo) {
        return alunoDAO.buscarPorTermo(termo);
    }
}
