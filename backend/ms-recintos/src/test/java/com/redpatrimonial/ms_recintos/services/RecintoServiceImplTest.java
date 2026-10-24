package com.redpatrimonial.ms_recintos.services;

import com.redpatrimonial.ms_recintos.dtos.CambioEstadoDTO;
import com.redpatrimonial.ms_recintos.enums.EstadoApertura;
import com.redpatrimonial.ms_recintos.exceptions.RecursoNoEncontradoException;
import com.redpatrimonial.ms_recintos.exceptions.ReglaNegocioException;
import com.redpatrimonial.ms_recintos.models.HistorialEstado;
import com.redpatrimonial.ms_recintos.models.Recinto;
import com.redpatrimonial.ms_recintos.repositories.HistorialEstadoRepository;
import com.redpatrimonial.ms_recintos.repositories.RecintoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecintoServiceImplTest {

    @Mock
    private RecintoRepository repository;

    @Mock
    private HistorialEstadoRepository historialRepository;

    @InjectMocks
    private RecintoServiceImpl service;

    private Recinto recinto(EstadoApertura estado) {
        Recinto recinto = new Recinto();
        recinto.setId(1L);
        recinto.setNombreOficial("Iglesia de prueba");
        recinto.setEstadoActual(estado);
        recinto.setActivo(true);
        return recinto;
    }

    @Test
    void cambiarEstadoGuardaElNuevoEstadoYRegistraHistorial() {
        when(repository.findById(1L)).thenReturn(Optional.of(recinto(EstadoApertura.ABIERTO)));
        when(repository.save(any(Recinto.class))).thenAnswer(inv -> inv.getArgument(0));

        CambioEstadoDTO dto = new CambioEstadoDTO();
        dto.setEstado(EstadoApertura.CERRADO);
        dto.setIdUsuario(2L);

        Recinto actualizado = service.cambiarEstado(1L, dto);

        assertEquals(EstadoApertura.CERRADO, actualizado.getEstadoActual());
        ArgumentCaptor<HistorialEstado> captor = ArgumentCaptor.forClass(HistorialEstado.class);
        verify(historialRepository).save(captor.capture());
        assertEquals(EstadoApertura.ABIERTO, captor.getValue().getEstadoAnterior());
        assertEquals(EstadoApertura.CERRADO, captor.getValue().getEstadoNuevo());
        assertEquals(2L, captor.getValue().getIdUsuario());
    }

    @Test
    void cambiarAlMismoEstadoLanzaReglaDeNegocio() {
        when(repository.findById(1L)).thenReturn(Optional.of(recinto(EstadoApertura.ABIERTO)));

        CambioEstadoDTO dto = new CambioEstadoDTO();
        dto.setEstado(EstadoApertura.ABIERTO);

        assertThrows(ReglaNegocioException.class, () -> service.cambiarEstado(1L, dto));
        verify(historialRepository, never()).save(any());
    }

    @Test
    void recintoInactivoSeConsideraNoEncontrado() {
        Recinto inactivo = recinto(EstadoApertura.ABIERTO);
        inactivo.setActivo(false);
        when(repository.findById(1L)).thenReturn(Optional.of(inactivo));

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(1L));
    }
}
