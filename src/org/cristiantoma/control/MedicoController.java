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
import org.cristiantoma.bean.Medico;
import org.cristiantoma.db.Conexion;
import org.cristiantoma.report.HacerReporte;
import org.cristiantoma.sistema.Principal;

public class MedicoController implements Initializable{
    
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO; 
    private ObservableList<Medico> listaMedico;
    
    @FXML private ComboBox cmbCodigoMedico;
    @FXML private TextField txtLicencia;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtSexo;
    @FXML private TextField txtHoraEntrada;
    @FXML private TextField txtHoraSalida;
    @FXML private TextField txtTurnoMaximo;
    @FXML private TableView tblMedicos;
    @FXML private TableColumn colCodigoMedico;
    @FXML private TableColumn colLicenciaMedica;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colHoraEntrada;
    @FXML private TableColumn colHoraSalida;
    @FXML private TableColumn colSexo;
    @FXML private TableColumn colTurnoMaximo;
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnReporte;
     
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    
        desactivarControles();
        cargarDatos();
        
    } 
       
   public void seleccionarElemento(){
       if(!(btnNuevo.getText().equals("Nuevo") || btnNuevo.getText().equals("Guardar"))){
        txtLicencia.setText(String.valueOf(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getLicenciaMedica()));
        txtNombres.setText(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getNombres());
        txtApellidos.setText(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getApellidos());
        txtSexo.setText(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getSexo());
        txtHoraEntrada.setText(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getHoraEntrada());
        txtHoraSalida.setText(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getHoraSalida());
       }
    }
    
    public void cargarDatos(){
        
        tblMedicos.setItems(getMedicos());
        colCodigoMedico.setCellValueFactory(new PropertyValueFactory<Medico, Integer>("codigoMedico"));
        colLicenciaMedica.setCellValueFactory(new PropertyValueFactory<Medico, Integer>("licenciaMedica"));
        colNombres.setCellValueFactory(new PropertyValueFactory<Medico, String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Medico, String>("apellidos"));
        colHoraEntrada.setCellValueFactory(new PropertyValueFactory<Medico, String>("horaEntrada"));
        colHoraSalida.setCellValueFactory(new PropertyValueFactory<Medico, String>("horaSalida"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Medico, String>("Sexo"));
        colTurnoMaximo.setCellValueFactory(new PropertyValueFactory<Medico, String>("turnoMaximo"));
    
    }
    
    public ObservableList<Medico> getMedicos(){
    
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
        
        return listaMedico = FXCollections.observableList(lista);
    
    }
    
     public Medico buscarMedico(int codigoMedico){
         Medico resultado = null;
         try{
             PreparedStatement buscar = Conexion.getInstancia().getConexion().prepareCall("{call sp_searchMedicos(?)}");
             buscar.setInt(1, codigoMedico);
             ResultSet registro = buscar.executeQuery();
             while(registro.next()){
                 resultado = new Medico(registro.getInt("codigoMedico"),registro.getInt("licenciaMedica"),registro.getString("nombres"),registro.getString("Apellidos"),registro.getString("sexo"),registro.getString("horaEntrada"),registro.getString("horaSalida"),registro.getInt("turnoMaximo"));
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
        
        Medico registro = new Medico();
        registro.setLicenciaMedica(Integer.parseInt(txtLicencia.getText()));
        registro.setNombres(txtNombres.getText());
        registro.setApellidos(txtApellidos.getText());
        registro.setSexo(txtSexo.getText());
        registro.setHoraEntrada(txtHoraEntrada.getText());
        registro.setHoraSalida(txtHoraSalida.getText());
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_addMedicos(?,?,?,?,?,?)}");
            procedimiento.setInt(1, registro.getLicenciaMedica());
            procedimiento.setString(2, registro.getNombres());
            procedimiento.setString(3, registro.getApellidos());
            procedimiento.setString(4, registro.getHoraEntrada());
            procedimiento.setString(5, registro.getHoraSalida());
            procedimiento.setString(6, registro.getSexo());
            procedimiento.execute();
            listaMedico.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            
            case NINGUNO:
                if (tblMedicos.getSelectionModel().getSelectedItem()!=null){
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
                btnNuevo.setText("Nuevoo");
                btnEliminar.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                limpiarControles();
                cargarDatos();
            
            break;   
        }
    }
    
    public void actualizar(){
         try{
             
             PreparedStatement actualizar = Conexion.getInstancia().getConexion().prepareCall("{call sp_updateMedicos(?,?,?,?,?,?,?)}");
             
             Medico registro = (Medico)tblMedicos.getSelectionModel().getSelectedItem();
             
             registro.setLicenciaMedica(Integer.parseInt(txtLicencia.getText()));
             registro.setNombres(txtNombres.getText());
             registro.setApellidos(txtApellidos.getText());
             registro.setSexo(txtSexo.getText());
             registro.setHoraEntrada(txtHoraEntrada.getText());
             registro.setHoraSalida(txtHoraSalida.getText());
             
             actualizar.setInt(1, registro.getCodigoMedico());
             actualizar.setInt(2, registro.getLicenciaMedica());
             actualizar.setString(3, registro.getNombres());
             actualizar.setString(4, registro.getApellidos());
             actualizar.setString(5, registro.getSexo());
             actualizar.setString(6, registro.getHoraEntrada());
             actualizar.setString(7, registro.getHoraSalida());
             
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
                tipoDeOperacion = operaciones.NINGUNO;
            break; 
            default:
                if(tblMedicos.getSelectionModel().getSelectedItem() == null){
                                 JOptionPane.showMessageDialog(null,"ERROR: SELECCIONE UN ELEMENTO");
                }
                if(tblMedicos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿SEGURO QUE QUIERE ELIMINAR EL REGISTRO?","Eliminar Medico",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement eliminar = Conexion.getInstancia().getConexion().prepareCall("{call sp_deleteMedicos(?)}");
                            eliminar.setInt(1, ((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getCodigoMedico());
                            eliminar.execute();
                            listaMedico.remove(tblMedicos.getSelectionModel().getSelectedIndex());
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
        parametros.put("codigoMedico", null);
        HacerReporte.cargarReporte("reportMedico.jasper", "Reporte Médicos", parametros);
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
                    tblMedicos.getSelectionModel().clearSelection();
                    break;
                    
            case NINGUNO:
                if(tblMedicos.getSelectionModel().getSelectedItem()!=null){
                    imprimirReporte();
                    limpiarControles();
                }else{
                    imprimirReporte();
                }
        }
    }
    
    public void desactivarControles(){
        
        txtLicencia.setEditable(false);
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtSexo.setEditable(false);
        txtHoraEntrada.setEditable(false);
        txtHoraSalida.setEditable(false);
        txtTurnoMaximo.setEditable(false);
        
    }
    
    public void activarControles(){
        
        txtLicencia.setEditable(true);
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtSexo.setEditable(true);
        txtHoraEntrada.setEditable(true);
        txtHoraSalida.setEditable(true);
        txtTurnoMaximo.setEditable(false); 
    
    }
    
    public void limpiarControles(){
    
        txtLicencia.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtSexo.setText("");
        txtHoraEntrada.setText("");
        txtHoraSalida.setText("");
        txtTurnoMaximo.setText("");
    
    }
    
    public Principal getEscenarioPrincipal(){
    
        return escenarioPrincipal;
    
    }
    
    public void setEscenarioPrincipal (Principal escenarioPrincipal){
    
        this.escenarioPrincipal = escenarioPrincipal;
    
    }
    
    public void menuPrincipal(){
    
        escenarioPrincipal.menuPrincipal();
    
    }
}