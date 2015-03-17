/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.gtfs.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsExtendedStopProducer;
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsTransferProducer;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporter;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 *
 */
@Log4j

@Stateless(name = GtfsStopAreaProducerCommand.COMMAND)

public class GtfsStopAreaProducerCommand implements Command, Constant 
{
	public static final String COMMAND = "GtfsStopAreaProducerCommand";

	@EJB
	private StopAreaDAO stopAreaDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {


			GtfsExportParameters parameters = (GtfsExportParameters) context
					.get(CONFIGURATION);

			List<Object> ids = null;
			if (parameters.getIds() != null) {
				ids  = new ArrayList<Object>(parameters.getIds());
			}
			Set<StopArea> stopAreas = new HashSet<>();
			if (ids == null || ids.isEmpty()) {
				stopAreas.addAll(stopAreaDAO.findAll());
			}
			else
			{
				stopAreas.addAll(stopAreaDAO.findAll(ids));
			}
			saveStopAreas(context, stopAreas);

			result = SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}




	public void saveStopAreas(Context context,Collection<StopArea> beans)
	{
		// Metadata metadata = (Metadata) context.get(METADATA);
		GtfsExporter exporter = (GtfsExporter) context.get(GTFS_EXPORTER);
		Set<StopArea> physicalStops = new HashSet<StopArea>();
		Set<StopArea> commercialStops = new HashSet<StopArea>();
		Set<ConnectionLink> connectionLinks = new HashSet<ConnectionLink>();
		GtfsExtendedStopProducer stopProducer = new GtfsExtendedStopProducer(exporter);
		GtfsTransferProducer transferProducer = new GtfsTransferProducer(exporter);
		ActionReport report = (ActionReport) context.get(REPORT);
		GtfsExportParameters configuration = (GtfsExportParameters) context
				.get(CONFIGURATION);
		String prefix = configuration.getObjectIdPrefix();
		String sharedPrefix = prefix;
		// metadata.setDescription("limited to stops and transfers");
		for (StopArea area : beans)
		{
			if (area.getAreaType().equals(ChouetteAreaEnum.BoardingPosition) || area.getAreaType().equals(ChouetteAreaEnum.Quay))
			{
				if (area.hasCoordinates())
				{
					physicalStops.add(area);
					if (area.getConnectionStartLinks() != null)
						connectionLinks.addAll(area.getConnectionStartLinks());
					if (area.getConnectionEndLinks() != null)
						connectionLinks.addAll(area.getConnectionEndLinks());

					if (area.getParent() != null && area.getParent().hasCoordinates())
					{
						commercialStops.add(area.getParent());
						if (area.getParent().getConnectionStartLinks() != null)
							connectionLinks.addAll(area.getParent().getConnectionStartLinks());
						if (area.getParent().getConnectionEndLinks() != null)
							connectionLinks.addAll(area.getParent().getConnectionEndLinks());
					}
				}
			}

		}
		for (Iterator<StopArea> iterator = commercialStops.iterator(); iterator.hasNext();)
		{
			StopArea stop = iterator.next();
			if (!stopProducer.save(stop, report, sharedPrefix, null))
			{
				iterator.remove();
			}
			else
			{
				//				if (stop.hasCoordinates())
				//					metadata.getSpatialCoverage().update(stop.getLongitude().doubleValue(), stop.getLatitude().doubleValue());
			}
		}
		for (StopArea stop : physicalStops)
		{
			stopProducer.save(stop, report, sharedPrefix, commercialStops);
			//			if (stop.hasCoordinates())
			//				metadata.getSpatialCoverage().update(stop.getLongitude().doubleValue(), stop.getLatitude().doubleValue());
		}
		// remove incomplete connectionlinks
		for (ConnectionLink link : connectionLinks)
		{
			if (!physicalStops.contains(link.getStartOfLink()) && !commercialStops.contains(link.getStartOfLink()))
			{
				continue;
			}
			else if (!physicalStops.contains(link.getEndOfLink()) && !commercialStops.contains(link.getEndOfLink()))
			{
				continue;
			}
			transferProducer.save(link, report, sharedPrefix);
		}

	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.gtfs/"
						+ COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(GtfsStopAreaProducerCommand.class.getName(),
				new DefaultCommandFactory());
	}


}
