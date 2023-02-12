package de.esg.xls2sap.service;

import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.tinylog.Logger;

import com.google.common.base.Preconditions;

import de.esg.xls2sap.gui.ApplicationRegistry;
import de.esg.xls2sap.gui.LoggerExposer;
import de.esg.xls2sap.gui.ApplicationProperties.PropertyName;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Basisklasse für alle konkreten Services für den Zugriff vom Client per REST auf den Server.
 *
 * @author Stefan Prechtl
 *
 */
@SuppressWarnings({ "PMD.GuardLogStatement", "PMD.TooManyMethods", "PMD.ExcessiveImports" })
public abstract class AbstractService<T>
{

	protected static final long MIN_ID = 0;

	protected String resourcePath;
	protected HttpClient client;
	protected Jsonb jsonb;
	protected Class<T> type;

	protected Validator validator;

	// Timeout-Dauer in Sekunden
	private static int TIMEOUT_DURATION = 100;

	@Inject
	protected ApplicationRegistry registry;

	@Inject
	protected LoggerExposer logger;

	protected AbstractService(final String resourcePath, final Class<T> type)
	{
		final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		this.validator = factory.getValidator();

		this.resourcePath = resourcePath;
		this.type = type;
		this.client = HttpClient.newBuilder() //
				.version(HttpClient.Version.HTTP_2)//
				.connectTimeout(Duration.ofSeconds(TIMEOUT_DURATION)).build();
	}

	protected String encodeValue(final String value)
	{
		var result = value;

		try
		{
			result = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
		}
		catch (final UnsupportedEncodingException e)
		{
			this.logger.fatalErrorConsumer().accept(e);
		}
		return result;
	}

	@SuppressWarnings("PMD.EmptyControlStatement")
	public ServiceResult<List<T>> loadAll()
	{
		ServiceResult<List<T>> result = null;
		try
		{
			final HttpResponse<String> response = this.doGET("");

			List<T> content = new ArrayList<>();
			if (HTTP_OK == response.statusCode())
			{
				final String responseBody = response.body();
				content = this.jsonb.fromJson(responseBody, this.getTypeForArray());
			}

			return new ServiceResult<>(response.statusCode(), content, "", "");
		}
		catch (final ServiceWebException ex)
		{
			final var emptyList = new ArrayList<T>();
			result = new ServiceResult<>(ex.getStatusCode(), emptyList, ex.getClientMessage(), ex.getUrsprungsmessage());
		}
		return result;
	}

	@SuppressWarnings("PMD.EmptyControlStatement")
	public ServiceResult<List<T>> loadByQueryParams(final String pathExt, final String queryParamsPayload)
	{
		ServiceResult<List<T>> result = null;
		try
		{
			final HttpResponse<String> response = this.doPOSTString(pathExt, queryParamsPayload);

			List<T> content = new ArrayList<>();
			if (HTTP_OK == response.statusCode())
			{
				final String responseBody = response.body();
				content = this.jsonb.fromJson(responseBody, this.getTypeForArray());
			}

			return new ServiceResult<>(response.statusCode(), content, "", "");
		}
		catch (final ServiceWebException ex)
		{
			final var emptyList = new ArrayList<T>();
			result = new ServiceResult<>(ex.getStatusCode(), emptyList, ex.getClientMessage(), ex.getUrsprungsmessage());
		}
		return result;
	}

	@SuppressWarnings("PMD.EmptyControlStatement")
	public ServiceResult<Optional<T>> loadById(final long id)
	{
		ServiceResult<Optional<T>> result = null;
		try
		{
			final var pathExtension = "/" + String.valueOf(id);
			final HttpResponse<String> response = this.doGET(pathExtension);

			Optional<T> content = Optional.empty();
			if (HTTP_OK == response.statusCode())
			{
				final String responseBody = response.body();
				content = Optional.of(this.jsonb.fromJson(responseBody, this.type));
			}
			return new ServiceResult<>(response.statusCode(), content, "", "");
		}
		catch (final ServiceWebException ex)
		{
			result = new ServiceResult<>(ex.getStatusCode(), Optional.empty(), ex.getClientMessage(), ex.getUrsprungsmessage());
		}

		return result;
	}

