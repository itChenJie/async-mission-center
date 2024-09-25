package com.mission.center.annotation;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.ModelSpecification;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class EnumModelPropertyBuilderPlugin implements ModelPropertyBuilderPlugin {

    @Override
    public void apply(ModelPropertyContext context) {
        if (context.getBeanPropertyDefinition().isPresent()) {
            Field field = context.getBeanPropertyDefinition().get().getField().getAnnotated();
            if (field.isAnnotationPresent(ApiEnumProperty.class)) {
                ApiEnumProperty annotation = field.getAnnotation(ApiEnumProperty.class);
                Class<? extends Enum<?>> enumClass = annotation.value();
                String description = Arrays.stream(enumClass.getEnumConstants())
                        .map(enumConstant -> enumConstant.toString())
                        .collect(Collectors.joining(", "));
                context.getSpecificationBuilder()
                        .description(description)
                        .example(enumClass.getEnumConstants()[0].name());
            }
        }
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return DocumentationType.SWAGGER_2.equals(delimiter);
    }
}
