package org.cristiantoma.control;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.cristiantoma.bean.Especialidad;
import org.cristiantoma.bean.Horario;
import org.cristiantoma.bean.MedicoEspecialidad;
import org.cristiantoma.db.Conexion;
import org.cristiantoma.report.HacerReporte;
import org.cristiantoma.sistema.Principal;


public class MedicoEspecialidadController  implements Initializable{
   private enum operaciones{NUEVO, ACTUALIZAR, EDITAR, GUARDAR, ELIMINAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion =  operaciones.NINGUNO;
    private ObservableList <MedicoEspecialidad> listaMedicoEspecialidad;
    private ObservableList<Especialidad> listaEspecialidad;
    private ObservableList<Horario> listaHorario;
    
    @FXML private ComboBox cmbCodigoMedicoEspecialidad;
    @FXML private ComboBox cmbCodigoEspecialidad;
    @FXML private ComboBox cmbCodigoHorario;
    @FXML private TableView tblMedicoEsecialidades;
    @FXML private TableColumn colCodigoMedicoEspecialidad;
    @FXML private TableColumn colCodigoEspecialidad;
    @FXML private TableColumn colCodigoHorario;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
   
    @Override
    public void initialize(URL location, ResourceBundle resources) {
     cargarDatos();
     desactivarControles();
     cmbCodigoEspecialidad.setItems(getEspecialidad());
     cmbCodigoHorario.setItems(getHorario());
    }
    
    public void cargarDatos(){
        tblMedicoEsecialidades.setItems(getMedicoEspecialidad());
        colCodigoMedicoEspecialidad.setCellValueFactory(new PropertyValueFactory<MedicoEspecialidad, Integer>("codigoMedicoEspecialidad"));
        colCodigoEspecialidad.setCellValueFactory(new PropertyValueFactory<MedicoEspecialidad, Integer>("codigoEspecialidad"));
        colCodigoHorario.setCellValueFactory(new PropertyValueFactory<MedicoEspecialidad, Integer>("codigoHorario"));
    }
    
    public void seleccionarDatos(){
        cmbCodigoEspecialidad.setValue(((MedicoEspecialidad)tblMedicoEsecialidades.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
        cmbCodigoHorario.setValue(((MedicoEspecialidad)tblMedicoEsecialidades.getSelectionModel().getSelectedItem()).getCodigoHorario());
    }
    
    public ObservableList<MedicoEspecialidad> getMedicoEspecialidad(){
        ArrayList<MedicoEspecialidad> lista = new ArrayList<MedicoEspecialidad>();
        try{
            PreparedStatement mostrar = Conexion.getInstancia().getConexion().prepareCall("{call sp_selectMedicoEspecialidad()}");
            ResultSet resultado = mostrar.executeQuery();
            while(resultado.next()){
                lista.add(new MedicoEspecialidad(resultado.getInt("codigoMedicoEspecialidad"),resultado.getInt("codigoEspecialidad"),resultado.getInt("codigoHorario")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaMedicoEspecialidad = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Especialidad>getEspecialidad(){
        ArrayList<Especialidad> lista = new ArrayList<Especialidad>();
       
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_selectEspecialidades()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Especialidad(resultado.getInt("codigoEspecialidad"), resultado.getString("nombreEspecialidad")));
            }
        }
          catch(Exception e){
            e.printStackTrace();
        }
        
        return listaEspecialidad = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Horario>getHorario(){
        ArrayList<Horario> lista = new ArrayList<Horario>();
       
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_selectHorarios()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Horario(resultado.getInt("codigoHorario"), resultado.getString("horarioInicio"),resultado.getString("horarioSalida"),resultado.getBoolean("lunes"),resultado.getBoolean("martes"),resultado.getBoolean("miercoles"),resultado.getBoolean("jueves"),resultado.getBoolean("viernes")));
            }
        }
          catch(Exception e){
            e.printStackTrace();
        }
        
        return listaHorario = FXCollections.observableArrayList(lista);
    }
    
   public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:

                    activarControles();
                    btnNuevo.setText("Guardar");
                    btnEliminar.setText("Cancelar");
                    btnEditar.setDisable(true);
                    btnReporte.setDisable(true);
                    tipoDeOperacion = operaciones.GUARDAR;
            
             break;

             case GUARDAR:
             
                    agregar();
                    desactivarControles();
                    limpiarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    cargarDatos();
                
             break;      
        }
    }
    
