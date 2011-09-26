/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.exchange.csv.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.csv.exception.ExchangeException;
import fr.certu.chouette.exchange.csv.exception.ExchangeExceptionCode;
import fr.certu.chouette.exchange.csv.importer.producer.CompanyProducer;
import fr.certu.chouette.exchange.csv.importer.producer.LineProducer;
import fr.certu.chouette.exchange.csv.importer.producer.PTNetworkProducer;
import fr.certu.chouette.exchange.csv.importer.producer.TimetableProducer;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.ReportHolder;

public class CSVImportLinePlugin implements IImportPlugin<Line>
{

   private static final Logger logger            = Logger.getLogger(CSVImportLinePlugin.class);

   private FormatDescription   description;

   private List<String>        allowedExtensions = Arrays.asList(new String[] { "csv" });


   @Getter
   @Setter
   private TimetableProducer   timetableProducer;
   @Getter
   @Setter
   private PTNetworkProducer   ptNetworkProducer;
   @Getter
   @Setter
   private CompanyProducer     companyProducer;
   @Getter
   @Setter
   private LineProducer        lineProducer;
   @Getter
   @Setter
   private String defaultObjectIdPrefix;

   /**
    * 
    */
   public CSVImportLinePlugin()
   {
      description = new FormatDescription(this.getClass().getName());
      description.setName("CSV");
      List<ParameterDescription> params = new ArrayList<ParameterDescription>();
      ParameterDescription param1 = new ParameterDescription("inputFile", ParameterDescription.TYPE.FILEPATH, false,
            true);
      param1.setAllowedExtensions(Arrays.asList(new String[] { "csv" }));
      params.add(param1);
      ParameterDescription param2 = new ParameterDescription("fileFormat", ParameterDescription.TYPE.STRING, false,
      "file extension");
      param2.setAllowedExtensions(Arrays.asList(new String[] { "csv" }));
      params.add(param2);
      ParameterDescription param3 = new ParameterDescription("objectIdPrefix", ParameterDescription.TYPE.STRING, false, defaultObjectIdPrefix);
      params.add(param3);
      description.setParameterDescriptions(params);
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.plugin.exchange.IExchangePlugin#getDescription()
    */
   @Override
   public FormatDescription getDescription()
   {
      return description;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.plugin.exchange.IImportPlugin#doImport(java.util.List,
    * fr.certu.chouette.plugin.report.ReportHolder)
    */
   @Override
   public List<Line> doImport(List<ParameterValue> parameters, ReportHolder reportContainer) throws ChouetteException
   {
      String filePath = null;
      String extension = "file extension";
      String objectIdPrefix = defaultObjectIdPrefix;
      for (ParameterValue value : parameters)
      {
         if (value instanceof SimpleParameterValue)
         {
            SimpleParameterValue svalue = (SimpleParameterValue) value;
            if (svalue.getName().equalsIgnoreCase("inputFile"))
            {
               filePath = svalue.getFilepathValue();
            }
            else if (svalue.getName().equals("fileFormat"))
            {
               extension = svalue.getStringValue().toLowerCase();
            }
            else if (svalue.getName().equalsIgnoreCase("objectIdPrefix"))
            {
               objectIdPrefix = svalue.getStringValue();
            }
            else
            {
               throw new IllegalArgumentException("unexpected argument " + svalue.getName());
            }
         }
         else
         {
            throw new IllegalArgumentException("unexpected argument " + value.getName());
         }
      }
      if (filePath == null)
      {
         logger.error("missing argument inputFile");
         throw new IllegalArgumentException("inputFile required");
      }

      if (extension.equals("file extension"))
      {
         extension = FilenameUtils.getExtension(filePath).toLowerCase();
      }
      if (!allowedExtensions.contains(extension))
      {
         logger.error("invalid argument inputFile " + filePath + ", allowed format : "
               + Arrays.toString(allowedExtensions.toArray()));
         throw new IllegalArgumentException("invalid file type : " + extension);
      }

      List<Line> lines = null;

      // simple file processing
      logger.info("start import simple file " + filePath);
      lines = processImport(filePath,objectIdPrefix);
      logger.info("import terminated");
      return lines;
   }

   /**
    * @param objectIdPrefix 
    * @param rootObject
    * @param validate
    * @param report
    * @param entryName
    * @return
    * @throws ExchangeException
    */
   private List<Line> processImport(String filePath, String objectIdPrefix) throws ExchangeException
   {
      ChouetteCsvReader csvReader = null;
      Map<String, Timetable> timetableMap = new HashMap<String, Timetable>();
      PTNetwork ptNetwork = null;
      Company company = null;
      List<Line> lines = new ArrayList<Line>();

      try
      {
         File input = new File(filePath);
         FileInputStream stream = new FileInputStream(input);
         csvReader = new ChouetteCsvReader(new InputStreamReader(stream, "UTF-8"), ';');
      }
      catch (FileNotFoundException e)
      {
         throw new ExchangeException(ExchangeExceptionCode.FILE_NOT_FOUND, filePath);
      }
      catch (UnsupportedEncodingException e)
      {
         throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, filePath);
      }

      String[] currentLine;
      try
      {
         currentLine = csvReader.readNext();
      }
      catch (IOException e)
      {
         throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, filePath);
      }

