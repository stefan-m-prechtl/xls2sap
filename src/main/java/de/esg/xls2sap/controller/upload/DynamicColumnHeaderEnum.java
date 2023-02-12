package de.esg.xls2sap.controller.upload;

public enum DynamicColumnHeaderEnum
{
	IPPN("IPPN", 30), CSN("CSN", 30), ISN("ISN", 30), NILAmEBO("NILamEBO", 30), DFL("DFL", 30), QNAAmEBO("QNAamEBO", 30), MOVAmEBO("MOVamEBO", 30);

	private String value;
	private int count;

	DynamicColumnHeaderEnum(final String value, final int count)
	{
		this.value = value;
		this.count = count;
	}

	public static String[] getEnumValues(final DynamicColumnHeaderEnum dynamicColumnHeaderEnum)
	{
		final String[] result = new String[dynamicColumnHeaderEnum.getCount()];
		for (int i = 0; i < dynamicColumnHeaderEnum.getCount(); i++)
		{
			result[i] = dynamicColumnHeaderEnum.getValue() + String.valueOf(i + 1);
		}
		return result;
	}

	public String getValue()
	{
		return this.value;
	}

	public int getCount()
	{
		return this.count;
	}

}
