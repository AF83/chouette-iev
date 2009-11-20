<%@ taglib prefix="s" uri="/struts-tags" %>
<%-- Titre et barre de navigation --%>
<s:url id="urlUpdate" value="/line/edit">
  <s:param name="idLigne" value="%{id}"/>
</s:url>
<s:if test="id != null">
  <title><s:text name="text.ligne.update.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.ligne.update.title'), '', #urlLigneUpdate)"/>
</s:if>
<s:else>
  <title><s:text name="text.ligne.create.title" /></title>
  <s:property value="filAriane.addElementFilAriane(getText('text.ligne.create.title'), '', #urlLigneUpdate)"/>
</s:else>
<div class="panelData">
  <s:property value="filAriane.texteFilAriane" escape="false"/>
</div>
<br>
<s:form> 
  <s:hidden name="idLigne" value="%{id}"/>
  <s:hidden name="operationMode" value="STORE" />
  <s:hidden key="actionMethod" value="%{actionMethod}"/>
  <%-- Valeur s�lectionn� par d�faut est contenue dans value (chaineIdReseau) et doit �tre une cha�ne de caract�re obligatoirement --%>
  <s:select key="ligne.idReseau" name="idReseau" label="%{getText('ligne.idReseau')}" value="%{idReseau}" list="reseaux" listKey="id" listValue="name" headerKey="-1" headerValue="%{getText('ligne.aucunReseau')}">
  </s:select>
  <%-- Valeur s�lectionn� par d�faut est contenue dans value (chaineIdTransporteur) et doit �tre une cha�ne de caract�re obligatoirement --%>
  <s:select key="ligne.idTransporteur" name="idTransporteur" label="%{getText('ligne.idTransporteur')}" value="%{idTransporteur}" list="transporteurs" listKey="id" listValue="name"  headerKey="-1" headerValue="%{getText('ligne.aucunTransporteur')}">
  </s:select>
  <s:textfield key="ligne.name" name="name" required="true"/>
  <s:textfield key="ligne.publishedName" name="publishedName" />
  <s:textfield key="ligne.registrationNumber" name="registrationNumber" required="true"/>
  <s:textfield key="ligne.number" name="number" />
  <s:if test="ligne.id != null">
    <s:select key="ligne.transportModeName" name="transportModeName" list="@fr.certu.chouette.struts.enumeration.EnumerationApplication@getModeTransportEnum()" listKey="enumeratedTypeAccess" listValue="textePropriete"/>
  </s:if>
  <s:else>
    <s:select key="ligne.transportModeName" name="transportModeName" list="@fr.certu.chouette.struts.enumeration.EnumerationApplication@getModeTransportEnum()" listKey="enumeratedTypeAccess" listValue="textePropriete" value="@chouette.schema.types.TransportModeNameType@BUS"/>
  </s:else>
  <s:textfield key="ligne.comment" name="comment"/>

  <%-- Actions --%>
  <tr>
    <td colspan="2">
      <s:if test="id != null">
        <s:submit key="action.update" action="%{actionMethod}"  theme="simple" cssStyle="float: right;"/>
      </s:if>
      <s:else>
        <s:submit key="action.create" action="%{actionMethod}" theme="simple" cssStyle="float: right;"/>
      </s:else>
      <s:submit key="action.cancel" action="%{actionMethod}" theme="simple" cssStyle="float: right;"/>
    </td>
  </tr>

  <%-- Ajout des balises tr et td pour le faire apparaitre dans le tableau --%>
  <tr><td colspan="2"><s:include value="/jsp/commun/asterisque.jsp" /></td></tr>
</s:form>