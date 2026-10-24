package com.redpatrimonial.ms_rutas;

import com.redpatrimonial.ms_rutas.enums.EstadoSolicitud;
import com.redpatrimonial.ms_rutas.enums.TipoRuta;
import com.redpatrimonial.ms_rutas.services.CoordinacionGuiasService;
import com.redpatrimonial.ms_rutas.services.RutaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MsRutasApplicationTests {

	@Autowired
	private RutaService rutaService;

	@Autowired
	private CoordinacionGuiasService coordinacionService;

	@Test
	void cargaLasRutasOficialesYLaAsignacionDePrueba() {
		assertEquals(3, rutaService.listar(TipoRuta.OFICIAL, null, null).size());
		assertEquals(EstadoSolicitud.ASIGNADA, coordinacionService.listarSolicitudes(null, 4L, null).get(0).getEstado());
		assertEquals(1, coordinacionService.listarAsignaciones(4L).size());
	}

}
