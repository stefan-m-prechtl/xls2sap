package de.esg.xls2sap.model.validators;

import java.util.Arrays;
import java.util.List;

import de.esg.xls2sap.model.constraints.ValidLagerort;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidLagerortValidator implements ConstraintValidator<ValidLagerort, String>
{
	private List<String> validValues;

	public ValidLagerortValidator()
	{
		this.validValues = Arrays.asList("3000");
	}

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context)
	{
		final var result = this.validValues.contains(value);
		return result;
	}

}
