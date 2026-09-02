package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.VendedorDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.VendedorMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.Funcionario;
import br.com.drs.radiotv_app_pro.model.escritorio.Vendedor;
import br.com.drs.radiotv_app_pro.repository.escritorio.FuncionarioRepository;
import br.com.drs.radiotv_app_pro.repository.escritorio.VendedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VendedorService {

    private final VendedorRepository repository;
    private final VendedorMapper mapper;
    private final FuncionarioRepository funcionarioRepository;

    public VendedorDTO salvar(VendedorDTO dto) {
        Vendedor entity = mapper.toEntity(dto);
        repository.save(entity);
        return mapper.toDto(entity);
    }

    public List<Vendedor> listarTodos() {
        return repository.findAll();
    }

    public Optional<Vendedor> buscarPorChaveUsuario(String chaveUsuario) {
        return repository.findByChaveUsuario(chaveUsuario);
    }

    @Transactional
    public VendedorDTO atualizar(Long id, VendedorDTO dto) {
        Vendedor vendedorExistente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vendedor não encontrado na base de dados."));

        if (dto.getChaveUsuario() != null && dto.getChaveUsuario() != null) {
            Funcionario func = (Funcionario) funcionarioRepository.findByChaveUsuario(dto.getChaveUsuario())
                    .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));
            vendedorExistente.setChaveUsuario(dto.getChaveUsuario());
        }

        vendedorExistente.setMetaMes(dto.getMetaMes());
        vendedorExistente.setMesAno(dto.getMesAno());
        vendedorExistente.setVendasMes(dto.getVendasMes());
        vendedorExistente.setVendasTotal(dto.getVendasTotal());
        vendedorExistente.setComissaoVendas(dto.getComissaoVendas());

        repository.save(vendedorExistente);
        return mapper.toDto(vendedorExistente);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}