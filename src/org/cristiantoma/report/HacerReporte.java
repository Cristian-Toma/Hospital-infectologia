package org.cristiantoma.report;


import java.io.InputStream;
import java.util.Map;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.cristiantoma.db.Conexion;

public class HacerReporte {
     public static void  cargarReporte (String nombreReporte, String titulo, Map parametros)
    {
        InputStream reporte = HacerReporte.class.getResourceAsStream(nombreReporte);
            try
            {
                JasperReport reporteMaestro = (JasperReport)JRLoader.loadObject(reporte);
                JasperPrint reporteIngreso = JasperFillManager.fillReport(reporteMaestro, parametros,Conexion.getInstancia().getConexion());
                JasperViewer visor = new JasperViewer (reporteIngreso,false);
                visor.setTitle(titulo);
                visor.setVisible(true);
    
            }catch(Exception e){
                    e.printStackTrace();
            }
    }
    
    
}
