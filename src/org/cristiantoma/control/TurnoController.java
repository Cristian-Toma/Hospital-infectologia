package org.cristiantoma.control;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
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
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.cristiantoma.bean.MedicoEspecialidad;
import org.cristiantoma.bean.Paciente;
import org.cristiantoma.bean.ResponsableTurno;
import org.cristiantoma.bean.Turno;
import org.cristiantoma.db.Conexion;
import org.cristiantoma.sistema.Principal;

public class TurnoController implements Initializable {
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, NINGUNO, GUARDAR, ELIMINAR, EDITAR, CANCELAR, ACTUALIZAR};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList <Turno> listaTurno;
    private ObservableList <MedicoEspecialidad> listaMedicoEspecialidad;
    private ObservableList <ResponsableTurno> listaResponsableTurno;
    private ObservableList <Paciente> listaPaciente;
   
    private DatePicker dtpFechaTurno;
    private DatePicker dtpFechaCita;
    @FXML private GridPane grpFechaTurno;
    @FXML private GridPane grpFechaCita;
    @FXML private TextField txtValorCita;
    @FXML private ComboBox cmbCodigoMedicoEspecialidad;
    @FXML private ComboBox cmbCodigoResponsableTurno;
    @FXML private ComboBox cmbCodigoPaciente;
    @FXML private TableView tblTurnos;
    @FXML private TableColumn colCodigoTurno;
    @FXML private TableColumn colFechaTurno;
    @FXML private TableColumn colFechaCita;
    @FXML private TableColumn colValorCita;
    @FXML private TableColumn codMedicoEspecialidad;
    @FXML private TableColumn colCodResponsableTurno;
    @FXML private TableColumn colCodPaciente;
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       cargarDatos();
       desactivarControles();
       dtpFechaTurno = new DatePicker(Locale.ENGLISH);
       dtpFechaTurno.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
       dtpFechaTurno.getCalendarView().todayButtonTextProperty().set("Today");
       dtpFechaTurno.getCalendarView().setShowWeeks(false);

       grpFechaTurno.add(dtpFechaTurno,0,0);
       
       dtpFechaCita = new DatePicker(Locale.ENGLISH);
       dtpFechaCita.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
       dtpFechaCita.getCalendarView().todayButtonTextProperty().set("Today");
       dtpFechaCita.getCalendarView().setShowWeeks(false);

       grpFechaCita.add(dtpFechaCita,0,0);
       
       cmbCodigoMedicoEspecialidad.setItems(getMedicoEspecialidad());
       cmbCodigoResponsableTurno.setItems(getResponsableTurno());
       cmbCodigoPaciente.setItems(getPacientes());
    }
    
    public void cargarDatos(){
        tblTurnos.setItems(getTurno());
        colCodigoTurno.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoTurno"));
        colFechaTurno.setCellValueFactory(new PropertyValueFactory<Turno, Date>("fechaTurno"));
        colFechaCita.setCellValueFactory(new PropertyValueFactory<Turno, Date>("fechaCita"));
        colValorCita.setCellValueFactory(new PropertyValueFactory<Turno, Float>("valorCita"));
        codMedicoEspecialidad.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoMedicoEspecialidad"));
        colCodResponsableTurno.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoResponsableTurno"));
        colCodPaciente.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoPaciente"));
    }
    public ObservableList<Turno> getTurno(){
        ArrayList<Turno> lista = new ArrayList<Turno>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_selectTurno}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
               lista.add(new Turno (resultado.getInt("codigoTurno"),resultado.getDate("fechaTurno"),resultado.getDate("fechaCita"),resultado.getFloat("valorCita"),resultado.getInt("codigoMedicoEspecialidad"),resultado.getInt("codigoResponsableTurno"),resultado.getInt("codigoPaciente")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaTurno = FXCollections.observableList(lista);
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
                cargarDatos();
                
            break;    
        }
    }
     
    public void guardar(){
        Turno registro = new Turno();
        registro.setFechaTurno(dtpFechaTurno.getSelectedDate());
        registro.setFechaCita(dtpFechaCita.getSelectedDate());
        registro.setValorCita(Float.parseFloat(txtValorCita.getText()));
        registro.setCodigoMedicoEspecialidad(((MedicoEspecialidad) cmbCodigoMedicoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad());
        registro.setCodigoResponsableTurno(((ResponsableTurno) cmbCodigoResponsableTurno.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
        registro.setCodigoPaciente(((Paciente) cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_addTurno(?,?,?,?,?,?)}");
            procedimiento.setDate(1, new java.sql.Date (registro.getFechaTurno().getTime()));
            procedimiento.setDate(2, new java.sql.Date (registro.getFechaCita().getTime()));
            procedimiento.setFloat(3, registro.getValorCita());
            procedimiento.setInt(4, registro.getCodigoMedicoEspecialidad());
            procedimiento.setInt(5, registro.getCodigoResponsableTurno());
            procedimiento.setInt(6, registro.getCodigoPaciente());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            
            case NINGUNO:
                if (tblTurnos.getSelectionModel().getSelectedItem() != null){
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
                cargarDatos();
            break;    
        }
    }
    
     public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarTurno(?,?,?,?,?,?)}");
            Turno registro = (Turno)tblTurnos.getSelectionModel().getSelectedItem();
            registro.setFechaTurno(dtpFechaTurno.getSelectedDate());
            registro.setFechaCita(dtpFechaCita.getSelectedDate());
            procedimiento.setInt(1, registro.getCodigoTurno());
            procedimiento.setDate(2, new java.sql.Date (registro.getFechaTurno().getTime()));
            procedimiento.setDate(3, new java.sql.Date (registro.getFechaCita().getTime()));
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void desactivarControles(){
        dtpFechaTurno.setDisable(true);
        dtpFechaCita.setDisable(true);
        cmbCodigoMedicoEspecialidad.setDisable(true);
        cmbCodigoResponsableTurno.setDisable(true);
        cmbCodigoPaciente.setDisable(true);
    }
    
    public void activarControles(){
        dtpFechaTurno.setDisable(false);
        dtpFechaCita.setDisable(false);
        cmbCodigoMedicoEspecialidad.setDisable(false);
        cmbCodigoResponsableTurno.setDisable(false);
        cmbCodigoPaciente.setDisable(false);
    }
    
    public void limpiarControles(){
        dtpFechaTurno.setSelectedDate(null);
        dtpFechaCita.setSelectedDate(null);
        cmbCodigoMedicoEspecialidad.getSelectionModel().clearSelection();
        cmbCodigoResponsableTurno.getSelectionModel().clearSelection();
        cmbCodigoPaciente.getSelectionModel().clearSelection();
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
