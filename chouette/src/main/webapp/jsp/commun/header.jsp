<%@ taglib uri="/struts-tags" prefix="s"%>
<div id="header">
  <div class="title">
    <a href="http://www.ecologie.gouv.fr/developpement-durable" target="_blank">
      <img alt="Visiter le site du Minist�re de l'Ecologie, du D�veloppement et de l'Am�nagement Durable" class="logoMedad" >
    </a>
    <ul class="certu">
      <li>
        <a href="http://www.certu.fr" target="_blank">
          <img alt="Visiter le site du Centre d'Etudes sur les R�seaux, les Transports, l'Urbanisme et les constructions publiques" class="logoCertu">
        </a>
      </li>
      <li>
        <a href="http://www.predim.org" target="_blank">
          <img border="0" alt="Visiter le site de la Plate-forme de Recherche et d'Exp�rimentation pour le D�veloppement de l'information Multimodale" class="logoPredim">
        </a>
      </li>
    </ul>
    <ul class="chouette">
      <li class="title">
        Chouette
      </li>
      <li class="definition">(Cr�ation d'horaires avec un outil d'�change de donn�es TC selon le format Trident Europ�en)</li>
    </ul>
  </div>

  <div class="tools">
    <ul>
      <s:url action="AProposDe" id="aproposde"/>
      <li><s:a href="%{aproposde}"><s:text name="app.aproposde.title"/></s:a></li>
    </ul>
    <ul>
      <li><s:property value="principalProxy.remoteUser"/></li> |
      <s:url action="deconnexion" id="deconnexion" includeParams="none"/>
      <li><s:a href="%{deconnexion}" >D&eacute;connexion</s:a></li>
    </ul>
  </div>

</div>