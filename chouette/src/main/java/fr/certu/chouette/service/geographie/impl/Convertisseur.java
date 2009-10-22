package fr.certu.chouette.service.geographie.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import fr.certu.chouette.service.geographie.IConvertisseur;

public class Convertisseur implements IConvertisseur {
	
	private static final Logger                  logger             = Logger.getLogger(Convertisseur.class);
	private              DriverManagerDataSource managerDataSource;
	private              int                     lambert2SRID;      // 27561
	private              int                     wgs84SRID;         // 4326
	
	public void deLambert2AWGS84() {
		Connection connexion = null;
		try {
			Class.forName(managerDataSource.getDriverClassName());
			Properties props = new Properties();
			props.setProperty("user",managerDataSource.getUsername());
			props.setProperty("password",managerDataSource.getPassword());
			props.setProperty("allowEncodingChanges","true");
			connexion = DriverManager.getConnection(managerDataSource.getUrl(), props);
			connexion.setAutoCommit(false);
			String selection = "SELECT id, x, y FROM stoparea;";
			Statement sqlStatement = connexion.createStatement();
			ResultSet rs = sqlStatement.executeQuery(selection);
			while (rs.next()) {
				String id = rs.getObject(1).toString();
				if (rs.getObject(2) == null)
					continue;
				String x = rs.getObject(2).toString();
				if (rs.getObject(3) == null)
					continue;
				String y = rs.getObject(3).toString();
				if ((x == null) || (x.trim().length() == 0))
					continue;
				if ((y == null) || (y.trim().length() == 0))
					continue;
				String update = "UPDATE stoparea SET longitude = x(transform(GeometryFromText('POINT("+x+" "+y+")', "+lambert2SRID+"), "+wgs84SRID+")), latitude = y(transform(GeometryFromText('POINT("+x+" "+y+")', 27561), 4326)) WHERE id='"+id+"';";
				Statement statement = connexion.createStatement();
				statement.executeUpdate(update);
			}
			connexion.commit();
		}
		catch(Exception e) {
			try {
				logger.debug("Annuler conversion Lambert2 à WGS84 :"+e.getMessage(), e);
				if (connexion != null)
					connexion.rollback();
			}
			catch(Exception ex) {
				logger.error("Echec de la tentative de rollback de la transaction de conversion "+ex.getMessage(), ex);
			}
			throw new RuntimeException(e);
		}
		finally {
			try {
				if (connexion != null)
					connexion.close();
			}
			catch(Exception e) {
				logger.error("Echec de la tentative de fermeture de la connexion pour conversion "+e.getMessage(), e);
			}
		}
	}
	
	public void setManagerDataSource(DriverManagerDataSource managerDataSource) {
		this.managerDataSource = managerDataSource;
	}
	
	public void setLambert2SRID(int lambert2SRID) {
		this.lambert2SRID = lambert2SRID;
	}
	
	public void setWgs84SRID(int wgs84SRID) {
		this.wgs84SRID = wgs84SRID;
	}
}
