package br.com.tamanhofamilia.idadmin.controllers.dto.converters;

import br.com.tamanhofamilia.idadmin.controllers.dto.creategenerator.GeneratorResponseDto;
import br.com.tamanhofamilia.idadmin.models.entities.Generator;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class GeneratorResponseDtoConverter implements Converter<Generator, GeneratorResponseDto> {
    @Override
    public GeneratorResponseDto convert(Generator source) {
        GeneratorResponseDto responseDto = null;
        if (source != null){
            responseDto = new GeneratorResponseDto();
            BeanUtils.copyProperties(source, responseDto);
        }
        return responseDto;
    }
}
