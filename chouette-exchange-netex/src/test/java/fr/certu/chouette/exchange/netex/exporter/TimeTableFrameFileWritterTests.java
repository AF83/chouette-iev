package fr.certu.chouette.exchange.netex.exporter;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import lombok.extern.log4j.Log4j;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import fr.certu.chouette.exchange.netex.ComplexModelFactory;
import fr.certu.chouette.exchange.netex.ModelTranslator;
import fr.certu.chouette.exchange.netex.NetexNamespaceContext;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
@Log4j
public class TimeTableFrameFileWritterTests extends AbstractTestNGSpringContextTests {

    protected ModelTranslator modelTranslator = new ModelTranslator();
    private NetexFileWriter netexFileWriter;
    private ComplexModelFactory complexModelFactory;
    private String fileName = "/tmp/test.xml";
    private XPath xPath = XPathFactory.newInstance().newXPath();
    private Document xmlDocument;
    private Line line;

    @BeforeClass
    protected void setUp() throws Exception {
    	try
    	{
        xPath.setNamespaceContext(new NetexNamespaceContext());
        netexFileWriter = (NetexFileWriter) applicationContext.getBean("netexFileWriter");

        complexModelFactory = new ComplexModelFactory();
        complexModelFactory.init();
        
        
        line = complexModelFactory.nominalLine( "1");
        

        netexFileWriter.writeXmlFile(line, fileName);

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        xmlDocument = builder.parse(fileName);
    	}
    	catch (Exception ex)
    	{
    		log.error("setup fails",ex);
    		throw ex;
    	}
    }
    
    @Test(groups = {"TimeTableFrame"}, description = "Check if 2 vehicle journeys exists")
    public void verifyVehicleJourneys() throws XPathExpressionException {
        Assert.assertEquals( xPath.evaluate("count(//netex:ServiceJourney)", xmlDocument), "28");               
        
        List<VehicleJourney> vehicles = line.getRoutes().get(0).
                                getJourneyPatterns().get(0).getVehicleJourneys();
        VehicleJourney vehicle = vehicles.get( 0);
        Assert.assertNotNull( xPath.evaluate("//netex:ServiceJourney[@id='" + modelTranslator.netexId( vehicle) + "']", xmlDocument, XPathConstants.NODE) );
    }
    
    @Test(groups = {"TimeTableFrame"}, description = "Check if dayType exists")
    public void verifyDayTypes() throws XPathExpressionException {
        Assert.assertEquals( xPath.evaluate("count(//netex:ServiceJourney//netex:DayTypeRef)", xmlDocument), "56"); 
        
        List<VehicleJourney> vehicles = line.getRoutes().get(0).
                                getJourneyPatterns().get(0).getVehicleJourneys();
        for (int i = 0; i < vehicles.get(0).getTimetables().size(); i++) {
            Timetable timetable = vehicles.get(0).getTimetables().get(i);                   
            Assert.assertNotNull( xPath.evaluate("//netex:ServiceJourney//netex:DayTypeRef[@ref='" + modelTranslator.netexId( timetable) + "']", xmlDocument, XPathConstants.NODE) );
        }            
    }
    

    
}
