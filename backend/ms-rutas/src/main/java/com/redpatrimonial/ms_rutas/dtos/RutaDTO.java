package com.redpatrimonial.ms_rutas.dtos;

import com.redpatrimonial.ms_rutas.enums.TipoRuta;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

// Ruta oficial (CU-26) o personalizada (CU-09). Las paradas van en el orden del recorrido.
@Data
public class RutaDTO {

    private Long idUsuario;

    @NotBlank(message = "El nombre de la ruta es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El tipo de ruta es obligatorio (OFICIAL o PERSONALIZADA)")
    private TipoRuta tipo;

    @PositiveOrZero(message = "La distancia no puede ser negativa")
    private Double distanciaKm;

    @NotNull(message = "La ruta debe tener paradas")
    @Size(min = 2, message = "La ruta debe tener al menos 2 paradas")
    @Valid
    private List<ParadaDTO> paradas;
}