   public void agregar(){
        MedicoEspecialidad registro = new MedicoEspecialidad();
        registro.setCodigoEspecialidad( ( (Especialidad) cmbCodigoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
        registro.setCodigoHorario( ( (Horario) cmbCodigoHorario.getSelectionModel().getSelectedItem()).getCodigoHorario());
        
        try{
            PreparedStatement guardar = Conexion.getInstancia().getConexion().prepareCall("{call sp_addMedicoEspecialidad(?,?)}");
            guardar.setInt(1, registro.getCodigoEspecialidad());                                
            guardar.setInt(2, registro.getCodigoHorario());
            guardar.execute();
            listaMedicoEspecialidad.add(registro);
            }catch(Exception e){
            e.printStackTrace();
        }
    }
    
   public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblMedicoEsecialidades.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    activarControles();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    }
                else{
                    JOptionPane.showMessageDialog(null,"DEBE SELECCIONAR UN ELEMENTO");
                }
            break;
            
            case ACTUALIZAR:
                actualizar();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoDeOperacion = operaciones.NINGUNO;
                limpiarControles();
                cargarDatos();
        }
    }
    
    public void actualizar(){
        try{
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                agregar();
                    btnReporte.setDisable(false);
                    btnEditar.setDisable(false);
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    tipoDeOperacion = operaciones.NINGUNO;
            break;
            default:
                if(tblMedicoEsecialidades.getSelectionModel().getSelectedItem() == null){
                    JOptionPane.showMessageDialog(null,"DEBE SELECCIONAR UN ELEMENTO");
                }
                if(tblMedicoEsecialidades.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿DESEA ELIMINAR ESTE ELEMENTO?","ELIMINAR TELEFONO MEDICO",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                        if(respuesta == JOptionPane.YES_OPTION){
                            try{
                            PreparedStatement eliminar = Conexion.getInstancia().getConexion().prepareCall("{call sp_deleteMedicoEspecialidad(?)}");
                            eliminar.setInt(1, ((MedicoEspecialidad)tblMedicoEsecialidades.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad());
                            eliminar.execute();
                            listaMedicoEspecialidad.remove(tblMedicoEsecialidades.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                    }
                }
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoMedicoEspecialidad", null);
        HacerReporte.cargarReporte("reportMedicoEspecialidad.jasper", "Reporte Areas", parametros);
    }
    
    public void reporte(){
        switch(tipoDeOperacion){
            case ACTUALIZAR:
                    desactivarControles();
                    limpiarControles();
                    btnEditar.setText("Editar");
                    btnReporte.setText("Reporte");
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    tblMedicoEsecialidades.getSelectionModel().clearSelection();
                    break;
                    
            case NINGUNO:
                if(tblMedicoEsecialidades.getSelectionModel().getSelectedItem()!=null){
                    imprimirReporte();
                    limpiarControles();
                }else{
                    imprimirReporte();
                }
        }
    }
    
    public void limpiarControles(){
        cmbCodigoHorario.getSelectionModel().clearSelection();
        cmbCodigoEspecialidad.getSelectionModel().clearSelection();
    }
    
    public void activarControles(){
        cmbCodigoHorario.setEditable(false);
        cmbCodigoEspecialidad.setEditable(false);
    }
    
    public void desactivarControles(){
        cmbCodigoHorario.setEditable(false);
        cmbCodigoEspecialidad.setEditable(false);
    }
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
}



