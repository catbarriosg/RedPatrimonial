package com.redpatrimonial.ms_recintos.dtos;

import com.redpatrimonial.ms_recintos.enums.EstadoApertura;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CambioEstadoDTO {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoApertura estado;

    // Encargado que hace el cambio, para el historial
    private Long idUsuario;
}
