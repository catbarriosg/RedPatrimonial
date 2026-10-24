package com.redpatrimonial.ms_rutas.config;

import com.redpatrimonial.ms_rutas.dtos.AsignacionDTO;
import com.redpatrimonial.ms_rutas.dtos.ParadaDTO;
import com.redpatrimonial.ms_rutas.dtos.RutaDTO;
import com.redpatrimonial.ms_rutas.dtos.SalidaDTO;
import com.redpatrimonial.ms_rutas.enums.EstadoSolicitud;
import com.redpatrimonial.ms_rutas.enums.TipoRuta;
import com.redpatrimonial.ms_rutas.models.DisponibilidadGuia;
import com.redpatrimonial.ms_rutas.models.Ruta;
import com.redpatrimonial.ms_rutas.repositories.RutaRepository;
import com.redpatrimonial.ms_rutas.services.CoordinacionGuiasService;
import com.redpatrimonial.ms_rutas.services.RutaService;
import com.redpatrimonial.ms_rutas.services.SalidaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Rutas oficiales, salidas y disponibilidad de prueba. Los IDs de recintos y usuarios coinciden con los otros microservicios.
@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private static final long ID_DOCENTE = 1L;
    private static final long ID_ENCARGADO_MERCED = 3L;
    private static final long ID_GUIA_UNO = 4L;
    private static final long ID_GUIA_DOS = 5L;

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private RutaService rutaService;

    @Autowired
    private SalidaService salidaService;

    @Autowired
    private CoordinacionGuiasService coordinacionService;

    @Override
    public void run(String... args) {
        if (rutaRepository.count() > 0) {
            log.info("[DataLoader] La base ya tiene rutas, no se cargan datos de prueba");
            return;
        }

        // Minutos de traslado calculados a pie entre recintos consecutivos
        Ruta basilicas = crearRuta("Ruta de las Tres Basílicas", "La Merced, Sacramentinos y El Salvador en un solo recorrido.",
                4.8, new long[]{4, 5, 6}, new int[]{0, 31, 33});
        Ruta coloniales = crearRuta("Tesoros Coloniales del Centro", "Los templos más antiguos en torno a la Plaza de Armas.",
                1.7, new long[]{1, 10, 2, 4}, new int[]{0, 2, 15, 7});
        crearRuta("Circuito Sacro Recoleta", "Desde el Barrio Lastarria hasta la Recoleta Dominica.",
                2.3, new long[]{7, 3, 8}, new int[]{0, 13, 18});

        agregarDisponibilidad(ID_GUIA_UNO, 1, 9, 13);
        agregarDisponibilidad(ID_GUIA_UNO, 3, 15, 18);
        agregarDisponibilidad(ID_GUIA_DOS, 6, 10, 14);

        // Lunes 9 de noviembre de 2026: solicitud pendiente en San Francisco
        planificar(coloniales, LocalDate.of(2026, 11, 9), LocalTime.of(10, 0), 32, "2° medio", 1L);
        // Lunes 16 de noviembre de 2026: solicitud en La Merced, ya asignada al guía 4
        planificar(basilicas, LocalDate.of(2026, 11, 16), LocalTime.of(9, 30), 28, "8° básico", 4L);
        coordinacionService.listarSolicitudes(null, 4L, null).stream()
                .filter(s -> s.getEstado() == EstadoSolicitud.PENDIENTE)
                .findFirst()
                .ifPresent(s -> {
                    AsignacionDTO asignacion = new AsignacionDTO();
                    asignacion.setIdSolicitud(s.getId());
                    asignacion.setIdGuia(ID_GUIA_UNO);
                    asignacion.setIdEncargado(ID_ENCARGADO_MERCED);
                    coordinacionService.asignarGuia(asignacion);
                });

        log.info("[DataLoader] Datos de prueba cargados: {} rutas oficiales", rutaRepository.count());
    }

    private Ruta crearRuta(String nombre, String descripcion, double km, long[] recintos, int[] traslados) {
        List<ParadaDTO> paradas = new ArrayList<>();
        for (int i = 0; i < recintos.length; i++) {
            ParadaDTO parada = new ParadaDTO();
            parada.setIdRecinto(recintos[i]);
            parada.setTrasladoDesdeAnteriorMin(traslados[i]);
            paradas.add(parada);
        }
        RutaDTO dto = new RutaDTO();
        dto.setNombre(nombre);
        dto.setDescripcion(descripcion);
        dto.setTipo(TipoRuta.OFICIAL);
        dto.setDistanciaKm(km);
        dto.setParadas(paradas);
        return rutaService.crear(dto);
    }

    private void agregarDisponibilidad(long idGuia, int dia, int desde, int hasta) {
        DisponibilidadGuia bloque = new DisponibilidadGuia();
        bloque.setDiaSemana(dia);
        bloque.setHoraInicio(LocalTime.of(desde, 0));
        bloque.setHoraFin(LocalTime.of(hasta, 0));
        coordinacionService.agregarDisponibilidad(idGuia, bloque);
    }

    private void planificar(Ruta ruta, LocalDate fecha, LocalTime hora, int grupo, String nivel, long idRecintoGuia) {
        SalidaDTO dto = new SalidaDTO();
        dto.setIdDocente(ID_DOCENTE);
        dto.setIdRuta(ruta.getId());
        dto.setFecha(fecha);
        dto.setHoraInicio(hora);
        dto.setTamanoGrupo(grupo);
        dto.setNivelEducativo(nivel);
        dto.setSolicitarGuia(true);
        dto.setIdRecintoGuia(idRecintoGuia);
        salidaService.planificar(dto);
    }
}
