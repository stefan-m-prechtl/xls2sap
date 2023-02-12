package de.esg.xls2sap.service;

import java.net.http.HttpResponse;
import java.util.Optional;

public class ServiceWebException extends RuntimeException
{
	private static final long serialVersionUID = 8273970399584007146L;
	private int statusCode;
	private String clientMessage;
	private String ursprungsmessage;

	public ServiceWebException()
	{
		this("keine Exception-Message vorhanden");
	}

	ServiceWebException(final String message)
	{
		super(message);
		this.clientMessage = "Server nicht verfügbar";
		this.statusCode = 503;
	}

	public ServiceWebException(final int statusCode, final String message, final String ursprungsmessage)
	{
		super(message);
		this.clientMessage = "Server nicht verfügbar";
		this.ursprungsmessage = ursprungsmessage;
		this.statusCode = statusCode;
	}

	public ServiceWebException(final String message, final String ursprungsmessage)
	{
		super(message);
		this.clientMessage = "Server nicht verfügbar";
		this.ursprungsmessage = ursprungsmessage;
		this.statusCode = 503;
	}

	public ServiceWebException(final HttpResponse<?> response)
	{
		super();
		if (null == response)
		{
			this.clientMessage = "Server nicht verfügbar";
			this.statusCode = 503;
		}
		else
		{
			this.statusCode = response.statusCode();
			final Optional<String> error = response.headers().firstValue("reason");
			if (error.isPresent())
			{
				this.clientMessage = error.get();
			}
			else
			{
				this.clientMessage = "Keine Fehlerbeschreibung vom Server verfügbar";
			}
		}
	}

	ServiceWebException(final Exception e)
	{
		super();

		this.ursprungsmessage = e.getMessage();
		if (null != e.getCause())
		{
			final var cause = e.getCause();
			final var msgCause = cause.getMessage();
			this.ursprungsmessage = (msgCause == null) ? "keine Fehlermeldung vom System vorhanden" : msgCause;
		}
		this.clientMessage = "Fehler am Server";
		this.statusCode = 500;
	}

	public int getStatusCode()
	{
		return this.statusCode;
	}

	public String getClientMessage()
	{
		return this.clientMessage;
	}

	public String getUrsprungsmessage()
	{
		return this.ursprungsmessage;
	}

}
