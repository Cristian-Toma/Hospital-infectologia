package org.cristiantoma.control;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.cristiantoma.sistema.Principal;


public class MenuPrincipalController implements Initializable{
    private Principal escenarioPrincipal;
    @Override
    
    public void initialize(URL location, ResourceBundle resources) {
        
   } 
    public Principal getEscenarioPrincipal(){
        return escenarioPrincipal;
    }
    public void setEscenarioPrincipal (Principal escenarioPrincipal){
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
     public void ventanaMedicos(){
         escenarioPrincipal.ventanaMedicos();
     }
     
     public void ventanaProgramador(){
         escenarioPrincipal.ventanaProgramador();
     }
    
     public void ventanaPacientes(){
         escenarioPrincipal.ventanaPacientes();
     }
     
     public void ventanaAreas(){
         escenarioPrincipal.ventanaAreas();
     }
     public void ventanaCargos(){
         escenarioPrincipal.ventanaCargos();
     }
     
     public void ventanaContactoUrgencia(){
         escenarioPrincipal.ventanaContactoUrgencia();
     }
     
     public void ventanaEspecialidades(){
         escenarioPrincipal.ventanaEspecialidades();
     }
     
     
     public void ventanaTurnos(){
         escenarioPrincipal.ventanaTurnos();
     }
     
     public void ventanaMedicoEspecialidad(){
         escenarioPrincipal.ventanaMedicoEspecialidad();     
     }
     
     public void ventanaResponsableTurno(){
         escenarioPrincipal.ventanResponsableTurno();
     }
     
     public void ventanaHorarios(){
         escenarioPrincipal.ventanaHorarios();
     }
     
     public void ventanaTelefonoMedicos(){
         escenarioPrincipal.ventanaTelefonoMedicos();
     }
}