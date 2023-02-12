package de.esg.xls2sap.model.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.esg.xls2sap.model.validators.ValidMaterialartValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidMaterialartValidator.class)
public @interface ValidMaterialart
{
	String message() default "Ungültige Materialart";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}