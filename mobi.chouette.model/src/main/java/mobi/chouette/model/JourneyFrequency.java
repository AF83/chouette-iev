package mobi.chouette.model;

import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Models the frequencies for journies in timesheet category.
 * 
 * @author zbouziane
 *
 */
@Entity
@Table(name = "journey_frequencies")
@NoArgsConstructor
@ToString(callSuper = true, exclude={"timeband"})
public class JourneyFrequency extends NeptuneObject {

	private static final long serialVersionUID = 8361606377991750952L;
	
	@Getter
	@Setter
	@GenericGenerator(name = "journey_frequencies_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "journey_frequencies_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@GeneratedValue(generator = "journey_frequencies_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;
	
	@Getter
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle_journey_id")
	private VehicleJourney vehicleJourney;
	
	/**
	 * set vehicle journey reference
	 * 
	 * @param vehicleJourney
	 *     The new vehicle journey of this journey frequency
	 */
	public void setVehicleJourney(VehicleJourney vehicleJourney) {
		this.vehicleJourney = vehicleJourney;
	}
	
	@Getter
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "timeband_id")
	private Timeband timeband;
	
	/**
	 * set time band reference
	 * 
	 * @param timeband
	 *     The new time band of this journey frequency
	 */
	public void setTimeband(Timeband timeband) {
		this.timeband = timeband;
	}
	
	/**
	 * The scheduled headway interval
	 * 
	 * @param scheduledHeadwayInterval
	 *            The new scheduled headway interval of this journey frequency
	 * @return The scheduled headway interval of this journey frequency
	 */
	@Getter
	@Setter
	@Column(name = "scheduled_headway_interval", nullable = false)
	private Time scheduledHeadwayInterval;

	/**
	 * The first departure time
	 * 
	 * @param firstDepartureTime
	 *            The new first departure time of this journey frequency
	 * @return The first departure time of this journey frequency
	 */
	@Getter
	@Setter
	@Column(name = "first_departure_time", nullable = false)
	private Time firstDepartureTime;

	/**
	 * The last departure time
	 * 
	 * @param lastDepartureTime
	 *            The new last departure time of this journey frequency
	 * @return The last departure time of this journey frequency
	 */
	@Getter
	@Setter
	@Column(name = "last_departure_time", nullable = false)
	private Time lastDepartureTime;

	/**
	 * Are the first and last departure times exact or not.
	 */
	@Getter
	@Setter
	@Column(name = "exact_time")
	private Boolean exactTime = false;
}