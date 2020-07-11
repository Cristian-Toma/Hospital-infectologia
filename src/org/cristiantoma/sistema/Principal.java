package org.cristiantoma.sistema;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.cristiantoma.control.MenuPrincipalController;
import org.cristiantoma.db.Conexion;
import org.cristiantoma.control.AreasController;
import org.cristiantoma.control.CargosController;
import org.cristiantoma.control.ContactoUrgenciaController;
import org.cristiantoma.control.EspecialidadesController;
import org.cristiantoma.control.HorariosController;
import org.cristiantoma.control.MedicoController;
import org.cristiantoma.control.MedicoEspecialidadController;
import org.cristiantoma.control.PacientesController;
import org.cristiantoma.control.ProgramadorController;
import org.cristiantoma.control.ResponsableTurnoController;
import org.cristiantoma.control.TelefonoMedicoController;
import org.cristiantoma.control.TurnoController;

public class Principal extends Application {
    private final String PAQUETE_VISTA = "/org/cristiantoma/view/";
    private Stage escenarioPrincipal;
    private Scene escena;

    
    @Override
   
    public void start(Stage escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
        escenarioPrincipal.setTitle("HOSPITAL DE INFECTOLOGIA");
        menuPrincipal();
        escenarioPrincipal.show();
        try{
        Connection conn = Conexion.getInstancia().getConexion();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("Select * from medicos");
       
       
       while (rs.next()){
           System.out.println(rs.getInt(1));
    }
        }catch(SQLException e){
            e.printStackTrace();
        }
    } 
    
    public void menuPrincipal(){
        try{
            MenuPrincipalController  menuPrincipal = (MenuPrincipalController)cambiarEscena("MenuPrincipalView.fxml",600,336);
            menuPrincipal.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        } 
    }
    
     public void ventanaMedicos(){
        try{
            MedicoController medicoControl =(MedicoController) cambiarEscena("MedicoView.fxml",1227,740);
            medicoControl.setEscenarioPrincipal(this);        
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaProgramador(){
        try{
            ProgramadorController programador = (ProgramadorController) cambiarEscena ("ProgramadorView.fxml",600,400);
            programador.setEscenarioPrincipal(this);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaPacientes(){
        try{
            PacientesController pacientes = (PacientesController) cambiarEscena ("PacienteView.fxml",1000,676);
            pacientes.setEscenarioPrincipal(this);
        } catch(Exception e){
            e.printStackTrace();
        } 
    }
    
    public void ventanaAreas(){
        try{
            AreasController areas = (AreasController) cambiarEscena("AreaViw.fxml",511,556);
            areas.setEscenarioPrincipal(this);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaHorarios(){
        try{
            HorariosController horario = (HorariosController) cambiarEscena("HorariosView.fxml",978,534);
            horario.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanResponsableTurno(){
        try{
            ResponsableTurnoController responsable = (ResponsableTurnoController) cambiarEscena("ResponsableTurnoView.fxml",1112,534);
            responsable.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaMedicoEspecialidad(){
        try{
            MedicoEspecialidadController medicoEspecialidad  = (MedicoEspecialidadController) cambiarEscena("MedicoEspecialidadView.fxml",689,534);
            medicoEspecialidad.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaCargos(){
        try{
            CargosController cargos = (CargosController) cambiarEscena("CargosView.fxml",528,553);
            cargos.setEscenarioPrincipal(this);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaContactoUrgencia(){
        try{
           ContactoUrgenciaController contactoUrgencia = (ContactoUrgenciaController) cambiarEscena ("ContactoUrgenciaView.fxml",881,551);
           contactoUrgencia.setEscenarioPrincipal(this);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaEspecialidades(){
        try{
            EspecialidadesController especialidades = (EspecialidadesController) cambiarEscena("EspecialidadesView.fxml",501,513);
            especialidades.setEscenarioPrincipal(this);
        } catch(Exception e){
            e.printStackTrace();
    
        }
    }   

    public void ventanaTurnos(){
        try{
            TurnoController turnos = (TurnoController) cambiarEscena("TurnosView.fxml",978,534);
            turnos.setEscenarioPrincipal(this);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaTelefonoMedicos(){
        try{
            TelefonoMedicoController telefonoMedicoController = (TelefonoMedicoController)cambiarEscena("TelefonoMedicoView.fxml", 682,542);
            telefonoMedicoController.setEscenarioPrincipal(this);
            
        }catch(Exception e){
            e.printStackTrace();}
        }
    
    
    
    
    public Initializable cambiarEscena(String fxml, int ancho, int alto) throws  Exception{
            
            Initializable resultado = null;
            FXMLLoader cargadorFXML = new FXMLLoader();
            InputStream archivo = Principal.class.getResourceAsStream(PAQUETE_VISTA+fxml);
            
            cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
            cargadorFXML.setLocation(Principal.class.getResource(PAQUETE_VISTA+fxml));
            
            escena = new Scene((AnchorPane)cargadorFXML.load(archivo),ancho,alto);
            escenarioPrincipal.setScene(escena);
            escenarioPrincipal.sizeToScene();
            escenarioPrincipal.show();
             
            resultado = (Initializable)cargadorFXML.getController();
            
            return resultado;
    }
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
