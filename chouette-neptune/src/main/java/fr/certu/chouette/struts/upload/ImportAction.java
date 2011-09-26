package fr.certu.chouette.struts.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.struts.GeneriqueAction;

@SuppressWarnings("serial")
public class ImportAction extends GeneriqueAction {

   private static final Logger logger = Logger.getLogger(ImportAction.class);
   private static final String SUCCESS_ITINERAIRE = "success_itineraire";
   private static final String INPUT_ITINERAIRE = "input_itineraire";
   @Getter @Setter private String fichierContentType;
   @Getter @Setter private File fichier;
   @Getter @Setter private boolean incremental;
   @Getter @Setter private String fichierFileName; 

   @Setter INeptuneManager<Line> lineManager;
   @Setter INeptuneManager<Route> routeManager;

   //	private IImportateur importateur = null;
   //	private ILecteurCSV lecteurCSV;
   //	private ILecteurPrincipal lecteurCSVPrincipal;
   //	private IIdentificationManager identificationManager;
   //	private IImportHorairesManager importHorairesManager;
   @Getter @Setter private String useCSVGeneric;
   //	private IReducteur reducteur;
   @Getter @Setter private String tmprep;

   @Getter @Setter private Long idLigne;
   private InputStream inputStream;
   private User user = null;

   public ImportAction() {
      super();
   }


   public void setInputStream(InputStream inputStream) {
      this.inputStream = inputStream;
   }

   public InputStream getInputStream() throws Exception {
      return inputStream;
      //return new FileInputStream(logFile.getPath());
   }

   @Override
   public String execute() throws Exception {
      return SUCCESS;
   }


   public String importCSVGeneric() 
   {
      String canonicalPath = null;
      try {
         canonicalPath = fichier.getCanonicalPath();
         logger.debug("Importing Generic CSV File \"" + canonicalPath + "\"");
      } catch (Exception e) {
         addActionError(getExceptionMessage(e));
         return INPUT;
      }
      try {
         List<ParameterValue> parameters = new ArrayList<ParameterValue>();
         SimpleParameterValue inputFile = new SimpleParameterValue("inputFile");
         inputFile.setFilepathValue(canonicalPath);
         parameters.add(inputFile);
         SimpleParameterValue simpleParameterValue3 = new SimpleParameterValue("fileFormat");
         simpleParameterValue3.setStringValue(FilenameUtils.getExtension(fichierFileName));
         parameters.add(simpleParameterValue3);
         ReportHolder reportHolder = new ReportHolder();
         List<Line> lines = lineManager.doImport(user, "CSV", parameters, reportHolder);
         if (reportHolder.getReport() != null && !reportHolder.getReport().getStatus().equals(Report.STATE.OK))
         {
            logReport(reportHolder.getReport(),Level.ERROR);
            addActionError(reportHolder.getReport());
         }
         else
         {
            if (reportHolder.getReport() != null)
            {
               logReport(reportHolder.getReport(),Level.INFO);
            }
            lineManager.saveAll(user, lines, true, true);
         }
      } 
      catch (ChouetteException e) 
      {
         addActionError(e.getLocalizedMessage());
         return INPUT;
      }

      addActionMessage(getText("message.import.generical.csv.success"));
      return SUCCESS;
   }

   private void addActionError(Report report)
   {
      addActionError(report.getLocalizedMessage());
      addActionError("   ",report.getItems());

   }


   private void addActionError(String indent,List<ReportItem> items)
   {
      if (items == null) return;
      for (ReportItem item : items) 
      {
         if (!item.getStatus().equals(Report.STATE.OK))
         {
            addActionError(indent+item.getStatus().name()+" : "+item.getLocalizedMessage());
            addActionError(indent+"   ",item.getItems());
         }
      }

   }


   public String importCSV() {
      String canonicalPath = null;
      try {
         canonicalPath = fichier.getCanonicalPath();
         logger.debug("Importing CSV File \"" + canonicalPath + "\"");
      } catch (Exception e) {
         addActionError(getExceptionMessage(e));
         return INPUT;
      }
      try {
         List<ParameterValue> parameters = new ArrayList<ParameterValue>();
         SimpleParameterValue inputFile = new SimpleParameterValue("inputFile");
         inputFile.setFilepathValue(canonicalPath);
         parameters.add(inputFile);
         SimpleParameterValue simpleParameterValue3 = new SimpleParameterValue("fileFormat");
         simpleParameterValue3.setStringValue(FilenameUtils.getExtension(fichierFileName));
         parameters.add(simpleParameterValue3);
         ReportHolder reportHolder = new ReportHolder();
         List<Line> lines = lineManager.doImport(user, "CSV", parameters, reportHolder);
         if (reportHolder.getReport() != null && !reportHolder.getReport().getStatus().equals(Report.STATE.OK))
         {
            logReport(reportHolder.getReport(),Level.ERROR);
            addActionError(reportHolder.getReport());
         }
         else
         {
            if (reportHolder.getReport() != null)
            {
               logReport(reportHolder.getReport(),Level.INFO);
            }
            lineManager.saveAll(user, lines, true, true);
         }
      } 
      catch (ChouetteException e) 
      {
         addActionError(e.getLocalizedMessage());
         return INPUT;
      }

      addActionMessage(getText("message.import.csv.success"));
      return SUCCESS;
   }

