package mobi.chouette.exchange.validator.parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class RouteParameters {

	@XmlElement(name = "objectid")
	private FieldParameters objectid;

	@XmlElement(name = "name")
	private FieldParameters name;

	@XmlElement(name = "number")
	private FieldParameters number;

	@XmlElement(name = "published_name")
	private FieldParameters publishedName;

}