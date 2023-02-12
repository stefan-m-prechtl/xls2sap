package de.esg.xls2sap.model.validators;

import java.util.Arrays;
import java.util.List;

import de.esg.xls2sap.model.constraints.ValidMaterialart;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidMaterialartValidator implements ConstraintValidator<ValidMaterialart, String>
{
	private List<String> validValues;

	public ValidMaterialartValidator()
	{
		this.validValues = Arrays.asList("LF", "NLF");
	}

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context)
	{
		final var result = this.validValues.contains(value);
		return result;
	}
}
