package com.ricardofernandezv.blog.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCommentRequest {

    @NotBlank(message = "El contenido del comentario no puede estar vacío.")
    @Size(min = 2, max = 1000, message = "El comentario debe tener entre 2 y 1000 caracteres.")
    private String content;

}
