
package org.cristiantoma.control;

import java.net.URL;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.cristiantoma.bean.Paciente;
import org.cristiantoma.db.Conexion;
import org.cristiantoma.sistema.Principal;
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.cristiantoma.report.HacerReporte;

public class PacientesController implements Initializable {
    private enum operaciones{NUEVO, ACTUALIZAR, ELIMINAR, EDITAR, GUARDAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Paciente> listaPaciente = FXCollections.observableArrayList();
    
    @FXML private TextField txtDPI; 
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtSexo;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtOcupacion;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private TableColumn colDPI;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colFechaNacimiento;
    @FXML private TableColumn colEdad;
    @FXML private TableColumn colDireccion;
    @FXML private TableColumn colOcupacion;
    @FXML private TableColumn colSexo;
    @FXML private TableView tblPacientes;
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnReporte;
    @FXML private GridPane grpFechaNacimiento;
    private DatePicker dtpFechaNacimiento;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        desactivarControles();
        mostrarDatos();
        
        dtpFechaNacimiento = new DatePicker(Locale.ENGLISH);
        dtpFechaNacimiento.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        dtpFechaNacimiento.getCalendarView().todayButtonTextProperty().set("Today");
        dtpFechaNacimiento.getCalendarView().setShowWeeks(false);
        
