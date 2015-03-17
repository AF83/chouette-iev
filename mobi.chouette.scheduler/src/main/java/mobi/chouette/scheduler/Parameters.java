package mobi.chouette.scheduler;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import mobi.chouette.exchange.validator.parameters.ValidationParameters;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={})
@Data
public class Parameters {

	
	@XmlElements(value = {
			@XmlElement(name = "neptune-import", type = mobi.chouette.exchange.neptune.importer.NeptuneImportParameters.class),
			@XmlElement(name = "neptune-export", type = mobi.chouette.exchange.neptune.exporter.NeptuneExportParameters.class),
			@XmlElement(name = "gtfs-import", type = mobi.chouette.exchange.gtfs.importer.GtfsImportParameters.class),
			@XmlElement(name = "gtfs-export", type = mobi.chouette.exchange.gtfs.exporter.GtfsExportParameters.class),
			@XmlElement(name = "netex-import", type = mobi.chouette.exchange.netex.importer.NetexImportParameters.class),
			@XmlElement(name = "netex-export", type = mobi.chouette.exchange.netex.exporter.NetexExportParameters.class),
        	@XmlElement(name = "validate", type = mobi.chouette.exchange.validator.ValidateParameters.class) })
	private Object configuration;

	@XmlElement(name = "validation")
	private ValidationParameters validation;

}
