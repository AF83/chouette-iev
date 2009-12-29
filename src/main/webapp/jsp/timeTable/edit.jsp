<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<s:include value="/jsp/commun/jscalendar.jsp"></s:include>

<%-- Titre et barre de navigation --%>
<s:url id="urlTableauMarcheUpdate" action="edit" namespace="/tableauMarche">
  <s:param name="idTableauMarche" value="%{id}"/>
</s:url>
<s:if test="id != null">
  <title><s:text name="text.tableauMarche.update.title"/></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.tableauMarche.update.title'), '', #urlTableauMarcheUpdate)"/>
</s:if>
<s:else>
  <title><s:text name="text.tableauMarche.create.title"/></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.tableauMarche.create.title'), '', #urlTableauMarcheUpdate)"/>
</s:else>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<br>
<%-- Formulaire --%>
<div class="panelDataSection"><s:text name="tableauMarche"/></div>
<div class="panel">
  <s:form cssClass="panelDataInnerForm" method="POST">
    <s:hidden name="idTableauMarche" value="%{id}"/>
    <s:hidden name="operationMode" value="STORE" />
    <s:hidden key="actionMethod" value="%{actionMethod}"/>
    <s:textfield key="tableauMarche.objectId" name="objectId" readonly="true" cssClass="texteNonEditable" cssStyle="width: 250px;"/>
    <s:textfield key="tableauMarche.comment" name="comment" required="true" cssStyle="width: 250px;"/>

    <s:if test="id != null">
      <div class="editTableauMarche" >
        <s:checkboxlist name="joursTypes" list="@fr.certu.chouette.struts.enumeration.EnumerationApplication@getJoursTypesEnum()" listKey="enumeratedTypeAccess" listValue="textePropriete" template="checkboxlist.ftl"></s:checkboxlist>
      </div>
    </s:if>

    <%-- Actions --%>
    <tr>
      <td colspan="2">
        <s:if test="id != null">
          <s:submit key="action.update" action="%{actionMethod}" theme="simple" cssStyle="float: right;"/>
        </s:if>
        <s:else>
          <s:submit key="action.create" action="%{actionMethod}" theme="simple" cssStyle="float: right;"/>
        </s:else>
        <s:submit key="action.cancel" action="cancel" theme="simple" cssStyle="float: right;"/>
      </td>
    </tr>

    <%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
    <tr><td colspan="2"><s:include value="/jsp/commun/asterisque.jsp" /></td></tr>
  </s:form>
</div>

<s:if test="id != null">
  <div class="panelDataSection"><s:text name="tableauMarche.dates"/></div>
  <div class="panel">
    <s:div label="Dates" id="displaytag">
      <display:table uid="datesTable" name="dates" excludedParams="debut fin date tableauMarche.comment jour" sort="list" pagesize="10" defaultorder="ascending" defaultsort="1"  export="false">
        <display:column title="Date" sortable="true" headerClass="sortable" comparator="fr.certu.chouette.struts.util.DateComparator">
          <s:property value="%{dates[#attr.datesTable_rowNum - 1]}" />
        </display:column>
        <display:column title="Action">
          <s:url id="deleteUrl" action="deleteDate" namespace="/tableauMarche">
            <s:param name="idTableauMarche" value="%{id}"/>
            <s:param name="idxDate" value="#attr.datesTable_rowNum"/>
          </s:url>
          <s:a href="%{deleteUrl}" onclick="return confirm('%{getText('popup.confirmer')}'">
            <img border="0" alt="Delete" src="<s:url value='/images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>">
          </s:a>
        </display:column>
      </display:table>
    </s:div>
    <s:form cssClass="panelDataInnerForm" action="addDate" namespace="/tableauMarche">
      <s:if test="id != null">
        <s:hidden name="idTableauMarche" value="%{id}"/>
      </s:if>
      <s:textfield label="Date" name="jour" id="jour" />
      <s:submit key="text.tableauMarche.addDate.button"/>
      <br>

      <script type="text/javascript">
        <!--//
        Calendar.setup(
        {
          singleClick : true,
          firstDay : 1,
          inputField : "jour",    	// ID of the input field
          ifFormat : "%d/%m/%Y"  			// the date format
        }
      );
        //-->
      </script>
    </s:form>
  </div>
</s:if>


<s:if test="id != null">
  <div class="panelDataSection"><s:text name="tableauMarche.periods"/></div>
  <div class="panel">
    <!-- Affichage liste des périodes -->
    <div id="displaytag">
      <display:table uid="periodsTable" name="periodes" excludedParams="debut fin date tableauMarche.comment jour" sort="list" pagesize="10" defaultorder="ascending" defaultsort="2" export="false">
        <display:column title="Debut" sortable="true" headerClass="sortable">
          <s:property value="%{periodes[#attr.periodsTable_rowNum - 1].debut}" />
        </display:column>
        <display:column title="Fin" sortable="true" headerClass="sortable">
          <s:property value="%{periodes[#attr.periodsTable_rowNum - 1].fin}" />
        </display:column>
        <display:column title="Action">
          <s:url id="deleteUrl" action="deletePeriod" namespace="/tableauMarche">
            <s:param name="idTableauMarche" value="%{id}"/>
            <s:param name="idxPeriod" value="#attr.periodsTable_rowNum"/>
          </s:url>
          <s:a href="%{deleteUrl}" onclick="return confirm('%{getText('popup.confirmer')}'">
            <img border="0" alt="Delete" src="<s:url value='/images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>">
          </s:a>
        </display:column>
      </display:table>
    </div>
    <!-- Affichage formulaire création nouvelle période -->
    <s:form cssClass="panelDataInnerForm" action="addPeriode" namespace="/tableauMarche">
      <s:if test="id != null">
        <s:hidden name="idTableauMarche" value="%{id}"/>
      </s:if>
      <s:textfield key="tableauMarche.period.startDate" name="debut" id="debut"/>
      <s:textfield key="tableauMarche.period.endDate" name="fin" id="fin"/>
      <s:submit key="text.tableauMarche.addPeriod.button"/>
      <br>
      <script type="text/javascript">
        <!--//
        Calendar.setup(
        {
          singleClick : true,
          firstDay : 1,
          inputField : "debut",    	// ID of the input field
          ifFormat : "%d/%m/%Y"  			// the date format
        }
      );

        Calendar.setup(
        {
          singleClick : true,
          firstDay : 1,
          inputField : "fin",    	// ID of the input field
          ifFormat : "%d/%m/%Y"  			// the date format
        }
      );
        //-->
      </script>
    </s:form>
  </div>
</s:if>
