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
import org.cristiantoma.db.Conexion;
import org.cristiantoma.report.HacerReporte;
import org.cristiantoma.sistema.Principal;

public class AreasController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, ACTUALIZAR, EDITAR, ELIMINAR, NINGUNO, GUARDAR, CANCELAR};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Area> listaArea;
    
    @FXML private TextField txtNombreArea;
    @FXML private ComboBox cmbCodigoArea;
    @FXML private TableView tblAreas;
    @FXML private TableColumn colCodigoArea;
    @FXML private TableColumn colNombreArea;
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
        txtNombreArea.setText(((Area)tblAreas.getSelectionModel().getSelectedItem()).getNombreArea());
    }
    
    public void cargarDatos(){
        
        tblAreas.setItems(getAreas());
        colCodigoArea.setCellValueFactory(new PropertyValueFactory<Area, Integer>("codigoArea"));
        colNombreArea.setCellValueFactory(new PropertyValueFactory<Area, Integer>("nombreArea"));    
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
    
     public Area buscarArea(int codigoArea){
         Area resultado = null;
         try{
             PreparedStatement buscar = Conexion.getInstancia().getConexion().prepareCall("{call sp_searchAreas(?)}");
             buscar.setInt(1, codigoArea);
             ResultSet registro = buscar.executeQuery();
             while(registro.next()){
                 resultado = new Area(registro.getInt("codigoArea"),registro.getString("nombreArea"));
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
        
        Area registro = new Area();
        registro.setNombreArea(txtNombreArea.getText());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_addAreas(?)}");
            procedimiento.setString(1, registro.getNombreArea());
            procedimiento.execute();
            listaArea.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            
            case NINGUNO:
                if (tblAreas.getSelectionModel().getSelectedItem()!=null){
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
                limpiarControles();
                cargarDatos();
            
            break;   
        }
    }
    
    public void actualizar(){
         try{
             
             PreparedStatement actualizar = Conexion.getInstancia().getConexion().prepareCall("{call sp_updateAreas(?,?)}");
             
             Area registro = (Area)tblAreas.getSelectionModel().getSelectedItem();
             registro.setNombreArea(txtNombreArea.getText()); 
            
             actualizar.setInt(1, registro.getCodigoArea());
             actualizar.setString(2, registro.getNombreArea());;
             
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
                if(tblAreas.getSelectionModel().getSelectedItem() == null){
                                 JOptionPane.showMessageDialog(null,"ERROR: SELECCIONE UN ELEMENTO");
                }
                if(tblAreas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿SEGURO QUE QUIERE ELIMINAR EL REGISTRO?","Eliminar Area",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement eliminar = Conexion.getInstancia().getConexion().prepareCall("{call sp_deleteAreas(?)}");
                            eliminar.setInt(1, ((Area)tblAreas.getSelectionModel().getSelectedItem()).getCodigoArea());
                            eliminar.execute();
                            listaArea.remove(tblAreas.getSelectionModel().getSelectedIndex());
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
        parametros.put("codigoArea", null);
        HacerReporte.cargarReporte("reportAreas.jasper", "Reporte Areas", parametros);
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
                    tblAreas.getSelectionModel().clearSelection();
                    break;
                    
            case NINGUNO:
                if(tblAreas.getSelectionModel().getSelectedItem()!=null){
                    imprimirReporte();
                    limpiarControles();
                }else{
                    imprimirReporte();
                }
        }
    }
    
    public void desactivarControles(){
        cmbCodigoArea.setEditable(false);
        txtNombreArea.setEditable(false);
    }
    
    public void activarControles(){
        cmbCodigoArea.setEditable(true);
        txtNombreArea.setEditable(true);
    }
    
    public void limpiarControles(){
        cmbCodigoArea.getSelectionModel().clearSelection();
        txtNombreArea.setText("");
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
