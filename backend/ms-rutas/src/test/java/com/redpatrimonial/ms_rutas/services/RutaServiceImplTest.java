package com.redpatrimonial.ms_rutas.services;

import com.redpatrimonial.ms_rutas.dtos.ParadaDTO;
import com.redpatrimonial.ms_rutas.dtos.RutaDTO;
import com.redpatrimonial.ms_rutas.enums.TipoRuta;
import com.redpatrimonial.ms_rutas.exceptions.ReglaNegocioException;
import com.redpatrimonial.ms_rutas.models.Ruta;
import com.redpatrimonial.ms_rutas.repositories.RutaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RutaServiceImplTest {

    @Mock
    private RutaRepository repository;

    @InjectMocks
    private RutaServiceImpl service;

    private ParadaDTO parada(long idRecinto, int traslado, int visita) {
        ParadaDTO parada = new ParadaDTO();
        parada.setIdRecinto(idRecinto);
        parada.setTrasladoDesdeAnteriorMin(traslado);
        parada.setTiempoVisitaMin(visita);
        return parada;
    }

    private RutaDTO ruta(List<ParadaDTO> paradas) {
        RutaDTO dto = new RutaDTO();
        dto.setNombre("Ruta de prueba");
        dto.setTipo(TipoRuta.PERSONALIZADA);
        dto.setParadas(paradas);
        return dto;
    }

    @Test
    void laDuracionEsLaSumaDeTrasladosYVisitasSinContarTrasladoDeLaPrimera() {
        when(repository.save(any(Ruta.class))).thenAnswer(inv -> inv.getArgument(0));

        // El traslado de la primera parada se ignora aunque venga informado
        Ruta creada = service.crear(ruta(List.of(parada(1, 99, 20), parada(2, 10, 30), parada(3, 15, 20))));

        assertEquals(20 + (10 + 30) + (15 + 20), creada.getDuracionTotalMin());
        assertEquals(List.of(1, 2, 3), creada.getParadas().stream().map(p -> p.getOrden()).toList());
    }

    @Test
    void unRecintoRepetidoNoSePermite() {
        assertThrows(ReglaNegocioException.class,
                () -> service.crear(ruta(List.of(parada(1, 0, 20), parada(1, 5, 20)))));
        verify(repository, never()).save(any());
    }
}
