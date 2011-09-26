package fr.certu.chouette.jdbc.dao;
import java.util.ArrayList;
import java.util.List;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.ReportHolder;

@ContextConfiguration(locations={"classpath:testContext.xml","classpath*:chouetteContext.xml"})
@SuppressWarnings("unchecked")
public class JdbcDaoTests extends AbstractTestNGSpringContextTests
{
	private INeptuneManager<Line> lineManager;
	private INeptuneManager<Timetable> timetableManager;
	private INeptuneManager<VehicleJourney> vehicleJourneyManager;
	private INeptuneManager<JourneyPattern> journeyPatternManager;
	private INeptuneManager<Route> routeManager;
	private INeptuneManager<StopArea> stopareaManager;
	
	private String neptuneFile = null;
	private String neptuneZip = null;
	private String path="src/test/resources/";

	@Test(groups={"saveLine","saveLines","purge"}, description="Get a bean from context")
	public void getBean()
	{
		lineManager = (INeptuneManager<Line>) applicationContext.getBean("lineManager") ;
		timetableManager = (INeptuneManager<Timetable>) applicationContext.getBean("timetableManager") ;
		vehicleJourneyManager = (INeptuneManager<VehicleJourney>) applicationContext.getBean("vehicleJourneyManager") ;
		journeyPatternManager = (INeptuneManager<JourneyPattern>) applicationContext.getBean("journeyPatternManager") ;
		routeManager = (INeptuneManager<Route>) applicationContext.getBean("routeManager") ;
		stopareaManager = (INeptuneManager<StopArea>) applicationContext.getBean("stopAreaManager") ;
	}

	@Parameters({"neptuneFile"})
	@Test (groups = {"saveLine"}, description = "try saving single neptune file",dependsOnMethods={"getBean"})
	public void getNeptuneFile(String neptuneFile)
	{
		this.neptuneFile = neptuneFile;
	}

	@Parameters({"neptuneZip"})
	@Test (groups = {"saveLines"}, description = "try saving neptune zip file",dependsOnMethods={"getBean"})
	public void getNeptuneZip(String neptuneZip)
	{
		this.neptuneZip = neptuneZip;
	}

	
	@Test (groups = {"saveLine"}, description = "dao should save line",dependsOnMethods={"getBean"})
	public void verifyImportLine() throws ChouetteException
	{

		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
		simpleParameterValue.setFilepathValue(path+"/"+neptuneFile);
		parameters.add(simpleParameterValue);

		ReportHolder report = new ReportHolder();

		List<Line> lines = lineManager.doImport(null,"NEPTUNE",parameters, report);

		Assert.assertNotNull(lines,"lines can't be null");
		Assert.assertEquals(lines.size(), 1,"lines size must equals 1");
		lineManager.saveAll(null, lines, true, true);
		for(Line line : lines)
		{
			Assert.assertNotNull(line.getId(),"line's id can't be null");
			System.out.println(line.toString("\t",2));
		}
	}
	
	
	@Test (groups = {"saveLines"}, description = "dao should save lines",dependsOnMethods={"getBean"})
	public void verifyImportZipLines() throws ChouetteException
	{

		List<ParameterValue> parameters = new ArrayList<ParameterValue>();
		SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
		simpleParameterValue.setFilepathValue(path+"/"+neptuneZip);
		parameters.add(simpleParameterValue);

		ReportHolder report = new ReportHolder();

		List<Line> lines = lineManager.doImport(null,"NEPTUNE",parameters, report);

		Assert.assertNotNull(lines,"lines can't be null");
		Assert.assertEquals(lines.size(), 6,"lines size must equals 6");
		for(Line line : lines)
		{
			List<Line> bid = new ArrayList<Line>();
			bid.add(line);
			lineManager.saveAll(null, bid, true,true);
			Assert.assertNotNull(line.getId(),"line's id can't be null");
			System.out.println(line.toString("\t",0));
		}
		
	}
	
	@Test (groups = {"purge"}, description = "dao should purge",dependsOnMethods={"getBean"})
	public void verifyPurge() throws ChouetteException
	{
	    int count = timetableManager.purge(null);
	     count = vehicleJourneyManager.purge(null);
	     count = journeyPatternManager.purge(null);
	     count = routeManager.purge(null);
	     count = lineManager.purge(null);
	     count = stopareaManager.purge(null);
	     Assert.assertTrue(count>=0,"count must be greater than zero");
	}

}
