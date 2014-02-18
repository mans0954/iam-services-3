package org.openiam.idm.srvc.batch.birt;

import org.apache.log4j.Logger;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.*;
import org.eclipse.core.internal.registry.RegistryProviderFactory;
import org.openiam.idm.srvc.batch.constants.ReportFormat;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class ReportGenerator {

    private static Logger log = Logger.getLogger(ReportGenerator.class);

	/**
	 * This method takes care of the following 1. Deletes existing generated
	 * report directories 2. Generates reports for all active subscriptions 3.
	 * Emails reports as applicable - this task can be moved to groovy if
	 * needed.
	 */

	public void generateReport(String designFilePath, Map params, String outputFileName, ReportFormat format) throws BirtException { //String reportName, String reportDesign, String format, String userId, Map params, String deliveryMethod, List<String> emailAddresses, List<String> userIds) {
		EngineConfig config = new EngineConfig( );
		Platform.startup( config );  //If using RE API in Eclipse/RCP application this is not needed.
		IReportEngineFactory factory = (IReportEngineFactory) Platform
				.createFactoryObject( IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY );
		IReportEngine engine = factory.createReportEngine( config );
        //!!
		IReportRunnable design = engine.openReportDesign(designFilePath);
		IRunAndRenderTask task = engine.createRunAndRenderTask(design); 
		
		//Set parameter values and validate
		Set<String> paramKeySet = params.keySet();
		for (String key: paramKeySet){
			task.setParameterValue(key, params.get(key));
		}
		task.validateParameters();

		//!!String outputDir = reportRoot+ "/" + generatedReportsFolder + "/" + deliveryMethod + "/" ;
		//!!String outputFileName = outputDir + userId + "/" + reportName;
        IRenderOption renderOption = getRenderOption(outputFileName, format);
		task.setRenderOption(renderOption);

		//run and render report
		task.run();
		task.close();
		engine.destroy();
		Platform.shutdown();
		//Bugzilla 351052
		RegistryProviderFactory.releaseDefault();
    }

    private IRenderOption getRenderOption(String outputFileName, ReportFormat format) {
        IRenderOption renderOption;
        switch (format) {
            case HTML:
                final HTMLRenderOption htmlOption = new HTMLRenderOption();
                htmlOption.setEmbeddable(false);
                renderOption = htmlOption;
                break;
            case PDF:
                renderOption = new PDFRenderOption();
                break;
            default:
                return null;
        }
        renderOption.setOutputFileName(outputFileName);
        renderOption.setOutputFormat(format.toString());
        return renderOption;
    }
}