        grpFechaNacimiento.add(dtpFechaNacimiento,0,0);
        
    }
    public void seleccionarDatos(){
  
        txtDPI.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getDPI());
        txtNombres.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getNombres());
        txtApellidos.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getApellidos());
        txtSexo.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getSexo());
  
    }
   
    public void mostrarDatos(){
        
        tblPacientes.setItems(getPacientes());
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<Paciente, Integer>("codigoPaciente"));
        colDPI.setCellValueFactory(new PropertyValueFactory<Paciente, String>("DPI"));
        colNombres.setCellValueFactory(new PropertyValueFactory<Paciente, String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Paciente, String>("apellidos"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<Paciente, Date>("fechaNacimiento"));
        colEdad.setCellValueFactory(new PropertyValueFactory<Paciente, Integer>("edad"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<Paciente, String>("direccion"));
        colOcupacion.setCellValueFactory(new PropertyValueFactory<Paciente, String>("ocupacion"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Paciente, String>("sexo"));
    }
    
   public ObservableList<Paciente> getPacientes(){
        ArrayList<Paciente> lista = new ArrayList<>();
        
        try{
            PreparedStatement seleccionar = Conexion.getInstancia().getConexion().prepareCall("{call sp_selectPacientes()}");
            ResultSet resultado = seleccionar.executeQuery();
            while(resultado.next()){
                lista.add(new Paciente(resultado.getInt("codigoPaciente"),resultado.getString("DPI"),resultado.getString("nombres"),resultado.getString("apellidos"),resultado.getDate("fechaNacimiento"),resultado.getInt("edad"),resultado.getString("direccion"),resultado.getString("ocupacion"),resultado.getString("sexo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaPaciente = FXCollections.observableArrayList(lista);
    }
   
    public Paciente buscarPaciente(int codigoPaciente){
        Paciente resultado = null;
        try{
            PreparedStatement buscar = Conexion.getInstancia().getConexion().prepareCall("{call sp_searchPaciente(?)}");
            buscar.setInt(1, codigoPaciente);
            ResultSet registro = buscar.executeQuery();
            while(registro.next()){
                resultado = new Paciente(registro.getInt("codigoPaciente"),registro.getString("DPI"),registro.getString("nombres"),registro.getString("apellidos"),registro.getDate("fechaNacimiento"),registro.getInt("edad"),registro.getString("direccion"),registro.getString("ocupacion"),registro.getString("sexo"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
   
    public void nuevo(){
        
        switch(tipoDeOperacion){
        
            case NINGUNO:
                
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnReporte.setDisable(true);
                btnEditar.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                
            break;
            
            case GUARDAR:
                
                guardar();
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                mostrarDatos();
                
            break;    
        }
    }
    
    public void guardar(){
        Paciente registro = new Paciente();
        registro.setDPI(txtDPI.getText());
        registro.setNombres(txtNombres.getText());
        registro.setApellidos(txtApellidos.getText());
        registro.setDireccion(txtDireccion.getText());
        registro.setOcupacion(txtOcupacion.getText());
        registro.setSexo(txtSexo.getText());
        registro.setFechaNacimiento(dtpFechaNacimiento.getSelectedDate());

        try{
            PreparedStatement guardar = Conexion.getInstancia().getConexion().prepareCall("{call sp_addPacientes(?,?,?,?,?,?,?)}");
            guardar.setString(1, registro.getDPI());
            guardar.setString(2, registro.getNombres());
            guardar.setString(3, registro.getApellidos());
             guardar.setDate(4, new java.sql.Date ( registro.getFechaNacimiento().getTime()));
             guardar.setString(5, registro.getDireccion());
            guardar.setString(6, registro.getOcupacion());
            
            guardar.setString(7, registro.getSexo());
           
            guardar.execute();
            listaPaciente.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            
            case NINGUNO:
                if (tblPacientes.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnEliminar.setText("Cancelar");
                    btnReporte.setDisable(true);
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
                mostrarDatos();
            break;    
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement actualizar = Conexion.getInstancia().getConexion().prepareCall("{call sp_updatePacientes(?,?,?,?,?,?,?)}");
            
            Paciente registro = (Paciente)tblPacientes.getSelectionModel().getSelectedItem();
            registro.setDPI(txtDPI.getText());
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setOcupacion(txtOcupacion.getText());
            registro.setDireccion(txtDireccion.getText());
            registro.setSexo(txtSexo.getText());
            
            actualizar.setInt(1, registro.getCodigoPaciente());
            actualizar.setString(2, registro.getDPI());
            actualizar.setString(3, registro.getNombres());
            actualizar.setString(4, registro.getApellidos());
            actualizar.setString(5, registro.getOcupacion());
            actualizar.setString(6, registro.getDireccion());
            actualizar.setString(7, registro.getSexo());
            
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
                    if(tblPacientes.getSelectionModel().getSelectedItem() == null){
                        JOptionPane.showMessageDialog(null,"ERROR: SELECCIONE UN ELEMENTO");
                    }
                    if(tblPacientes.getSelectionModel().getSelectedItem() != null){
                        int respuesta = JOptionPane.showConfirmDialog(null,"¿SEGURO QUE DESEA ELIMINAR ESTE REGISTRO?","ELIMINAR PACIENTES",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                        if(respuesta == JOptionPane.YES_OPTION){
                            try{
                                PreparedStatement eliminar = Conexion.getInstancia().getConexion().prepareCall("{call sp_deletePacientes(?)}");
                                eliminar.setInt(1, ((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getCodigoPaciente());
                                eliminar.execute();
                                listaPaciente.remove(tblPacientes.getSelectionModel().getSelectedIndex());
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
        parametros.put("codigoPaciente", null);
        HacerReporte.cargarReporte("reportPaciente.jasper", "Reporte Pacientes", parametros);
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
                    tipoDeOperacion = PacientesController.operaciones.NINGUNO;
                    tblPacientes.getSelectionModel().clearSelection();
                    break;
                    
            case NINGUNO:
                if(tblPacientes.getSelectionModel().getSelectedItem()!=null){
                    imprimirReporte();
                    limpiarControles();
                }else{
                    imprimirReporte();
                }
        }
    }
    
    public void desactivarControles(){
        txtDPI.setEditable(false);
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtOcupacion.setEditable(false);
        txtDireccion.setEditable(false);
        txtSexo.setEditable(false);
    }
    
    public void activarControles(){
        txtDPI.setEditable(true);
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtOcupacion.setEditable(true);
        txtDireccion.setEditable(true);
        txtSexo.setEditable(true);
    }
    
    public void limpiarControles(){
        txtDPI.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtOcupacion.setText("");
        txtDireccion.setText("");
        txtSexo.setText("");
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