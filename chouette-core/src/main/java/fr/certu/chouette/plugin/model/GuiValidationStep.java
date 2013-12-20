/**
 * 
 */
package fr.certu.chouette.plugin.model;

import java.util.Calendar;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

/**
 * @author michel
 *
 */
public class GuiValidationStep extends ActiveRecordObject 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5725000704712085992L;
    @Getter @Setter private String ruleCode;
    @Getter @Setter private String severity;
	@Getter @Setter private String status;
	@Getter @Setter private Long violationCount;
    @Getter @Setter private JSONObject detail;
    @Getter @Setter private GuiValidation validation;

    
	public GuiValidationStep()
	{
		setCreatedAt(Calendar.getInstance().getTime());
		setUpdatedAt(getCreatedAt());
	}

}