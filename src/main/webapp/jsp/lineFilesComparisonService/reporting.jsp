<%@ taglib prefix="s" uri="/struts-tags" %>
<%-- Titre et barre de navigation --%>	
<title><s:property value="title"/></title>

<div class="panelData"><s:property value="title"/></div>

<%-- Import de différents fichiers --%>

<div style="width: 500px;" >
	<h2><s:text name="comparator.view.subtitle.availableActions"></s:text></h2>
		<s:form id="comparisonResultActionsForm" enctype="multipart/form-data" method="POST">
			<span>
				<s:submit 
					theme="simple" action="downloadReport" 
					name="downloadReport" value="download Report">					
				</s:submit>
				<s:submit 
					theme="simple" action="lineFilesComparisonService" 
					name="lineFilesComparisonService" value="new comparison"></s:submit>
			</span>
		</s:form>
</div>

<s:if test="#request.comparisonReport != null">
	<div style="width: 500px;">
		<h2><s:text name="comparator.view.subtitle.report"></s:text></h2>
			<s:iterator id="reportItem" value="#request.comparisonReport">
				<fieldset >
					<legend> <b><s:property value="name" /> : <i><s:property value="state" /></i></b> </legend>
						<p>Source Object Id : <s:property value="sourceId" /></p>
						<p>Target Object Id : <s:property value="targetId" /></p>								
						<s:if test="attributesStates.size > 0">
							<p>Attributes states :</p>
								<s:iterator id="dataObjectState" value="attributesStates">
									<p style="margin-left: 0.5em">
									<s:property value="name" /> : <s:property value="identical" /></p>
								</s:iterator>
						</s:if>
				</fieldset>
			</s:iterator>
		</div>
	</s:if>