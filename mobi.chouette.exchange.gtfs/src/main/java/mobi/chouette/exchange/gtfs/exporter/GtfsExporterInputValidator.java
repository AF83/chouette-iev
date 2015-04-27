package mobi.chouette.exchange.gtfs.exporter;

import java.io.IOException;
import java.util.Arrays;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

@Log4j
public class GtfsExporterInputValidator implements InputValidator {

	private static String[] allowedTypes = { "all", "line", "network", "company", "groupofline", "stoparea" };

	@Override
	public boolean check(AbstractParameter abstractParameter, ValidationParameters validationParameters, String fileName) {

		if (!(abstractParameter instanceof GtfsExportParameters)) {
			log.error("invalid parameters for gtfs export " + abstractParameter.getClass().getName());
			return false;
		}

		GtfsExportParameters parameters = (GtfsExportParameters) abstractParameter;
		if (parameters.getStartDate() != null && parameters.getEndDate() != null) {
			if (parameters.getStartDate().after(parameters.getEndDate())) {
				log.error("end date before start date ");
				return false;
			}
		}

		String type = parameters.getReferencesType();
		if (type == null || type.isEmpty()) {
			log.error("missing type");
			return false;
		}
		if (!Arrays.asList(allowedTypes).contains(type.toLowerCase())) {
			log.error("invalid type " + type);
			return false;
		}

		String timezone = parameters.getTimeZone();
		if (timezone == null || timezone.isEmpty()) {
			log.error("missing timezone");
			return false;
		}

		String prefix = parameters.getObjectIdPrefix();
		if (prefix == null || prefix.isEmpty()) {
			log.error("missing object_id_prefix");
			return false;
		}

		if (fileName != null) {
			log.error("input data not expected");
			return false;
		}

		return true;
	}

	public static class DefaultFactory extends InputValidatorFactory {

		@Override
		protected InputValidator create() throws IOException {
			InputValidator result = new GtfsExporterInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(GtfsExporterInputValidator.class.getName(), new DefaultFactory());
	}

}