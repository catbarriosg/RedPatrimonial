package com.redpatrimonial.ms_usuarios;

import com.redpatrimonial.ms_usuarios.dtos.LoginDTO;
import com.redpatrimonial.ms_usuarios.dtos.SesionDTO;
import com.redpatrimonial.ms_usuarios.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MsUsuariosApplicationTests {

	@Autowired
	private UsuarioService usuarioService;

	@Test
	void elUsuarioDePruebaPuedeIniciarSesion() {
		LoginDTO dto = new LoginDTO();
		dto.setEmail("encargado@demo.cl");
		dto.setPassword("1234");

		SesionDTO sesion = usuarioService.login(dto);

		assertEquals(2L, sesion.getId());
		assertEquals("ENCARGADO", sesion.getRol());
	}

}
