package com.clinicat.citas.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import clinicat.commons.dto.PacienteResponseDTO;
import clinicat.commons.dto.RazaDTO;
import clinicat.commons.dto.UsuarioResponseDTO;
import clinicat.commons.entity.PacienteEntity;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Configurar el converter para mapear PacienteEntity a PacienteResponseDTO
        Converter<PacienteEntity, PacienteResponseDTO> pacienteConverter = context -> {
            PacienteEntity source = context.getSource();
            PacienteResponseDTO destination = new PacienteResponseDTO();

            destination.setId(source.getId());
            destination.setNombre(source.getNombre());
            destination.setSexo(source.getSexo());
            destination.setDescripcion(source.getDescripcion());

            // Mapear el propietario (usuario)
            if (source.getUsuario() != null) {
                UsuarioResponseDTO propietario = new UsuarioResponseDTO();
                propietario.setId(source.getUsuario().getId());
                propietario.setNombre(source.getUsuario().getNombre());
                propietario.setApellido(source.getUsuario().getApellido());
                destination.setPropietario(propietario);
            }

            // Mapear la raza
            if (source.getRaza() != null) {
                RazaDTO raza = new RazaDTO();
                raza.setId(source.getRaza().getId());
                raza.setNombreRaza(source.getRaza().getNombreRaza());
                destination.setRaza(raza);
            }

            return destination;
        };

        modelMapper.createTypeMap(PacienteEntity.class, PacienteResponseDTO.class)
                .setConverter(pacienteConverter);

        return modelMapper;
    }
}