package org.cristiantoma.control;


import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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
import org.cristiantoma.bean.Medico;
import org.cristiantoma.bean.TelefonoMedico;
import org.cristiantoma.db.Conexion;
import org.cristiantoma.sistema.Principal;

public class TelefonoMedicoController implements Initializable {
    
    private enum operaciones{NUEVO, ACTUALIZAR, EDITAR, GUARDAR, ELIMINAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion =  operaciones.NINGUNO;
    private ObservableList<Medico> listaMedico;
    
    private ObservableList<TelefonoMedico> listaTelefonoMedico;
  
    @FXML private TextField txtCodigoTelefonoMedico;
    @FXML private TextField txtTelefonoPersonal;
    @FXML private TextField txtTelefonoTrabajo;
    @FXML private ComboBox cmbCodigoMedico;
    @FXML private TableColumn colCodigoTelefonoMedico;
    @FXML private TableColumn colTelefonoPersonal;
    @FXML private TableColumn colTelefonoTrabajo;
    @FXML private TableColumn colCodigoMedico;
    @FXML private TableView tblTelefonosMedicos;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        desactivarControles();
        cargarDatos();
        cmbCodigoMedico.setItems(getMedicos());
    }
    
    public void cargarDatos(){
        tblTelefonosMedicos.setItems(getTelefonosMedicos());
        colCodigoTelefonoMedico.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, Integer>("codigoTelefonoMedico"));
        colTelefonoPersonal.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, String>("telefonoPersonal"));
        colTelefonoTrabajo.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, String>("telefonoTrabajo"));
        colCodigoMedico.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, Integer>("codigoMedico"));
    }
    
    public void seleccionarDatos(){
        txtTelefonoPersonal.setText(((TelefonoMedico)tblTelefonosMedicos.getSelectionModel().getSelectedItem()).getTelefonoPersonal());
        txtTelefonoTrabajo.setText(((TelefonoMedico)tblTelefonosMedicos.getSelectionModel().getSelectedItem()).getTelefonoTrabajo());
    }
    public ObservableList<TelefonoMedico> getTelefonosMedicos(){
        ArrayList<TelefonoMedico> lista = new ArrayList<TelefonoMedico>();
        try{
            PreparedStatement mostrar = Conexion.getInstancia().getConexion().prepareCall("{call sp_selectTelefonosMedicos()}");
            ResultSet resultado = mostrar.executeQuery();
            while(resultado.next()){
                lista.add(new TelefonoMedico(resultado.getInt("codigoTelefonoMedico"),resultado.getString("telefonoPersonal"),resultado.getString("telefonoTrabajo")/*,resultado.getInt("codigoMedico")*/));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaTelefonoMedico = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Medico>getMedicos(){
        ArrayList<Medico> lista = new ArrayList<Medico>();
       
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_selectMedicos()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Medico(resultado.getInt("codigoMedico"), resultado.getInt("licenciaMedica"), resultado.getString("nombres"), resultado.getString("apellidos"), resultado.getString("sexo"), resultado.getString("horaEntrada"), resultado.getString("horaSalida"), resultado.getInt("turnoMaximo")));
            }
        }
          catch(Exception e){
            e.printStackTrace();
        }
        
        return listaMedico = FXCollections.observableArrayList(lista);
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
        TelefonoMedico registro = new TelefonoMedico();
        registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
        registro.setTelefonoTrabajo(txtTelefonoPersonal.getText());
        registro.setCodigoMedico(( (Medico) cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
         try{
            PreparedStatement guardar = Conexion.getInstancia().getConexion().prepareCall("{call sp_addTelefonosMedicos(?,?,?)}");
            guardar.setString(1, registro.getTelefonoPersonal());                                
            guardar.setString(2, registro.getTelefonoTrabajo());
            guardar.setInt(3, registro.getCodigoMedico());
            guardar.execute();
            listaTelefonoMedico.add(registro);
            }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblTelefonosMedicos.getSelectionModel().getSelectedItem() != null){
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
            PreparedStatement actualizar = Conexion.getInstancia().getConexion().prepareCall("{call sp_updateTelefonosMedicos(?,?,?)}");
            
            TelefonoMedico registro = (TelefonoMedico)tblTelefonosMedicos.getSelectionModel().getSelectedItem();
            registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
            registro.setTelefonoTrabajo(txtTelefonoTrabajo.getText());
            
            actualizar.setInt(1, registro.getCodigoTelefonoMedico());
            actualizar.setString(2, registro.getTelefonoPersonal());
            actualizar.setString(3, registro.getTelefonoTrabajo());
            
            actualizar.execute();
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
                if(tblTelefonosMedicos.getSelectionModel().getSelectedItem() == null){
                    JOptionPane.showMessageDialog(null,"DEBE SELECCIONAR UN ELEMENTO");
                }
                if(tblTelefonosMedicos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿DESEA ELIMINAR ESTE ELEMENTO?","ELIMINAR TELEFONO MEDICO",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                        if(respuesta == JOptionPane.YES_OPTION){
                            try{
                            PreparedStatement eliminar = Conexion.getInstancia().getConexion().prepareCall("{call sp_deleteTelefonosMedicos(?)}");
                            eliminar.setInt(1, ((TelefonoMedico)tblTelefonosMedicos.getSelectionModel().getSelectedItem()).getCodigoTelefonoMedico());
                            eliminar.execute();
                            listaTelefonoMedico.remove(tblTelefonosMedicos.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                    }
                }
        }
    }
    
    public void limpiarControles(){
        txtCodigoTelefonoMedico.setText("");
        txtTelefonoPersonal.setText("");
        txtTelefonoTrabajo.setText("");
        cmbCodigoMedico.getSelectionModel().clearSelection();
    }
    
    public void activarControles(){
        txtTelefonoPersonal.setEditable(true);
        txtTelefonoTrabajo.setEditable(true);
        cmbCodigoMedico.setEditable(false);
    }
    
    public void desactivarControles(){
        txtCodigoTelefonoMedico.setEditable(false);
        txtTelefonoPersonal.setEditable(false);
        txtTelefonoTrabajo.setEditable(false);
        cmbCodigoMedico.setEditable(false);
    }
    
    
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
}

    
    
    

