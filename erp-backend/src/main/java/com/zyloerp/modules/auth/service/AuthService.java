package com.zyloerp.modules.auth.service;

import com.zyloerp.core.security.JwtTokenProvider;
import com.zyloerp.modules.auth.dto.LoginRequest;
import com.zyloerp.modules.auth.dto.LoginResponse;
import com.zyloerp.modules.usuario.dto.UsuarioResponseDTO;
import com.zyloerp.modules.usuario.model.HistoricoAcesso;
import com.zyloerp.modules.usuario.model.Usuario;
import com.zyloerp.modules.usuario.repository.HistoricoAcessoRepository;
import com.zyloerp.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final HistoricoAcessoRepository historicoAcessoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public LoginResponse login(LoginRequest request, String ip, String userAgent) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail().toLowerCase().trim(),
                            request.getSenha()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Usuario usuario = (Usuario) authentication.getPrincipal();

            String token = jwtTokenProvider.generateToken(authentication);

            registrarAcesso(usuario, ip, userAgent, true, null);

            usuario.setUltimoAcesso(LocalDateTime.now());
            usuarioRepository.save(usuario); // persiste ultimoAcesso

            return LoginResponse.builder()
                    .token(token)
                    .tipo("Bearer")
                    .expiresIn(28800000L)
                    .usuario(UsuarioResponseDTO.fromEntity(usuario))
                    .build();

        } catch (AuthenticationException e) {
            // Captura BadCredentialsException, InternalAuthenticationServiceException,
            // DisabledException, LockedException — qualquer falha de autenticação
            log.warn("Falha no login para email={} | motivo={}", request.getEmail(), e.getMessage());
            throw new BadCredentialsException("Email ou senha incorretos");
        }
    }

    private void registrarAcesso(Usuario usuario, String ip, String userAgent,
                                 boolean sucesso, String motivo) {
        HistoricoAcesso acesso = HistoricoAcesso.builder()
                .usuario(usuario)
                .ipAcesso(ip)
                .userAgent(userAgent)
                .dataHoraAcesso(LocalDateTime.now())
                .sucesso(sucesso)
                .motivoFalha(motivo)
                .build();
        historicoAcessoRepository.save(acesso);
    }
}