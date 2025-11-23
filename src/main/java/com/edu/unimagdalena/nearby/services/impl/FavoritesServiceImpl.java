package com.edu.unimagdalena.nearby.services.impl;

import com.edu.unimagdalena.nearby.entities.*;
import com.edu.unimagdalena.nearby.repositories.*;
import com.edu.unimagdalena.nearby.services.FavoritesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class FavoritesServiceImpl implements FavoritesService {

    private final FavoritoRepository favoritoRepo;
    private final CuentaUsuarioRepository cuentaRepo;
    private final PropiedadRepository propiedadRepo;
    private final ListaPersonalizadaRepository listaRepo;

    public FavoritesServiceImpl(FavoritoRepository favoritoRepo,
                                CuentaUsuarioRepository cuentaRepo,
                                PropiedadRepository propiedadRepo,
                                ListaPersonalizadaRepository listaRepo) {
        this.favoritoRepo = favoritoRepo;
        this.cuentaRepo = cuentaRepo;
        this.propiedadRepo = propiedadRepo;
        this.listaRepo = listaRepo;
    }

    @Override
    public Favorito addFavorite(UUID studentId, UUID propertyId) {
        if (favoritoRepo.existsByEstudiante_IdAndPropiedad_Id(studentId, propertyId)) {
            throw new RuntimeException("Propiedad ya en favoritos");
        }
        CuentaUsuario estudiante = cuentaRepo.findById(studentId).orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        Propiedad propiedad = propiedadRepo.findById(propertyId).orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
        Favorito f = new Favorito();
        f.setEstudiante(estudiante);
        f.setPropiedad(propiedad);
        f.setFechaRegistro(LocalDateTime.now());
        return favoritoRepo.save(f);
    }

    @Override
    public void removeFavorite(UUID studentId, UUID propertyId) {
        if (!favoritoRepo.existsByEstudiante_IdAndPropiedad_Id(studentId, propertyId)) {
            throw new RuntimeException("Favorito no existe");
        }
        favoritoRepo.deleteByEstudiante_IdAndPropiedad_Id(studentId, propertyId);
    }

    @Override
    public List<Propiedad> favoritesByStudent(UUID studentId) {
        List<Favorito> favs = favoritoRepo.findByEstudiante_Id(studentId);
        return favs.stream().map(Favorito::getPropiedad).collect(Collectors.toList());
    }

    @Override
    public ListaPersonalizada createList(Map<String, Object> payload) {
        if (payload == null || payload.get("perfilId") == null || payload.get("nombreLista") == null) {
            throw new RuntimeException("perfilId y nombreLista son obligatorios");
        }
        UUID perfilId = UUID.fromString(payload.get("perfilId").toString());
        // perfil existence not strictly validated here (can be added)
        ListaPersonalizada l = new ListaPersonalizada();
        Perfil p = new Perfil();
        p.setId(perfilId);
        l.setPerfilPropietario(p);
        l.setNombreLista(payload.get("nombreLista").toString());
        return listaRepo.save(l);
    }

    @Override
    public ListaPersonalizada updateList(UUID id, Map<String, Object> payload) {
        ListaPersonalizada l = listaRepo.findById(id).orElseThrow(() -> new RuntimeException("Lista no encontrada"));
        if (payload.containsKey("nombreLista")) {
            l.setNombreLista(payload.get("nombreLista").toString());
        }
        return listaRepo.save(l);
    }

    @Override
    public void deleteList(UUID id) {
        ListaPersonalizada l = listaRepo.findById(id).orElseThrow(() -> new RuntimeException("Lista no encontrada"));
        listaRepo.delete(l);
    }

    @Override
    public ListaPersonalizada addPropertyToList(UUID listId, UUID propertyId) {
        ListaPersonalizada l = listaRepo.findById(listId).orElseThrow(() -> new RuntimeException("Lista no encontrada"));
        Propiedad p = propiedadRepo.findById(propertyId).orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
        // create ContenidoLista and add
        ContenidoLista cl = new ContenidoLista();
        cl.setListaPersonalizada(l);
        cl.setAgregado(p.getId());
        cl.setFechaAgregado(LocalDateTime.now());
        cl.setTipoLista("propiedad");
        cl.setTipoContenido(null);
        cl.setPesoVinculo(1);
        if (l.getElementos() == null) l.setElementos(new ArrayList<>());
        l.getElementos().add(cl);
        return listaRepo.save(l);
    }

    @Override
    public void removePropertyFromList(UUID listId, UUID propertyId) {
        ListaPersonalizada l = listaRepo.findById(listId).orElseThrow(() -> new RuntimeException("Lista no encontrada"));
        if (l.getElementos() == null || l.getElementos().isEmpty()) throw new RuntimeException("Lista vacía");
        boolean removed = l.getElementos().removeIf(e -> propertyId.equals(e.getAgregado()));
        if (!removed) throw new RuntimeException("Propiedad no encontrada en la lista");
        listaRepo.save(l);
    }
}
