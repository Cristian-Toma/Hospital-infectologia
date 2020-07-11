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
import org.cristiantoma.bean.Cargo;
import org.cristiantoma.db.Conexion;
import org.cristiantoma.report.HacerReporte;
import org.cristiantoma.sistema.Principal;

public class CargosController implements Initializable {
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, ACTUALIZAR, EDITAR, ELIMINAR, NINGUNO, GUARDAR, CANCELAR};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Cargo> listaCargo;
    
    @FXML private TextField txtNombreCargo;
    @FXML private ComboBox cmbCodigoCargo;
    @FXML private TableView tblCargos;
    @FXML private TableColumn colCodigoCargo;
    @FXML private TableColumn colNombreCargo;
    @FXML private Button btnNuevo; 
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       desactivarControles();
       cargarDatos();
    }
    
    public void seleccionarElemento(){
        txtNombreCargo.setText(((Cargo)tblCargos.getSelectionModel().getSelectedItem()).getNombreCargo());
    }
    
    public void cargarDatos(){
        
        tblCargos.setItems(getCargos());
        colCodigoCargo.setCellValueFactory(new PropertyValueFactory<Cargo, Integer>("codigoCargo"));
        colNombreCargo.setCellValueFactory(new PropertyValueFactory<Cargo, Integer>("nombreCargo"));    
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
    
     public Cargo buscarCargo(int codigoCargo){
         Cargo resultado = null;
         try{
             PreparedStatement buscar = Conexion.getInstancia().getConexion().prepareCall("{call sp_searchCargos(?)}");
             buscar.setInt(1, codigoCargo);
             ResultSet registro = buscar.executeQuery();
             while(registro.next()){
                 resultado = new Cargo(registro.getInt("codigoCargo"),registro.getString("nombreCargo"));
             } 
         }catch(Exception e){
             e.printStackTrace();
         }
         
         return resultado;
     }
    
    public void nuevo(){
    
        switch (tipoDeOperacion){
                case NINGUNO:

                    activarControles();
                    btnNuevo.setText("Guardar");
                    btnEliminar.setText("cancelar");
                    btnEditar.setDisable(true);
                    btnReporte.setDisable(true);
                    tipoDeOperacion=operaciones.GUARDAR;

                break;

                case GUARDAR:

                    guardar();
                    desactivarControles();
                    limpiarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tipoDeOperacion=operaciones.NINGUNO;
                    cargarDatos();
                    
                break;
                
        }
        
    }
        
    public void guardar(){
        
        Cargo registro = new Cargo();
        registro.setNombreCargo(txtNombreCargo.getText());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_addCargos(?)}");
            procedimiento.setString(1, registro.getNombreCargo());
            procedimiento.execute();
            listaCargo.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            
            case NINGUNO:
                if (tblCargos.getSelectionModel().getSelectedItem()!=null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnEliminar.setDisable(true);
                    btnNuevo.setDisable(true);
                    activarControles();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }
                else{
                    JOptionPane.showMessageDialog(null,"DEBE SELECCIONAR UN ELEMENTO");
                }
            break;
            
            case ACTUALIZAR:
                actualizar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
            
            break;   
        }
    }
    
    public void actualizar(){
         try{
             
             PreparedStatement actualizar = Conexion.getInstancia().getConexion().prepareCall("{call sp_updateCargos(?,?)}");
             
             Cargo registro = (Cargo)tblCargos.getSelectionModel().getSelectedItem();
             registro.setNombreCargo(txtNombreCargo.getText()); 
            
             actualizar.setInt(1, registro.getCodigoCargo());
             actualizar.setString(2, registro.getNombreCargo());;
             
             actualizar.execute();
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
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoDeOperacion = operaciones.NUEVO;
            break; 
            default:
                if(tblCargos.getSelectionModel().getSelectedItem() == null){
                                 JOptionPane.showMessageDialog(null,"ERROR: SELECCIONE UN ELEMENTO");
                }
                if(tblCargos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿SEGURO QUE QUIERE ELIMINAR EL REGISTRO?","Eliminar Cargo",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement eliminar = Conexion.getInstancia().getConexion().prepareCall("{call sp_deleteCargos(?)}");
                            eliminar.setInt(1, ((Cargo)tblCargos.getSelectionModel().getSelectedItem()).getCodigoCargo());
                            eliminar.execute();
                            listaCargo.remove(tblCargos.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    else{
                            
                    }
                    
                }
        }
        
        
    }
    
     public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoCargo", null);
        HacerReporte.cargarReporte("reportCargos.jasper", "Reporte Cargos", parametros);
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
                    tblCargos.getSelectionModel().clearSelection();
                    break;
                    
            case NINGUNO:
                if(tblCargos.getSelectionModel().getSelectedItem()!=null){
                    imprimirReporte();
                    limpiarControles();
                }else{
                    imprimirReporte();
                }
        }
    }
    
    public void desactivarControles(){
        cmbCodigoCargo.setEditable(false);
        txtNombreCargo.setEditable(false);
    }
    
    public void activarControles(){
        cmbCodigoCargo.setEditable(true);
        txtNombreCargo.setEditable(true);
    }
    
    public void limpiarControles(){
        cmbCodigoCargo.getSelectionModel().clearSelection();
        txtNombreCargo.setText("");
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
