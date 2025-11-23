package com.edu.unimagdalena.nearby.services.impl;

import com.edu.unimagdalena.nearby.entities.*;
import com.edu.unimagdalena.nearby.enums.Rol;
import com.edu.unimagdalena.nearby.repositories.*;
import com.edu.unimagdalena.nearby.services.AuthService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final CuentaUsuarioRepository cuentaRepo;
    private final PerfilRepository perfilRepo;
    private final ArrendadorRepository arrendadorRepo;
    private final EstudianteRepository estudianteRepo;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // In-memory token stores (simple; for production use persistent store or JWT)
    private final Map<String, UUID> accessTokenToUser = new ConcurrentHashMap<>();
    private final Map<String, UUID> refreshTokenToUser = new ConcurrentHashMap<>();
    private final Map<String, String> emailToOtp = new ConcurrentHashMap<>();
    private final Map<String, String> passwordResetTokens = new ConcurrentHashMap<>();
    private final Map<String, Long> otpExpiry = new ConcurrentHashMap<>();
    private final Map<String, Long> refreshExpiry = new ConcurrentHashMap<>();

    // token lifetimes (ms)
    private final long OTP_TTL = 5 * 60 * 1000; // 5 minutes
    private final long REFRESH_TTL = 7 * 24 * 60 * 60 * 1000L; // 7 days

    public AuthServiceImpl(CuentaUsuarioRepository cuentaRepo,
                           PerfilRepository perfilRepo,
                           ArrendadorRepository arrendadorRepo,
                           EstudianteRepository estudianteRepo) {
        this.cuentaRepo = cuentaRepo;
        this.perfilRepo = perfilRepo;
        this.arrendadorRepo = arrendadorRepo;
        this.estudianteRepo = estudianteRepo;
    }

    // Helpers
    private CuentaUsuario sanitize(CuentaUsuario u) {
        if (u == null) return null;
        CuentaUsuario s = new CuentaUsuario();
        s.setId(u.getId());
        s.setUsuario(u.getUsuario());
        s.setRol(u.getRol());
        s.setActivo(u.isActivo());
        s.setFechaRegistro(u.getFechaRegistro());
        return s;
    }

    private String genToken() { return UUID.randomUUID().toString(); }

    @Override
    public CuentaUsuario registerStudent(Map<String, Object> payload) {
        if (payload == null || payload.get("usuario") == null || payload.get("password") == null) {
            throw new RuntimeException("usuario y password son obligatorios");
        }
        String usuario = payload.get("usuario").toString();
        String rawPassword = payload.get("password").toString();

        if (cuentaRepo.findByUsuario(usuario).isPresent()) {
            throw new RuntimeException("Usuario ya existe");
        }

        CuentaUsuario account = new CuentaUsuario();
        account.setUsuario(usuario);
        account.setContrasenaHasheada(passwordEncoder.encode(rawPassword));
        account.setActivo(true);
        account.setFechaRegistro(LocalDateTime.now());
        // asignar rol de forma segura evitando referencia directa a la constante
        try {
            account.setRol(Rol.valueOf("STUDENT"));
        } catch (Exception ex) {
            account.setRol(null);
        }

        cuentaRepo.save(account);

        // create profile
        Perfil perfil = new Perfil();
        perfil.setCuentaUsuario(account);
        perfil.setNombres(payload.getOrDefault("nombres", "").toString());
        perfil.setApellidos(payload.getOrDefault("apellidos", "").toString());
        perfilRepo.save(perfil);

        // Optionally create Estudiante record if more fields provided
        if (payload.containsKey("codigoInstitucional") || payload.containsKey("correoInstitucional")) {
            Estudiante e = new Estudiante();
            e.setUsuario(account);
            e.setCorreoInstitucional(payload.getOrDefault("correoInstitucional", "").toString());
            try {
                e.setCodigoInstitucional(Integer.parseInt(payload.getOrDefault("codigoInstitucional", "0").toString()));
            } catch (Exception ex) {
                e.setCodigoInstitucional(0);
            }
            try {
                e.setCedula(Integer.parseInt(payload.getOrDefault("cedula", "0").toString()));
            } catch (Exception ex) {
                e.setCedula(0);
            }
            try {
                e.setSemestreActual(Integer.parseInt(payload.getOrDefault("semestreActual", "0").toString()));
            } catch (Exception ex) {
                e.setSemestreActual(0);
            }
            // programaDeEstudio and ubicacion left null unless provided and repositories available
            estudianteRepo.save(e);
        }

        // generate OTP to verify email/identity (optional)
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        emailToOtp.put(usuario, otp);
        otpExpiry.put(usuario, System.currentTimeMillis() + OTP_TTL);
        // In real app send OTP by email - here we just keep it in memory
        return sanitize(account);
    }

    @Override
    public CuentaUsuario registerOwner(Map<String, Object> payload) {
        if (payload == null || payload.get("usuario") == null || payload.get("password") == null) {
            throw new RuntimeException("usuario y password son obligatorios");
        }
        String usuario = payload.get("usuario").toString();
        String rawPassword = payload.get("password").toString();

        if (cuentaRepo.findByUsuario(usuario).isPresent()) {
            throw new RuntimeException("Usuario ya existe");
        }

        CuentaUsuario account = new CuentaUsuario();
        account.setUsuario(usuario);
        account.setContrasenaHasheada(passwordEncoder.encode(rawPassword));
        account.setActivo(true);
        account.setFechaRegistro(LocalDateTime.now());
        try {
            account.setRol(Rol.valueOf("OWNER"));
        } catch (Exception ex) {
            account.setRol(null);
        }

        cuentaRepo.save(account);

        // create profile
        Perfil perfil = new Perfil();
        perfil.setCuentaUsuario(account);
        perfil.setNombres(payload.getOrDefault("nombres", "").toString());
        perfil.setApellidos(payload.getOrDefault("apellidos", "").toString());
        perfilRepo.save(perfil);

        // create Arrendador entity minimal
        Arrendador arr = new Arrendador();
        arr.setUsuario(account);
        arr.setPerfil(perfil);
        try {
            arr.setCedula(Integer.parseInt(payload.getOrDefault("cedula", "0").toString()));
        } catch (Exception ex) {
            arr.setCedula(0);
        }
        arrendadorRepo.save(arr);

        // generate OTP
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        emailToOtp.put(usuario, otp);
        otpExpiry.put(usuario, System.currentTimeMillis() + OTP_TTL);

        return sanitize(account);
    }

    @Override
    public Map<String, Object> login(Map<String, Object> credentials) {
        if (credentials == null || credentials.get("usuario") == null || credentials.get("password") == null) {
            throw new RuntimeException("usuario y password son obligatorios");
        }
        String usuario = credentials.get("usuario").toString();
        String rawPassword = credentials.get("password").toString();

        CuentaUsuario user = cuentaRepo.findByUsuario(usuario).orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
        if (!passwordEncoder.matches(rawPassword, user.getContrasenaHasheada())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        if (!user.isActivo()) {
            throw new RuntimeException("Usuario inactivo");
        }

        String accessToken = genToken();
        String refreshToken = genToken();
        accessTokenToUser.put(accessToken, user.getId());
        refreshTokenToUser.put(refreshToken, user.getId());
        refreshExpiry.put(refreshToken, System.currentTimeMillis() + REFRESH_TTL);

        Map<String, Object> res = new HashMap<>();
        res.put("accessToken", accessToken);
        res.put("refreshToken", refreshToken);
        res.put("user", sanitize(user));
        res.put("issuedAt", Instant.now().toString());
        return res;
    }

    @Override
    public void logout(String authHeader, Map<String, Object> payload) {
        // Accept token in Authorization: Bearer <token> or in payload.token
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else if (payload != null && payload.get("token") != null) {
            token = payload.get("token").toString();
        }
        if (token == null) return;
        accessTokenToUser.remove(token);
        // remove refresh tokens linked to user if provided in payload.tokenType==refresh? optional:
        if (refreshTokenToUser.containsKey(token)) {
            refreshTokenToUser.remove(token);
        }
    }

    @Override
    public boolean verifyEmail(Map<String, Object> payload) {
        if (payload == null || payload.get("usuario") == null || payload.get("otp") == null) {
            throw new RuntimeException("usuario y otp son obligatorios");
        }
        String usuario = payload.get("usuario").toString();
        String otp = payload.get("otp").toString();
        String expected = emailToOtp.get(usuario);
        Long exp = otpExpiry.get(usuario);
        if (expected == null || exp == null || System.currentTimeMillis() > exp) {
            return false;
        }
        boolean ok = expected.equals(otp);
        if (ok) {
            emailToOtp.remove(usuario);
            otpExpiry.remove(usuario);
        }
        return ok;
    }

    @Override
    public boolean resendOtp(Map<String, Object> payload) {
        if (payload == null || payload.get("usuario") == null) {
            throw new RuntimeException("usuario es obligatorio");
        }
        String usuario = payload.get("usuario").toString();
        if (cuentaRepo.findByUsuario(usuario).isEmpty()) {
            throw new RuntimeException("Usuario no existe");
        }
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        emailToOtp.put(usuario, otp);
        otpExpiry.put(usuario, System.currentTimeMillis() + OTP_TTL);
        // In real app, send email
        return true;
    }

    @Override
    public boolean forgotPassword(Map<String, Object> payload) {
        if (payload == null || payload.get("usuario") == null) {
            throw new RuntimeException("usuario es obligatorio");
        }
        String usuario = payload.get("usuario").toString();
        CuentaUsuario user = cuentaRepo.findByUsuario(usuario).orElse(null);
        if (user == null) {
            // do not reveal existence
            return true;
        }
        String token = genToken();
        passwordResetTokens.put(token, usuario);
        // in real app send token via email
        return true;
    }

    @Override
    public boolean resetPassword(Map<String, Object> payload) {
        if (payload == null || payload.get("token") == null || payload.get("newPassword") == null) {
            throw new RuntimeException("token y newPassword son obligatorios");
        }
        String token = payload.get("token").toString();
        String newPassword = payload.get("newPassword").toString();
        String usuario = passwordResetTokens.get(token);
        if (usuario == null) return false;
        CuentaUsuario user = cuentaRepo.findByUsuario(usuario).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setContrasenaHasheada(passwordEncoder.encode(newPassword));
        cuentaRepo.save(user);
        passwordResetTokens.remove(token);
        return true;
    }

    @Override
    public CuentaUsuario me(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization header faltante");
        }
        String token = authHeader.substring(7);
        UUID userId = accessTokenToUser.get(token);
        if (userId == null) throw new RuntimeException("Token inválido");
        CuentaUsuario user = cuentaRepo.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return sanitize(user);
    }

    @Override
    public Map<String, Object> refreshToken(Map<String, Object> payload) {
        if (payload == null || payload.get("refreshToken") == null) {
            throw new RuntimeException("refreshToken es obligatorio");
        }
        String refresh = payload.get("refreshToken").toString();
        UUID userId = refreshTokenToUser.get(refresh);
        Long exp = refreshExpiry.get(refresh);
        if (userId == null || exp == null || System.currentTimeMillis() > exp) {
            throw new RuntimeException("Refresh token inválido o expirado");
        }
        // rotate tokens
        String newAccess = genToken();
        String newRefresh = genToken();
        accessTokenToUser.put(newAccess, userId);
        refreshTokenToUser.put(newRefresh, userId);
        refreshExpiry.put(newRefresh, System.currentTimeMillis() + REFRESH_TTL);
        // remove old refresh
        refreshTokenToUser.remove(refresh);
        refreshExpiry.remove(refresh);

        CuentaUsuario user = cuentaRepo.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Map<String, Object> res = new HashMap<>();
        res.put("accessToken", newAccess);
        res.put("refreshToken", newRefresh);
        res.put("user", sanitize(user));
        return res;
    }
}
