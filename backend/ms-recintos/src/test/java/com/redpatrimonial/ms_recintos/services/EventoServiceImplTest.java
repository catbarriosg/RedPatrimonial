package com.redpatrimonial.ms_recintos.services;

import com.redpatrimonial.ms_recintos.dtos.ModeracionDTO;
import com.redpatrimonial.ms_recintos.enums.AccionModeracion;
import com.redpatrimonial.ms_recintos.enums.Visibilidad;
import com.redpatrimonial.ms_recintos.exceptions.ReglaNegocioException;
import com.redpatrimonial.ms_recintos.models.Evento;
import com.redpatrimonial.ms_recintos.models.Moderacion;
import com.redpatrimonial.ms_recintos.repositories.EventoRepository;
import com.redpatrimonial.ms_recintos.repositories.ModeracionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceImplTest {

    @Mock
    private EventoRepository repository;

    @Mock
    private ModeracionRepository moderacionRepository;

    @Mock
    private RecintoService recintoService;

    @InjectMocks
    private EventoServiceImpl service;

    private Evento evento(long id, double lat, double lng) {
        Evento evento = new Evento();
        evento.setId(id);
        evento.setTitulo("Evento " + id);
        evento.setLatitud(lat);
        evento.setLongitud(lng);
        return evento;
    }

    @Test
    void cercanosFiltraPorRadioYOrdenaPorDistancia() {
        Evento lejano = evento(1, -33.4530, -70.6490);   // ~1,7 km de la Plaza de Armas
        Evento cercano = evento(2, -33.4370, -70.6467);  // ~0,4 km
        Evento fuera = evento(3, -33.3000, -70.5000);    // más de 15 km
        when(repository.findByVisibilidadOrderByFechaInicioAsc(Visibilidad.VISIBLE))
                .thenReturn(List.of(lejano, cercano, fuera));

        List<Evento> resultado = service.obtenerCercanos(-33.4378, -70.6504, 3);

        assertEquals(List.of(cercano, lejano), resultado);
    }

    @Test
    void radioCeroNoEsValido() {
        assertThrows(ReglaNegocioException.class, () -> service.obtenerCercanos(-33.4, -70.6, 0));
    }

    @Test
    void ocultarEventoCambiaVisibilidadYRegistraModeracion() {
        when(repository.findById(5L)).thenReturn(Optional.of(evento(5, -33.44, -70.65)));
        when(repository.save(any(Evento.class))).thenAnswer(inv -> inv.getArgument(0));

        ModeracionDTO dto = new ModeracionDTO();
        dto.setAccion(AccionModeracion.OCULTAR);
        dto.setIdAdmin(6L);
        dto.setMotivo("Contenido duplicado");

        Evento moderado = service.moderar(5L, dto);

        assertEquals(Visibilidad.OCULTO, moderado.getVisibilidad());
        verify(moderacionRepository).save(any(Moderacion.class));
    }
}
