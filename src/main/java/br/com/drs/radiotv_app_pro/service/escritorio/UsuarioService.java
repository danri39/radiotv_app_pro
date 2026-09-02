package br.com.drs.radiotv_app_pro.service.escritorio;

import br.com.drs.radiotv_app_pro.dto.escritorio.LoginResponseDTO;
import br.com.drs.radiotv_app_pro.dto.escritorio.UsuarioDTO;
import br.com.drs.radiotv_app_pro.mapper.escritorio.UsuarioMapper;
import br.com.drs.radiotv_app_pro.model.escritorio.Usuario;
import br.com.drs.radiotv_app_pro.model.enuns.Papel;
import br.com.drs.radiotv_app_pro.model.enuns.Setor;
import br.com.drs.radiotv_app_pro.repository.escritorio.UsuarioRepository;
import br.com.drs.radiotv_app_pro.util.JwtUtil;
import br.com.drs.radiotv_app_pro.util.KeyGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    public List<UsuarioDTO> buscarTodos() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toDto)
                .toList();
    }

    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));
        return usuarioMapper.toDto(usuario);
    }

    @Transactional
    public LoginResponseDTO login(UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findByChaveUsuario(dto.getChaveUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Chave de usuário inválida."));

        if (!usuario.getAtivo()) throw new IllegalStateException("Usuário inativo.");
        if (usuario.getPrimeiroAcesso()) throw new IllegalStateException("Realize o primeiro acesso.");
        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) throw new IllegalArgumentException("Senha incorreta.");

        usuario.setAcessoSistema(LocalDateTime.now());
        usuarioRepository.save(usuario);

        return new LoginResponseDTO(jwtUtil.gerarToken(usuario), usuarioMapper.toDto(usuario));
    }

    @Transactional
    public UsuarioDTO cadastrar(UsuarioDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) throw new IllegalArgumentException("E-mail já cadastrado.");

        Usuario novoUsuario = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .chaveUsuario(KeyGeneratorUtil.gerarChaveUsuario(Setor.OUTROS))
                .chavePrimeiroAcesso(KeyGeneratorUtil.gerarChavePrimeiroAcesso())
                .papeis(Papel.CONVIDADO)
                .setores(List.of(Setor.OUTROS))
                .acessoEscritorio(false)
                .primeiroAcesso(true)
                .ativo(true)
                .build();

        novoUsuario = usuarioRepository.save(novoUsuario);
        emailService.enviarEmailCadastro(novoUsuario.getEmail(), novoUsuario.getChaveUsuario(), novoUsuario.getChavePrimeiroAcesso());
        return usuarioMapper.toDto(novoUsuario);
    }

    @Transactional
    public UsuarioDTO atualizar(Long id, UsuarioDTO dto) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));

        usuarioMapper.updateEntityFromDto(dto, usuarioExistente);

        usuarioExistente.setUsuarioId(id);

        Usuario salvo = usuarioRepository.save(usuarioExistente);
        return usuarioMapper.toDto(salvo);
    }

    @Transactional
    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) throw new RuntimeException("Usuário não encontrado.");
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public void definirSenhaPrimeiroAcesso(UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findByChavePrimeiroAcesso(dto.getChavePrimeiroAcesso())
                .orElseThrow(() -> new IllegalArgumentException("Chave inválida."));
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setPrimeiroAcesso(false);
        usuario.setChavePrimeiroAcesso(null);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void solicitarTrocaSenha(UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findByChaveUsuario(dto.getChaveUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Chave inválida."));
        usuario.setChaveTrocaSenha(KeyGeneratorUtil.gerarChaveTrocaSenha());
        usuarioRepository.save(usuario);
        emailService.enviarEmailTrocaSenha(usuario.getEmail(), usuario.getChaveTrocaSenha());
    }

    @Transactional
    public void efetivarTrocaSenha(UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findByChaveTrocaSenha(dto.getChaveTrocaSenha())
                .orElseThrow(() -> new IllegalArgumentException("Chave inválida."));
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setChaveTrocaSenha(null);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public Optional<Usuario.UsuarioResumoProjection> buscarPorNome(String nome) {
        return usuarioRepository.encontrarNomeEPapelPorNome(nome);
    }
}