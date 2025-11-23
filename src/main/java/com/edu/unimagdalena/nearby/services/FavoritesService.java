package com.edu.unimagdalena.nearby.services;

import com.edu.unimagdalena.nearby.entities.ListaPersonalizada;
import com.edu.unimagdalena.nearby.entities.Propiedad;
import com.edu.unimagdalena.nearby.entities.Favorito;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface FavoritesService {
    Favorito addFavorite(UUID studentId, UUID propertyId);
    void removeFavorite(UUID studentId, UUID propertyId);
    List<Propiedad> favoritesByStudent(UUID studentId);

    ListaPersonalizada createList(Map<String, Object> payload);
    ListaPersonalizada updateList(UUID id, Map<String, Object> payload);
    void deleteList(UUID id);
    ListaPersonalizada addPropertyToList(UUID listId, UUID propertyId);
    void removePropertyFromList(UUID listId, UUID propertyId);
}
