<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<s:include value="/jsp/commun/mapStopPlaceJavascript.jsp" />
<%-- Titre et barre de navigation --%>
<s:url id="urlPositionGeographiqueUpdate" action="edit" namespace="/stopPlace">
  <s:param name="idPositionGeographique" value="%{id}"/>
</s:url>
<s:if test="id != null">
  <title><s:text name="text.zone.update.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.zone.update.title'), '', #urlPositionGeographiqueUpdate)"/>
</s:if> 
<s:else>
  <title><s:text name="text.zone.create.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.zone.create.title'), '', #urlPositionGeographiqueUpdate)"/>
</s:else>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>

<%-- Caractéristiques des zones --%>
<div class="panelDataSection"><s:text name="text.zone" /></div>
<div class="panel">
  <div class="left">
    <s:form  theme="css_xhtml" id="stoparea">
      <s:hidden name="idLigne" value="%{idLigne}"/>
      <s:hidden name="idItineraire" value="%{idItineraire}"/>
      <s:hidden name="actionSuivante" value="%{actionSuivante}"/>
      <s:hidden name="idPositionGeographique" value="%{id}" />
      <s:hidden name="operationMode" value="STORE" />
      <s:hidden key="actionMethod" value="%{actionMethod}"/>

      <s:textfield key="objectId" readonly="true" cssClass="texteNonEditable"/>
      <s:textfield key="name" required="true"/>
      <s:textfield key="comment"/>
      <s:textfield key="nearestTopicName" />
      <s:textfield key="streetName" />
      <s:textfield key="countryCode" />
      <s:textfield key="fareCode" />
      <s:textfield key="registrationNumber" />

      <s:if test="id != null">
        <s:select key="areaType" required="true" list="%{getStopAreaEnum('CommercialStopStopPlace')}" listKey="enumeratedTypeAccess" listValue="textePropriete" disabled="true"/>
      </s:if>
      <s:else>
        <s:select key="areaType" required="true" list="%{getStopAreaEnum('CommercialStopStopPlace')}" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
      </s:else>

      <fieldset>
        <legend><s:text name="text.positionGeographique.dataGeo.fieldset"/></legend>
        <s:text name="lambert2"/>
        <s:textfield key="x"  onblur="updateCoordsFrom('x')"/>
        <s:textfield key="y" onblur="updateCoordsFrom('y')"/>
        <s:text name="wsg84"/>
        <s:textfield key="latitude" onblur="updateCoordsFrom('lat')"/>
        <s:textfield key="longitude" onblur="updateCoordsFrom('lon')"/>
      </fieldset>

      <%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
      <s:include value="/jsp/commun/asterisque.jsp" />
      <%-- Actions --%>
      <div class="submit">
        <s:if test="id != null">
          <s:submit key="action.update" action="%{actionMethod}"  theme="simple" cssClass="right"/>
        </s:if>
        <s:else>
          <s:submit key="action.create" action="%{actionMethod}" theme="simple" cssClass="right"/>
        </s:else>
        <s:submit key="action.cancel" action="cancel" theme="simple" cssClass="right"/>
      </div>
    </s:form>
  </div>
  <div class="map-wrapper"><div id="map"></div></div>
  <div class="spacer"></div>
</div>

<s:if test="id != null">
  <%-- Zones filles --%>
  <div class="panelDataSection">
    <s:text name="text.positionGeographique.childArea.title" />
  </div>
  <div class="panel">
    <s:div label="Children" id="displaytag">
      <display:table name="children" id="child"  excludedParams="" sort="list" pagesize="10" export="false">
        <display:column titleKey="table.title.action">
          <s:url id="editUrl" action="edit" namespace="/stopPlace">
            <s:param name="idPositionGeographique">${child.id}</s:param>
          </s:url>
          <s:a href="%{editUrl}">
            <img border="0" alt="Edit" src="<s:url value='/images/editer.png'/>" title="<s:text name="tooltip.edit"/>">
          </s:a>&nbsp;&nbsp;
          <s:url id="removeUrl" action="removeChildFromParent" namespace="/stopPlace">
            <s:param name="idPositionGeographique" value="%{id}" />
            <s:param name="idChild">${child.id}</s:param>
          </s:url>
          <s:a href="%{removeUrl}">
            <img border="0" alt="Delete" src="<s:url value='/images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>">
          </s:a>
        </display:column>
        <display:column titleKey="table.title.name">
					Zone	<s:property value="%{#attr.child.name}"/>
        </display:column>
        <display:column titleKey="table.title.type">
          <s:text name="%{#attr.child.areaType}"/>
        </display:column>
      </display:table>
    </s:div>
    <%-- Formulaire de recherche de zone fille --%>
    <s:form id="areaSearchForm" action="search"  namespace="/stopPlace">
      <s:hidden name="idPositionGeographique" value="%{id}"/>
      <s:hidden name="actionSuivante" value="addChild"/>
      <s:submit key="action.add"/>
    </s:form>
  </div>


  <%-- Zones parentes --%>
  <div class="panelDataSection">
    <s:text name="text.positionGeographique.fatherArea.title" />
  </div>

  <div class="panel">
    <s:div label="father" id="displaytag">
      <display:table name="father"  excludedParams="" sort="list" pagesize="10" export="false">
        <display:column titleKey="table.title.action">
          <s:url id="editUrl" action="edit" namespace="/stopPlace">
            <s:param name="idPositionGeographique" value="%{father.id}" />
          </s:url>
          <s:a href="%{editUrl}">
            <img border="0" alt="Edit" src="<s:url value='/images/editer.png'/>" title="<s:text name="tooltip.edit"/>">
          </s:a>&nbsp;&nbsp;
          <s:url id="removeUrl" action="removeChildFromParent" namespace="/stopPlace">
            <s:param name="idChild" value="%{id}" />
            <s:param name="idPositionGeographique" value="%{id}" />
            <s:param name="idItineraire" value="%{idItineraire}"/>
            <s:param name="idLigne" value="%{idLigne}"/>
            <s:param name="actionSuivante" value="%{actionSuivante}"/>
          </s:url>
          <s:a href="%{removeUrl}">
            <img border="0" alt="Delete" src="<s:url value='/images/supprimer.png'/>" title="<s:text name="tooltip.delete"/>">
          </s:a>
        </display:column>
        <display:column titleKey="table.title.name">
          <s:text name="text.zone"/>	<s:property value="%{#attr.father.name}"/>
        </display:column>
        <display:column titleKey="table.title.type">
          <s:text name="%{#attr.father.areaType}"/>
        </display:column>
      </display:table>
    </s:div>
    <%-- Formulaire de recherche de zone parente --%>
    <div ID="father">
      <s:form id="areaSearchForm" action="search" namespace="/stopPlace">
        <s:hidden name="idPositionGeographique" value="%{id}"/>
        <s:hidden name="actionSuivante" value="addFather"/>
        <s:hidden name="authorizedType" value="%{authorizedType}" />
        <s:if test="father.id != null">
          <s:submit key="action.replace" />
        </s:if>
        <s:else>
          <s:submit key="action.add" />
        </s:else>
      </s:form>
    </div>
  </div>
</s:if>	