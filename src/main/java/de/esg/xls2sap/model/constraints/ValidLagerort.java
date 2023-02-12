package de.esg.xls2sap.model.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.esg.xls2sap.model.validators.ValidLagerortValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidLagerortValidator.class)
public @interface ValidLagerort
{
	String message() default "Ungültiger Lagerort";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}