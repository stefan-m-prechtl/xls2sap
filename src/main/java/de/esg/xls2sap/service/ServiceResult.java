package de.esg.xls2sap.service;

/**
 * Klassifizierungsklasse für das Ergebnis eines REST-Service Aufrufs: HttpCode und ggf. die Nutzdaten
 * 
 * @author Stefan Prechtl
 *
 * @param <R> Typ der Nutzdaten im Service-Result
 */
public record ServiceResult<R> (Integer httpCode, R data, String userMsg, String detailMsg)
{
}