   /**
    * Neptune multiple import : zip format but each entry is imported and saved before next one
    * <br/> to use for large zip files 
    * 
    * @return SUCCESS or INPUT
    * @throws Exception
    */
   public String importXMLs() throws Exception 
   {
      // migrated in new architecture
      File temp = null;
      try {
         String result = SUCCESS;
         ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(fichier));
         ZipEntry zipEntry = zipInputStream.getNextEntry();
         if (zipEntry == null) {
            addActionError(getText("zipfile.empty"));
            return INPUT;
         }
         while (zipEntry != null) {
            byte[] bytes = new byte[4096];
            int len = zipInputStream.read(bytes);
            temp = new File(tmprep, zipEntry.getName());
            FileOutputStream fos = new FileOutputStream(temp);
            while (len > 0) {
               fos.write(bytes, 0, len);
               len = zipInputStream.read(bytes);
            }
            if (!result.equals(importXML(temp,zipEntry.getName()))) {
               result = INPUT;
            }
            zipEntry = zipInputStream.getNextEntry();
            temp.delete();
         }
         return result;
      } catch (Exception e) {
         if (temp != null)
            addActionError(getExceptionMessage(e) + " : " + temp.getAbsolutePath());
         else
            addActionError(getExceptionMessage(e));
         return INPUT;
      }
   }

   /**
    * Neptune simple import : xml or zip format but all entries are imported before save step
    * <br/> to use for small zip files only 
    * 
    * @return SUCCESS or INPUT
    * @throws Exception
    */
   public String importXML() throws Exception {
      try {
         return importXML(fichier,fichierFileName);
      } catch (Exception e) {
         addActionError(getExceptionMessage(e));
         return INPUT;
      }
   }


   /**
    * import and save a file (xml or zip format)
    * 
    * @param file file to import
    * @param fileName file name for extension identification
    * @return SUCCESS or INPUT
    * @throws Exception
    */
   private String importXML(File file,String fileName) throws Exception 
   {
      try
      {
         String canonicalPath = file.getCanonicalPath();

         if(!FilenameUtils.getExtension(fileName).toLowerCase().equals("xml") && 
               !FilenameUtils.getExtension(fileName).toLowerCase().equals("zip"))
         {
            addActionError(getText("message.import.xml.failure"));
            return INPUT;
         }
         List<ParameterValue> parameters = new ArrayList<ParameterValue>();
         SimpleParameterValue simpleParameterValue = new SimpleParameterValue("inputFile");
         simpleParameterValue.setFilepathValue(canonicalPath);
         parameters.add(simpleParameterValue);

         SimpleParameterValue simpleParameterValue2 = new SimpleParameterValue("validate");
         simpleParameterValue2.setBooleanValue(true);
         parameters.add(simpleParameterValue2);	

         SimpleParameterValue simpleParameterValue3 = new SimpleParameterValue("fileFormat");
         simpleParameterValue3.setStringValue(FilenameUtils.getExtension(fileName));
         parameters.add(simpleParameterValue3);
         ReportHolder reportHolder = new ReportHolder();

         List<Line> lines = lineManager.doImport(null,"NEPTUNE",parameters, reportHolder);

         if(lines != null && !lines.isEmpty())
         {
            if (reportHolder.getReport() != null)
            {
               logReport(reportHolder.getReport(),Level.INFO);
            }
            for (Line line : lines) 
            {
               List<Line> bid = new ArrayList<Line>();
               bid.add(line);
               lineManager.saveAll(null, bid, true, true);
               String[] args = new String[1];
               args[0] = line.getName();
               addActionMessage(getText("message.import.xml.success", args));
            }
            return SUCCESS;
         }	
         addActionError(getText("message.import.xml.failure"));
         if (reportHolder.getReport() != null)
         {
            logReport(reportHolder.getReport(),Level.ERROR);
         }
         return INPUT;
      }
      catch (ChouetteException ex)
      {
         addActionError(ex.getLocalizedMessage());
         return INPUT;
      }
   }

   /**
    * @param report
    * @param level
    */
   private void logReport(Report report, Level level)
   {
      logger.log(level,report.getLocalizedMessage());
      logItems("",report.getItems(),level);

   }


   /**
    * log report details from import plugins
    * 
    * @param indent text indentation for sub levels
    * @param items report items to log
    * @param level log level 
    */
   private void logItems(String indent, List<ReportItem> items, Level level) 
   {
      if (items == null) return;
      for (ReportItem item : items) 
      {
         logger.log(level,indent+item.getStatus().name()+" : "+item.getLocalizedMessage());
         logItems(indent+"   ",item.getItems(),level);
      }

   }

   /**
    * @return
    */
   public String importHorairesItineraire() 
   {
      String canonicalPath = null;
      try {
         canonicalPath = fichier.getCanonicalPath();
         logger.debug("Importing CSV Route File \"" + canonicalPath + "\"");
      } catch (Exception e) {
         addActionError(getExceptionMessage(e));
         return INPUT_ITINERAIRE;
      }
      try {
         List<ParameterValue> parameters = new ArrayList<ParameterValue>();
         SimpleParameterValue inputFile = new SimpleParameterValue("inputFile");
         inputFile.setFilepathValue(canonicalPath);
         ReportHolder reportHolder = new ReportHolder();
         List<Route> routes = routeManager.doImport(user, "CSV", parameters, reportHolder);
         if (!reportHolder.getReport().getStatus().equals(Report.STATE.OK))
         {
            logReport(reportHolder.getReport(),Level.ERROR);
            addActionError(reportHolder.getReport());
         }
         else
         {
            if (reportHolder.getReport() != null)
            {
               logReport(reportHolder.getReport(),Level.INFO);
            }
            // TODO : see how to merge with existing route ! 
            routeManager.saveAll(user, routes, true, true);
         }
      } 
      catch (ChouetteException e) 
      {
         addActionError(e.getLocalizedMessage());
         return INPUT_ITINERAIRE;
      }

      addActionMessage(getText("message.import.vehicleJourneyAtStop.success"));
      return SUCCESS_ITINERAIRE;
      
   }


   @Override
   public String input() throws Exception {
      return INPUT;
   }


}