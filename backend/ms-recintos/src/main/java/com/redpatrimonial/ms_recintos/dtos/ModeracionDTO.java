package com.redpatrimonial.ms_recintos.dtos;

import com.redpatrimonial.ms_recintos.enums.AccionModeracion;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ModeracionDTO {

    @NotNull(message = "La acción es obligatoria (APROBAR u OCULTAR)")
    private AccionModeracion accion;

    @NotNull(message = "El administrador es obligatorio")
    private Long idAdmin;

    private String motivo;
}