      while (currentLine[TimetableProducer.TITLE_COLUMN].equals(TimetableProducer.TIMETABLE_LABEL_TITLE))
      {
         logger.debug("timetables");
         Timetable timetable = timetableProducer.produce(csvReader, currentLine,objectIdPrefix);
         timetableMap.put(timetable.getObjectId().split(":")[2], timetable);
         try
         {
            currentLine = csvReader.readNext(); // empty line
            currentLine = csvReader.readNext();
         }
         catch (IOException e)
         {
            throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, filePath);
         }
      }

      logger.debug("network");
      ptNetwork = ptNetworkProducer.produce(csvReader, currentLine,objectIdPrefix);
      try
      {
         currentLine = csvReader.readNext(); // empty line
         currentLine = csvReader.readNext();
      }
      catch (IOException e)
      {
         throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, filePath);
      }

      logger.debug("company");
      company = companyProducer.produce(csvReader, currentLine,objectIdPrefix);
      try
      {
         currentLine = csvReader.readNext(); // empty line
         currentLine = csvReader.readNext();
      }
      catch (IOException e)
      {
         throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, filePath);
      }

      while (currentLine[LineProducer.TITLE_COLUMN].equals(LineProducer.LINE_NAME_TITLE))
      {
         logger.debug("lines");
         Line line = lineProducer.produce(csvReader, currentLine,objectIdPrefix);
         line.setCompany(company);
         line.setPtNetwork(ptNetwork);
         assemble(line,timetableMap);
         if (line.getRoutes().isEmpty())
         {
            logger.error("empty line removed :"+line.getNumber());
         }
         else
         {
            lines.add(line);
         }
         try
         {
            currentLine = csvReader.readNext();
            if (currentLine == null) break;
         }
         catch (IOException e)
         {
            throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, filePath);
         }
      }

      return lines;
   }
   /**
    * @param line
    * @param timetableMap
    * @throws ExchangeException
    */
   private void assemble(Line line, Map<String, Timetable> timetableMap) throws ExchangeException
   {
      for (Iterator<Route> iterator = line.getRoutes().iterator(); iterator.hasNext();)
      {
         Route route =  iterator.next();
         for (Iterator<JourneyPattern> iterator2 = route.getJourneyPatterns().iterator(); iterator2.hasNext();)
         {
            JourneyPattern journey = iterator2.next();
            for (Iterator<VehicleJourney> iterator3 = journey.getVehicleJourneys().iterator(); iterator3.hasNext();)
            {
               VehicleJourney vj = iterator3.next();
               String key = vj.getComment();
               vj.setComment(null);
               Timetable t = timetableMap.get(key);
               if (t == null)
               {
                  logger.error("missing timetable "+key+" vehicleJourney removed :"+vj.getObjectId());
                  iterator3.remove();
               }
               else
               {
                  vj.addTimetable(t);
                  t.addVehicleJourney(vj);
               }
            }
            if (journey.getVehicleJourneys().isEmpty())
            {
               logger.error("empty journeyPattern removed :"+journey.getObjectId());
               iterator2.remove();
            }
         }
         if (route.getJourneyPatterns().isEmpty())
         {
            logger.error("empty route removed :"+route.getObjectId());
            iterator.remove();
         }
      }

   }

}
