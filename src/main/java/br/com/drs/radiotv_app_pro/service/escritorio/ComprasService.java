package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.ComprasDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.ComprasMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.Compras;
import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import br.com.drs.radiotv_app_pro.model.escritorio.Produto;
import br.com.drs.radiotv_app_pro.repository.escritorio.ComprasRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.FuncionarioRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.ProdutoRepository;
import br.com.drs.radiotv_app_pro.util.KeyGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComprasService {

    private final ComprasRepository repository;
    private final ComprasMapper mapper;
    private final ProdutoRepository produtoRepository;
    private final FuncionarioRepository funcionarioRepository;

    @Transactional
    public ComprasDTO salvar(ComprasDTO dto) {
        // Busca direta pelo ID que vem do Front-end
        Funcionario funcionario = (Funcionario) funcionarioRepository.findByChaveUsuario(dto.getChaveUsuario())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado ID: " + dto.getChaveUsuario()));

        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado ID: " + dto.getProdutoId()));

        Compras entidade = new Compras();
        // IMPORTANTE: Não setar o ID manualmente, deixe o @GeneratedValue do JPA trabalhar!
        entidade.setChaveUsuario(dto.getChaveUsuario());
        entidade.setProdutos(produto);
        entidade.setQuantidade(dto.getQuantidade());
        entidade.setValorCompra(dto.getValorCompra());
        entidade.setValorTotal(dto.getValorTotal());
        entidade.setDataCompra(dto.getDataCompra());
        entidade.setAtiva(true);
        entidade.setCompraAceita(false);

        return mapper.toDTO(repository.save(entidade));
    }

    public List<ComprasDTO> listarTodos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    public ComprasDTO buscarPorId(Long id) {
        Compras compra = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada com o ID: " + id));
        return mapper.toDTO(compra);
    }

    @Transactional
    public ComprasDTO atualizar(Long id, ComprasDTO dto) {
        Compras compraExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada com o ID: " + id));

        mapper.updateEntityFromDto(dto, compraExistente);

        return mapper.toDTO(repository.save(compraExistente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Não é possível deletar. Compra não encontrada com o ID: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public ComprasDTO aprovarCompra(Long id) {
        Compras compra = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada ID: " + id));

        compra.setCompraAceita(true);
        compra.setJustificativaRecusa(null);
        compra.setChaveAdministrador(KeyGeneratorUtil.gerarChaveCompras()); // Chave de segurança para liberar pagamento

        return mapper.toDTO(repository.save(compra));
    }

    @Transactional
    public ComprasDTO recusarCompra(Long id, String justificativa) {
        Compras compra = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada ID: " + id));

        compra.setCompraAceita(false);
        compra.setJustificativaRecusa(justificativa);
        compra.setChaveAdministrador(null);

        return mapper.toDTO(repository.save(compra));
    }
}