	@SuppressWarnings("PMD.EmptyControlStatement")
	public ServiceResult<List<T>> loadByWildcard(final String attribute, final String value)
	{
		ServiceResult<List<T>> result = null;
		try
		{
			var pathExtension = "/wildcard?attribute=#attr&value=#val";
			pathExtension = pathExtension.replace("#attr", attribute);
			pathExtension = pathExtension.replace("#val", value);

			final HttpResponse<String> response = this.doGET(pathExtension);

			List<T> content = new ArrayList<>();
			if (HTTP_OK == response.statusCode())
			{
				final String responseBody = response.body();
				content = this.jsonb.fromJson(responseBody, this.getTypeForArray());
			}
			return new ServiceResult<>(response.statusCode(), content, "", "");

		}
		catch (final ServiceWebException ex)
		{
			final var emptyList = new ArrayList<T>();
			result = new ServiceResult<>(ex.getStatusCode(), emptyList, ex.getClientMessage(), ex.getUrsprungsmessage());
		}
		return result;
	}

	@SuppressWarnings("PMD.EmptyControlStatement")
	public ServiceResult<Long> post(final T obj)
	{
		return this.post("", obj);
	}

	@SuppressWarnings("PMD.EmptyControlStatement")
	public ServiceResult<Long> post(final String path, final T obj)
	{
		if (obj != null)
		{
			this.validate(obj);
		}

		ServiceResult<Long> result = null;
		try
		{
			final var jsonString = this.jsonb.toJson(obj);
			final var response = this.doPOSTString(path, jsonString);
			this.registry.putCurrentHttpCode(response.statusCode());

			// Ergebnis prüfen und Ergebnis ID aus Header extrahieren
			var content = -1L;
			if (HTTP_NO_CONTENT == response.statusCode())
			{
				final var link = response.headers().firstValue("link");
				Preconditions.checkState(link.isPresent(), "Header ohne Link");

				final var linkPart = link.get().split(";");
				final var uri = linkPart[0];
				final var uri2 = uri.substring(1, uri.length() - 1);
				final var idAsString = uri2.substring(uri2.lastIndexOf("/") + 1);
				content = Long.valueOf(idAsString);
			}

			result = new ServiceResult<>(response.statusCode(), content, "", "");
		}
		catch (final ServiceWebException ex)
		{
			result = new ServiceResult<>(ex.getStatusCode(), -1L, ex.getClientMessage(), ex.getUrsprungsmessage());
		}
		return result;
	}

	@SuppressWarnings("PMD.EmptyControlStatement")
	public ServiceResult<Boolean> put(final String pathExtension, final T obj)
	{
		this.validate(obj);

		ServiceResult<Boolean> result = null;
		try
		{
			final var jsonString = this.jsonb.toJson(obj);
			final var response = this.doPUT(pathExtension, jsonString);
			this.registry.putCurrentHttpCode(response.statusCode());
			// Ergebnis prüfen
			final var content = (HTTP_NO_CONTENT == response.statusCode());
			result = new ServiceResult<>(response.statusCode(), content, "", "");
		}
		catch (final ServiceWebException ex)
		{
			result = new ServiceResult<>(ex.getStatusCode(), Boolean.FALSE, ex.getClientMessage(), ex.getUrsprungsmessage());
		}
		return result;
	}

	@SuppressWarnings("PMD.EmptyControlStatement")
	public ServiceResult<Boolean> putNoObj(final String pathExtension)
	{
		ServiceResult<Boolean> result = null;
		try
		{
			final var jsonString = "";
			final var response = this.doPUT(pathExtension, jsonString);
			this.registry.putCurrentHttpCode(response.statusCode());
			// Ergebnis prüfen
			final var content = (HTTP_NO_CONTENT == response.statusCode());
			result = new ServiceResult<>(response.statusCode(), content, "", "");
		}
		catch (final ServiceWebException ex)
		{
			result = new ServiceResult<>(ex.getStatusCode(), Boolean.FALSE, ex.getClientMessage(), ex.getUrsprungsmessage());
		}
		return result;
	}

