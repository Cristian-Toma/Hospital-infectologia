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
import org.cristiantoma.bean.Area;
import org.cristiantoma.bean.Cargo;
import org.cristiantoma.bean.ResponsableTurno;
import org.cristiantoma.db.Conexion;
import org.cristiantoma.report.HacerReporte;
import org.cristiantoma.sistema.Principal;

public class ResponsableTurnoController implements Initializable{
    private enum operaciones{NUEVO, ACTUALIZAR, EDITAR, GUARDAR, ELIMINAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion =  operaciones.NINGUNO;
    private ObservableList<ResponsableTurno> listaResponsableTurno;
    private ObservableList<Cargo> listaCargo;
    private ObservableList<Area> listaArea;
  
    @FXML private TextField txtApellidosResponsable;
    @FXML private TextField txtNombreResponsable;
    @FXML private TextField txtTelefonoPersonal;
    @FXML private ComboBox cmbCodigoResponsableTurno;
    @FXML private ComboBox cmbCodigoArea;
    @FXML private ComboBox cmbCodigoCargo;
    @FXML private TableColumn colCodigoResponsableTurno;
    @FXML private TableColumn colNombreResponsable;
    @FXML private TableColumn colApellidosResponsable;
    @FXML private TableColumn colTelefonoPersonal;
    @FXML private TableColumn colCodigoArea;
    @FXML private TableColumn colCodigioCargo;
    @FXML private TableView tblResponsableTurno;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
     cargarDatos();
     desactivarControles();
     cmbCodigoArea.setItems(getAreas());
     cmbCodigoCargo.setItems(getCargos());
    }
    
