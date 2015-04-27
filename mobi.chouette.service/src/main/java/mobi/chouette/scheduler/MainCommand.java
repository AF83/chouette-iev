package mobi.chouette.scheduler;

import java.io.IOException;
import java.nio.file.Paths;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.service.JobService;
import mobi.chouette.service.JobServiceManager;

@Log4j
@Stateless(name = MainCommand.COMMAND)
public class MainCommand implements Command, Constant {

	public static final String COMMAND = "MainCommand";

	@EJB
	JobServiceManager jobManager;

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public boolean execute(Context context) throws Exception {
		boolean result = false;

		Long id = (Long) context.get(JOB_ID);
		JobService jobService = jobManager.getJobService(id);
		try {
			if (jobService.getAction().equals("exporter"))
			{
				jobService.setFilename("export_" + jobService.getType() + "_" + jobService.getId() + ".zip");
			}
			// set job status to started
			jobManager.start(jobService);

			context.put(JOB_DATA, jobService);
//			context.put(ARCHIVE, jobService.getFilename());
//			context.put(JOB_REFERENTIAL, jobService.getReferential());
//			context.put(ACTION, jobService.getAction());
//			context.put(TYPE, jobService.getType());
			
			Parameters parameters = JSONUtil.fromJSON(Paths.get(jobService.getPath(), PARAMETERS_FILE), Parameters.class);
			// context.put(PARAMETERS, parameters);
			context.put(CONFIGURATION, parameters.getConfiguration());
			context.put(VALIDATION, parameters.getValidation());
			context.put(REPORT, new ActionReport());
			context.put(MAIN_VALIDATION_REPORT, new ValidationReport());

			String name = jobService.getCommandName();

			InitialContext ctx = (InitialContext) context.get(INITIAL_CONTEXT);
			Command command = CommandFactory.create(ctx, name);
			command.execute(context);

			jobManager.terminate(jobService);

		} catch (Exception ex) {
			log.error(ex);
			jobManager.abort(jobService);

		}
		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.service/" + COMMAND;
				result = (Command) context.lookup(name);
			} catch (Exception e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(MainCommand.class.getName(), new DefaultCommandFactory());
	}
}