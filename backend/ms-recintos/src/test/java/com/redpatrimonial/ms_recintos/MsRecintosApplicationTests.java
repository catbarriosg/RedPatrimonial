package com.redpatrimonial.ms_recintos;

import com.redpatrimonial.ms_recintos.repositories.RecintoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MsRecintosApplicationTests {

	@Autowired
	private RecintoRepository recintoRepository;

	@Test
	void contextLoadsYCargaElCatalogo() {
		assertEquals(10, recintoRepository.count());
	}

}