	@SuppressWarnings("PMD.EmptyControlStatement")
	public ServiceResult<Boolean> delete(final String pathExtension)
	{
		ServiceResult<Boolean> result = null;
		try
		{
			final var response = this.doDELETE(pathExtension);
			this.registry.putCurrentHttpCode(response.statusCode());

			// Ergebnis prüfen
			final var content = (HTTP_NO_CONTENT == response.statusCode());
			result = new ServiceResult<>(response.statusCode(), content, "", "");

		}
		catch (final ServiceWebException ex)
		{
			result = new ServiceResult<>(ex.getStatusCode(), Boolean.FALSE, ex.getClientMessage(), ex.getUrsprungsmessage());
		}
		return result;
	}

	// ****************************************************************************************************************
	//
	// Ab hier protected Methoden
	//
	// ****************************************************************************************************************

	private void validate(final T obj)
	{
		final Set<ConstraintViolation<T>> violations = this.validator.validate(obj);
		if (!violations.isEmpty())
		{
			throw new ValidationException("Validierungsfehler: " + violations.toString());
		}
	}

	protected HttpResponse<String> doGET(final String pathExtension)
	{
		HttpResponse<String> response = null;
		try
		{
			final var request = HttpRequest.newBuilder()//
					.uri(URI.create(this.restEndpoint() + this.resourcePath + pathExtension))//
					.header("Content-Type", "application/json")//
					.GET() //
					.build();
			response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
			Logger.info("REST-Call: GET " + request.uri().toString());
			this.registry.putCurrentHttpCode(response.statusCode());

			return response;
		}
		catch (final Exception e)
		{
			if (null == response)
			{
				this.logger.fatalErrorConsumer().accept(e);
				throw new ServiceWebException("Server nicht verfügbar", e.getMessage());
			}
			throw new ServiceWebException(response);
		}
	}

	protected void doGETAsync(final String pathExtension, final Consumer<HttpResponse<String>> consumer)
	{
		try
		{
			final var request = HttpRequest.newBuilder()//
					.uri(URI.create(this.restEndpoint() + this.resourcePath + pathExtension))//
					.header("Content-Type", "application/json") //
					.GET() //
					.build();
			final CompletableFuture<HttpResponse<String>> response = this.client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
			response.orTimeout(1, TimeUnit.MINUTES)//
					// .thenApply(HttpResponse<String>::body)//
					.thenAccept(consumer) //
					.exceptionally(ex -> {
						response.cancel(true);
						this.logger.fatalErrorConsumer().accept(ex);
						return null;
					});
		}
		catch (final Exception e)
		{
			this.logger.fatalErrorConsumer().accept(e);
		}
	}

	protected void doPUTAsync(final String pathExtension, final String payload, final Consumer<HttpResponse<String>> consumer)
	{
		try
		{
			final var request = HttpRequest.newBuilder() //
					.uri(URI.create(this.restEndpoint() + this.resourcePath + pathExtension))//
					.header("Content-Type", "application/json") //
					.PUT(BodyPublishers.ofString(payload))//
					.build();
			final var response = this.client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
			response.orTimeout(1, TimeUnit.MINUTES)//
					// .thenApply(HttpResponse<String>::body)//
					.thenAccept(consumer) //
					.exceptionally(ex -> {
						response.cancel(true);
						this.logger.fatalErrorConsumer().accept(ex);
						return null;
					});
		}
		catch (final Exception e)
		{
			throw new ServiceWebException(e.getMessage());
		}
	}

	protected HttpResponse<String> doPUT(final String pathExtension, final String payload)
	{
		HttpResponse<String> response = null;
		try
		{
			final var request = HttpRequest.newBuilder() //
					.uri(URI.create(this.restEndpoint() + this.resourcePath + pathExtension))//
					.header("Content-Type", "application/json") //
					.PUT(BodyPublishers.ofString(payload))//
					.build();
			response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
			Logger.info("REST-Call: PUT " + request.uri().toString());
			this.registry.putCurrentHttpCode(response.statusCode());

			return response;
		}
		catch (final Exception e)
		{
			if (null == response)
			{
				this.logger.fatalErrorConsumer().accept(e);
				throw new ServiceWebException("Server nicht verfügbar", e.getMessage());
			}
			throw new ServiceWebException(response);
		}
	}

