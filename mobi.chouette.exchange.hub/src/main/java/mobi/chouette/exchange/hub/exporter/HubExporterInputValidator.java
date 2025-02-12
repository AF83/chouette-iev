package mobi.chouette.exchange.hub.exporter;

import java.io.IOException;
import java.util.Arrays;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.AbstractInputValidator;
import mobi.chouette.exchange.InputValidator;
import mobi.chouette.exchange.InputValidatorFactory;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

@Log4j
public class HubExporterInputValidator extends AbstractInputValidator {

	private static String[] allowedTypes = { "line", "network", "company", "group_of_line" };
	
	@Override
	public boolean checkParameters(AbstractParameter abstractParameter, ValidationParameters validationParameters) {
		if (!(abstractParameter instanceof HubExportParameters)) {
			log.error("invalid parameters for hub export " + abstractParameter.getClass().getName());
			return false;
		}

		HubExportParameters parameters = (HubExportParameters) abstractParameter;
		if (parameters.getStartDate() != null && parameters.getEndDate() != null) {
			if (parameters.getStartDate().after(parameters.getEndDate())) {
				log.error("end date before start date ");
				return false;
			}
		}

		String type = parameters.getReferencesType();
		if (type != null && !type.isEmpty()) {
			if (!Arrays.asList(allowedTypes).contains(type.toLowerCase())) {
				log.error("invalid type " + type);
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean checkFilename(String fileName) {
		if (fileName != null)
		{
			log.error("input data not expected");
			return false;
		}
		return true;
	}
	
	public static class DefaultFactory extends InputValidatorFactory {

		@Override
		protected InputValidator create() throws IOException {
			InputValidator result = new HubExporterInputValidator();
			return result;
		}
	}

	static {
		InputValidatorFactory.factories.put(HubExporterInputValidator.class.getName(),
				new DefaultFactory());
	}
	

}
