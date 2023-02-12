package de.esg.xls2sap.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import de.esg.xls2sap.controller.upload.DynamicColumnHeaderEnum;

@Tag("unit-test")
@DisplayName("Pure Unittest 'DynamicColumnHeaderEnum'")
class DynamicColumnHeaderEnumTest
{

	@Test
	void itShouldReturn30IPPN()
	{
		final String[] enumValues = DynamicColumnHeaderEnum.getEnumValues(DynamicColumnHeaderEnum.IPPN);
		assertThat(enumValues.length).isEqualTo(30);
	}

	@Test
	void itShouldReturnAnError()
	{
		assertThrows(NullPointerException.class, () -> DynamicColumnHeaderEnum.getEnumValues(null));
	}
}