	protected HttpResponse<String> doPOSTString(final String pathExtension, final String payload)
	{
		HttpResponse<String> response = null;
		try
		{
			final var request = HttpRequest.newBuilder() //
					.uri(URI.create(this.restEndpoint() + this.resourcePath + pathExtension))//
					.header("Content-Type", "application/json") //
					.POST(BodyPublishers.ofString(payload))//
					.build();
			response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
			Logger.info("REST-Call: POST " + request.uri().toString());
			this.registry.putCurrentHttpCode(response.statusCode());

			return response;
		}
		catch (final Exception e)
		{
			if (null == response)
			{
				this.logger.fatalErrorConsumer().accept(e);
				// throw new ServiceWebException("Server nicht verfügbar", e.getMessage());
				throw new ServiceWebException(e);
			}
			throw new ServiceWebException(response);
		}
	}

	protected HttpResponse<String> doPOSTStream(final String pathExtension, final InputStream is)
	{
		HttpResponse<String> response = null;
		try
		{
			final var request = HttpRequest.newBuilder() //
					.uri(URI.create(this.restEndpoint() + this.resourcePath + pathExtension)) //
					.header("Content-Type", "application/json") //
					.POST(BodyPublishers.ofInputStream(() -> is)) //
					.build();

			response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
			Logger.info("REST-Call: POST " + request.uri().toString());
			this.registry.putCurrentHttpCode(response.statusCode());

			return response;
		}
		catch (final Exception e)
		{
			if (null == response)
			{
				this.logger.fatalErrorConsumer().accept(e);
				throw new ServiceWebException("Server nicht verfügbar", e.getMessage());
			}
			throw new ServiceWebException(response);
		}
	}

	protected HttpResponse<String> doDELETE(final String pathExtension)
	{
		HttpResponse<String> response = null;
		try
		{
			final var request = HttpRequest.newBuilder()//
					.uri(URI.create(this.restEndpoint() + this.resourcePath + pathExtension))//
					.header("Content-Type", "application/json")//
					.DELETE() //
					.build();
			response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
			Logger.info("REST-Call: DELETE " + request.uri().toString());
			this.registry.putCurrentHttpCode(response.statusCode());

			return response;
		}
		catch (final Exception e)
		{
			if (null == response)
			{
				this.logger.fatalErrorConsumer().accept(e);
				throw new ServiceWebException("Server nicht verfügbar", e.getMessage());
			}
			throw new ServiceWebException(response);
		}
	}

	protected void doDELETEAsync(final String pathExtension, final Consumer<HttpResponse<String>> consumer)
	{
		try
		{
			final var request = HttpRequest.newBuilder()//
					.uri(URI.create(this.restEndpoint() + this.resourcePath + pathExtension))//
					.header("Content-Type", "application/json")//
					.DELETE() //
					.build();
			final var response = this.client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
			response.orTimeout(1, TimeUnit.MINUTES)//
					.thenAccept(consumer) //
					.exceptionally(ex -> {
						response.cancel(true);
						this.logger.fatalErrorConsumer().accept(ex);
						return null;
					});
		}
		catch (final Exception e)
		{
			throw new ServiceWebException(e.getMessage());
		}
	}

	// Konfiguration des Server endpoints
	protected String restEndpoint()
	{
		return this.registry.getApplicationProperties().getStringProperty(PropertyName.REST_ENDPOINT);
	}

	// Für Unit-Tests
	@SuppressWarnings("ucd")
	public void init(final LoggerExposer logger, final ApplicationRegistry registry)
	{
		this.logger = logger;
		this.registry = registry;
	}

	// Für die Konvertierung von Java-Arrays zu Json
	abstract protected Type getTypeForArray();

}
