package com.redpatrimonial.ms_recintos.config;

import com.redpatrimonial.ms_recintos.enums.EstadoApertura;
import com.redpatrimonial.ms_recintos.enums.TipoEvento;
import com.redpatrimonial.ms_recintos.models.*;
import com.redpatrimonial.ms_recintos.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

// Carga el catálogo sintético la primera vez que arranca el servicio. Todos los datos son ficticios o públicos.
@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private ComunaRepository comunaRepository;

    @Autowired
    private TipoRecintoRepository tipoRepository;

    @Autowired
    private RecintoRepository recintoRepository;

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Override
    public void run(String... args) {
        if (recintoRepository.count() > 0) {
            log.info("[DataLoader] La base ya tiene datos, no se carga el catálogo");
            return;
        }

        Map<String, Comuna> comunas = new HashMap<>();
        for (String nombre : new String[]{"Santiago", "Recoleta", "Providencia"}) {
            Comuna comuna = new Comuna();
            comuna.setNombre(nombre);
            comunas.put(nombre, comunaRepository.save(comuna));
        }

        Map<String, TipoRecinto> tipos = new HashMap<>();
        for (String nombre : new String[]{"Iglesia", "Catedral", "Basílica", "Capilla", "Santuario", "Museo", "Colegio", "Archivo"}) {
            TipoRecinto tipo = new TipoRecinto();
            tipo.setNombre(nombre);
            tipos.put(nombre, tipoRepository.save(tipo));
        }

        // Los IDs de encargado corresponden a los usuarios sintéticos de ms-usuarios
        Recinto sanFrancisco = crear("Iglesia de San Francisco", "Iglesia", "Santiago", "Av. Libertador Bernardo O'Higgins 834", -33.4440, -70.6476, "Colonial", "XVI", EstadoApertura.ABIERTO, 2L, comunas, tipos,
                "Inaugurada en 1618, es la construcción más antigua de Santiago y de todo Chile.");
        Recinto catedral = crear("Catedral Metropolitana", "Catedral", "Santiago", "Plaza de Armas s/n", -33.4378, -70.6510, "Neoclásico", "XVIII", EstadoApertura.EN_CELEBRACION, null, comunas, tipos,
                "Sede del Arzobispado de Santiago, frente a la Plaza de Armas.");
        crear("Iglesia de Santo Domingo", "Iglesia", "Santiago", "Santo Domingo 961", -33.4352, -70.6487, "Neoclásico", "XVIII", EstadoApertura.CERRADO, null, comunas, tipos,
                "Templo dominico construido en piedra canteada del cerro Blanco.");
        Recinto merced = crear("Basílica de la Merced", "Basílica", "Santiago", "Mac-Iver 341", -33.4370, -70.6467, "Neorrenacentista", "XVIII", EstadoApertura.ABIERTO, 3L, comunas, tipos,
                "Iglesia de la orden mercedaria, presente desde la fundación de la ciudad.");
        Recinto sacramentinos = crear("Basílica de los Sacramentinos", "Basílica", "Santiago", "Arturo Prat 471", -33.4530, -70.6490, "Neorrománico", "XX", EstadoApertura.ABIERTO, null, comunas, tipos,
                "Inspirada en el Sagrado Corazón de Montmartre, en París.");
        crear("Basílica del Salvador", "Basílica", "Santiago", "Huérfanos 1781", -33.4415, -70.6643, "Neogótico", "XIX", EstadoApertura.CERRADO, null, comunas, tipos,
                "Gran templo neogótico de ladrillo en el Barrio Brasil, en restauración.");
        crear("Capilla de la Veracruz", "Capilla", "Santiago", "José Victorino Lastarria 124", -33.4385, -70.6413, "Neogótico", "XIX", EstadoApertura.ABIERTO, null, comunas, tipos,
                "Pequeña capilla neogótica en el corazón del Barrio Lastarria.");
        Recinto recoleta = crear("Basílica de la Recoleta Dominica", "Basílica", "Recoleta", "Av. Recoleta 759", -33.4270, -70.6440, "Neoclásico", "XIX", EstadoApertura.ABIERTO, null, comunas, tipos,
                "Templo de columnas de mármol al norte del río Mapocho.");
        crear("Santuario de la Divina Providencia", "Santuario", "Providencia", "Av. Providencia 1990", -33.4290, -70.6178, "Neorrománico", "XIX", EstadoApertura.ABIERTO, null, comunas, tipos,
                "Iglesia de dos torres rojas que dio nombre a la comuna.");
        Recinto museo = crear("Museo de Arte Colonial San Francisco", "Museo", "Santiago", "Londres 4", -33.4446, -70.6470, "Colonial", "XVI", EstadoApertura.ABIERTO, null, comunas, tipos,
                "Funciona en el antiguo convento franciscano.");

        recintoRepository.findAll().forEach(this::crearHorarios);

        crearEvento(sanFrancisco, "Misa Coral de San Francisco", TipoEvento.LITURGICO, LocalDateTime.of(2026, 11, 22, 11, 0));
        crearEvento(catedral, "Concierto de Órgano Barroco", TipoEvento.CONCIERTO, LocalDateTime.of(2026, 11, 27, 19, 30));
        crearEvento(sacramentinos, "Visita Nocturna Guiada", TipoEvento.TOUR_CULTURAL, LocalDateTime.of(2026, 11, 28, 20, 30));
        crearEvento(museo, "Exposición: Platería del Virreinato", TipoEvento.EXPOSICION, LocalDateTime.of(2026, 11, 5, 10, 0));
        crearEvento(recoleta, "Apertura especial del claustro", TipoEvento.AVISO, LocalDateTime.of(2026, 11, 12, 15, 0));
        crearEvento(merced, "Ciclo de música sacra", TipoEvento.CONCIERTO, LocalDateTime.of(2026, 12, 3, 19, 0));

        log.info("[DataLoader] Catálogo sintético cargado: {} recintos, {} eventos", recintoRepository.count(), eventoRepository.count());
    }

    private Recinto crear(String nombre, String tipo, String comuna, String direccion, double lat, double lng,
                          String estilo, String siglo, EstadoApertura estado, Long idEncargado,
                          Map<String, Comuna> comunas, Map<String, TipoRecinto> tipos, String resena) {
        Recinto recinto = new Recinto();
        recinto.setNombreOficial(nombre);
        recinto.setTipo(tipos.get(tipo));
        recinto.setComuna(comunas.get(comuna));
        recinto.setDireccion(direccion);
        recinto.setLatitud(lat);
        recinto.setLongitud(lng);
        recinto.setCategoria("Monumento Nacional");
        recinto.setEstilo(estilo);
        recinto.setSiglo(siglo);
        recinto.setEstadoActual(estado);
        recinto.setIdEncargado(idEncargado);
        recinto.setResenaHistorica(resena);
        return recintoRepository.save(recinto);
    }

    private void crearHorarios(Recinto recinto) {
        for (int dia = 1; dia <= 7; dia++) {
            Horario horario = new Horario();
            horario.setRecinto(recinto);
            horario.setDiaSemana(dia);
            horario.setHoraApertura(dia <= 5 ? LocalTime.of(9, 0) : LocalTime.of(10, 0));
            horario.setHoraCierre(dia <= 5 ? LocalTime.of(18, 0) : LocalTime.of(13, 0));
            horarioRepository.save(horario);
        }
    }

    private void crearEvento(Recinto recinto, String titulo, TipoEvento tipo, LocalDateTime inicio) {
        Evento evento = new Evento();
        evento.setRecinto(recinto);
        evento.setTitulo(titulo);
        evento.setDescripcion("Actividad de prueba del catálogo sintético.");
        evento.setTipo(tipo);
        evento.setFechaInicio(inicio);
        evento.setFechaFin(inicio.plusHours(2));
        evento.setLatitud(recinto.getLatitud());
        evento.setLongitud(recinto.getLongitud());
        eventoRepository.save(evento);
    }
}
