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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.cristiantoma.bean.Especialidad;
import org.cristiantoma.db.Conexion;
import org.cristiantoma.report.HacerReporte;
import org.cristiantoma.sistema.Principal;

public class EspecialidadesController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NINGUNO, NUEVO, GUARDAR, CANCELAR, ACTUALIZAR, ElIIMINAR, EDITAR};
    private operaciones tipoDeOperacion = operaciones.NINGUNO; 
    private ObservableList<Especialidad> listaEspecialidad;

    @FXML private ComboBox cmbCodigoEspecialidad;
    @FXML private TextField txtNombreEspecialidad;
    @FXML private TableView tblEspecialidades;
    @FXML private TableColumn colCodigoEspecialidad;
    @FXML private TableColumn colNombreEspecialidad;
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnReporte;
            
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        desactivarControles();
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblEspecialidades.setItems(getEspecialidades());
        colCodigoEspecialidad.setCellValueFactory(new PropertyValueFactory<Especialidad, Integer>("codigoEspecialidad"));
        colNombreEspecialidad.setCellValueFactory(new PropertyValueFactory<Especialidad, String>("nombreEspecialidad"));
    }
   
    public void seleccionarDatos(){
        txtNombreEspecialidad.setText(((Especialidad)tblEspecialidades.getSelectionModel().getSelectedItem()).getNombreEspecialidad());
    }
    
    public ObservableList<Especialidad> getEspecialidades(){
        ArrayList<Especialidad> lista= new ArrayList<Especialidad>();
        try{
            PreparedStatement select = Conexion.getInstancia().getConexion().prepareCall("{call sp_selectEspecialidades()}");
            ResultSet resultado = select.executeQuery();
            while(resultado.next()){
                lista.add(new Especialidad(resultado.getInt("codigoEspecialidad"),resultado.getString("nombreEspecialidad")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaEspecialidad = FXCollections.observableList(lista);
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
                
                guardar();
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
                
            break;    
        }
    }
  
        public void guardar(){
        Especialidad registro = new Especialidad();
        registro.setNombreEspecialidad(txtNombreEspecialidad.getText());
        try{
            PreparedStatement guardar = Conexion.getInstancia().getConexion().prepareCall("{call sp_addEspecialidades(?)}");
                    guardar.setString(1, registro.getNombreEspecialidad());
                    guardar.execute();
                    listaEspecialidad.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }    

    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
            
            if(tblEspecialidades.getSelectionModel().getSelectedItem() != null){
                btnNuevo.setDisable(true);
                btnEditar.setText("Actualizar");
                btnEliminar.setText("Cancelar");
                btnReporte.setDisable(true);
                activarControles();
                tipoDeOperacion = operaciones.ACTUALIZAR;
            }else{
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
            
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement editar = Conexion.getInstancia().getConexion().prepareCall("{call sp_updateEspecialidades(?,?)}");
            
            Especialidad registro = (Especialidad)tblEspecialidades.getSelectionModel().getSelectedItem();
            registro.setNombreEspecialidad(txtNombreEspecialidad.getText());
            
            editar.setInt(1, registro.getCodigoEspecialidad());
            editar.setString(2, registro.getNombreEspecialidad());
            
            editar.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                
                    desactivarControles();
                    limpiarControles();
                    btnNuevo.setText("Nuevo");
                    btnEditar.setDisable(false);
                    btnEliminar.setText("Eliminar");
                    btnReporte.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
            
            break;
            
            default:
            if(tblEspecialidades.getSelectionModel().getSelectedItem() == null){
                JOptionPane.showMessageDialog(null,"ERROR: DEBE SELECCIONAR UN ELEMENTO");
            }
            if(tblEspecialidades.getSelectionModel().getSelectedItem() != null){
                int respuesta = JOptionPane.showConfirmDialog(null,"¿DESEA ELIMINAR ESTE REGISTRO?","ELIMINAR ESPECIALIDAD",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                          PreparedStatement eliminar = Conexion.getInstancia().getConexion().prepareCall("{call sp_deleteEspecialidades(?)}");
                                 eliminar.setInt(1, ((Especialidad)tblEspecialidades.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
                                  eliminar.execute();
                                  listaEspecialidad.remove(tblEspecialidades.getSelectionModel().getSelectedIndex());
                                  limpiarControles();
                        }catch(Exception e){
                             e.printStackTrace();
                        }
                        }
                        else{
                    
                        }
                    }
            break;
        }
    }
    
    public void imprimirReporte(){
        Map parametro = new HashMap();
        parametro.put("codigoEspecialidad",null);
        HacerReporte.cargarReporte("reportEspecialidad.jasper","Reporte Especialidades", parametro);
    }
    
    public void reporte(){
        switch(tipoDeOperacion){
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoDeOperacion = operaciones.NINGUNO;
                tblEspecialidades.getSelectionModel().clearSelection();
            break;
            case NINGUNO:
                if(tblEspecialidades.getSelectionModel().getSelectedItem()!=null){
                    imprimirReporte();
                    limpiarControles();
                }else{
                    imprimirReporte();
                }
        }
    }
    
    public void desactivarControles(){
        txtNombreEspecialidad.setDisable(true);
    }
    
    public void activarControles(){
        txtNombreEspecialidad.setDisable(false);
    }
    
    public void limpiarControles(){
        txtNombreEspecialidad.setText("");
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