    public void cargarDatos(){
        tblResponsableTurno.setItems(getResponsableTurno());
        colCodigoResponsableTurno.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, Integer>("codigoResponsableTurno"));
        colTelefonoPersonal.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, String>("nombreResponsable"));
        colNombreResponsable.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, String>("apellidosResponsable"));
        colApellidosResponsable.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, String>("telefonoPersonal"));
        colCodigoArea.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, Integer>("codigoArea"));
        colCodigioCargo.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, Integer>("codigoCargo"));
    }
    
    public void seleccionarDatos(){
        txtTelefonoPersonal.setText(((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getTelefonoPersonal());
        txtNombreResponsable.setText(((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getNombreResponsable());
        txtApellidosResponsable.setText(((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getApellidosResponsable());
    }
    
    public ObservableList<ResponsableTurno> getResponsableTurno(){
        ArrayList<ResponsableTurno> lista = new ArrayList<ResponsableTurno>();
        try{
            PreparedStatement mostrar = Conexion.getInstancia().getConexion().prepareCall("{call sp_selectResponsableTurno()}");
            ResultSet resultado = mostrar.executeQuery();
            while(resultado.next()){
                lista.add(new ResponsableTurno(resultado.getInt("codigoResponsableTurno"),resultado.getString("nombreResponsable"),resultado.getString("apellidosResponsable"),resultado.getString("telefonoPersonal"),resultado.getInt("codigoArea"),resultado.getInt("codigoCargo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaResponsableTurno = FXCollections.observableList(lista);
    }
     
    public ObservableList<Area> getAreas(){
    
       ArrayList<Area> lista = new ArrayList<Area>();
       
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_selectAreas()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Area(resultado.getInt("codigoArea"), resultado.getString("nombreArea")));
            }
        }
          catch(Exception e){
            e.printStackTrace();
        }
        
        return listaArea = FXCollections.observableList(lista);
    
    }
    
    public ObservableList<Cargo> getCargos(){
    
       ArrayList<Cargo> lista = new ArrayList<Cargo>();
       
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_selectCargos}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Cargo(resultado.getInt("codigoCargo"), resultado.getString("nombreCargo")));
            }
        }
          catch(Exception e){
            e.printStackTrace();
        }
        
        return listaCargo = FXCollections.observableList(lista);
    
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
        ResponsableTurno registro = new ResponsableTurno();
        registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
        registro.setApellidosResponsable(txtApellidosResponsable.getText());
        registro.setNombreResponsable(txtNombreResponsable.getText());
        registro.setCodigoArea(((Area) cmbCodigoArea.getSelectionModel().getSelectedItem()).getCodigoArea());
        registro.setCodigoCargo(((Cargo) cmbCodigoCargo.getSelectionModel().getSelectedItem()).getCodigoCargo());
        try{
            PreparedStatement guardar = Conexion.getInstancia().getConexion().prepareCall("{call sp_addResponsableTurno(?,?,?,?,?)}");
            guardar.setString(1, registro.getTelefonoPersonal());                                
            guardar.setString(2, registro.getApellidosResponsable());
            guardar.setString(3, registro.getNombreResponsable());
            guardar.setInt(4, registro.getCodigoArea());
            guardar.setInt(5, registro.getCodigoCargo());
            guardar.execute();
            listaResponsableTurno.add(registro);
            }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblResponsableTurno.getSelectionModel().getSelectedItem() != null){
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
            
            ResponsableTurno registro = (ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem();
            registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
            registro.setApellidosResponsable(txtApellidosResponsable.getText());
            registro.setNombreResponsable(txtNombreResponsable.getText());
            registro.setCodigoArea(((Area) cmbCodigoArea.getSelectionModel().getSelectedItem()).getCodigoArea());
            registro.setCodigoCargo(((Cargo) cmbCodigoCargo.getSelectionModel().getSelectedItem()).getCodigoCargo());
            
            actualizar.setInt(1, registro.getCodigoResponsableTurno());
            actualizar.setString(2, registro.getTelefonoPersonal());
            actualizar.setString(3, registro.getApellidosResponsable());
            actualizar.setString(4, registro.getNombreResponsable());
            
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
                if(tblResponsableTurno.getSelectionModel().getSelectedItem() == null){
                    JOptionPane.showMessageDialog(null,"DEBE SELECCIONAR UN ELEMENTO");
                }
                if(tblResponsableTurno.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿DESEA ELIMINAR ESTE ELEMENTO?","ELIMINAR TELEFONO MEDICO",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                        if(respuesta == JOptionPane.YES_OPTION){
                            try{
                            PreparedStatement eliminar = Conexion.getInstancia().getConexion().prepareCall("{call sp_deleteTelefonosMedicos(?)}");
                            eliminar.setInt(1, ((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
                            eliminar.execute();
                            listaResponsableTurno.remove(tblResponsableTurno.getSelectionModel().getSelectedIndex());
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
        parametros.put("codigoArea", null);
        HacerReporte.cargarReporte("reportResponsableTurno.jasper", "Reporte Areas", parametros);
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
                    tblResponsableTurno.getSelectionModel().clearSelection();
                    break;
                    
            case NINGUNO:
                if(tblResponsableTurno.getSelectionModel().getSelectedItem()!=null){
                    imprimirReporte();
                    limpiarControles();
                }else{
                    imprimirReporte();
                }
        }
    }
    
    public void limpiarControles(){
        txtApellidosResponsable.setText("");
        txtTelefonoPersonal.setText("");
        txtNombreResponsable.setText("");
        cmbCodigoArea.getSelectionModel().clearSelection();
        cmbCodigoCargo.getSelectionModel().clearSelection();
    }
    
    public void activarControles(){
        txtTelefonoPersonal.setEditable(true);
        txtApellidosResponsable.setEditable(true);
        txtNombreResponsable.setEditable(true);
    }
    
    public void desactivarControles(){
        txtNombreResponsable.setEditable(false);
        txtTelefonoPersonal.setEditable(false);
        txtApellidosResponsable.setEditable(false);
        cmbCodigoArea.setEditable(false);
        cmbCodigoCargo.setEditable(false);
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