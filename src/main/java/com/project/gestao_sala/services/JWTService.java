package com.project.gestao_sala.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class JWTService {

    private final SecretKey SECRET =Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRACAO = 1000 * 60 * 60 * 2;

    public String gerarToken(String email, String nome,String[] permissoes) {
        return Jwts.builder()
                .setSubject(email)
                .claim("nome",nome)
                .addClaims(Map.of("permissoes", permissoes))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRACAO))
                .signWith( SECRET)
                .compact();
    }
    public Claims decodificarToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public List<String> obterPermissoes(String token) {
        Claims claims = decodificarToken(token);
        return (List<String>) claims.get("permissoes");
    }

    public boolean verificarPermissao(String token, String permissao) {
        List<String> permissoes = obterPermissoes(token);
        return permissoes.contains(permissao);
    }

}
