package org.cristiantoma.control;

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
import org.cristiantoma.bean.Paciente;
import org.cristiantoma.bean.ContactoUrgencia;
import org.cristiantoma.db.Conexion;
import org.cristiantoma.sistema.Principal;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.cristiantoma.sistema.Principal;import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.cristiantoma.report.HacerReporte;


public class ContactoUrgenciaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NINGUNO, NUEVO, CANCELAR, ACTUALIZAR, ELIMINAR, EDITAR, GUARDAR};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList <ContactoUrgencia> listaContactoUrgencia;
    private ObservableList<Paciente> listaPaciente;
    
    @FXML private ComboBox cmbCodigoContactoUrgencia;
    @FXML private TextField txtNumeroContacto;
    @FXML private TextField  txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TableView tblContactoUrgencia;
    @FXML private ComboBox cmbCodigoPaciente;
    @FXML private TableColumn colCodigoContactoUrgencia;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colNumeroContacto;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        desactivarControles();
        cargarDatos();
        cmbCodigoPaciente.setItems(getPacientes());
    }
    
    public void cargarDatos(){
        tblContactoUrgencia.setItems(getContactoUrgencia());
        colCodigoContactoUrgencia.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, Integer>("codigoContactoUrgencia"));
        colNombres.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, String>("apellidos"));
        colNumeroContacto.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, String>("numeroContacto"));
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, Integer>("codigoPaciente"));

    }
    
    public void seleccionarDatos(){
        txtNumeroContacto.setText(((ContactoUrgencia)tblContactoUrgencia.getSelectionModel().getSelectedItem()).getNumeroContacto());
        txtNombres.setText(((ContactoUrgencia)tblContactoUrgencia.getSelectionModel().getSelectedItem()).getNombres());
        txtApellidos.setText(((ContactoUrgencia)tblContactoUrgencia.getSelectionModel().getSelectedItem()).getApellidos());
    }
    
    public ObservableList<ContactoUrgencia> getContactoUrgencia(){
        ArrayList<ContactoUrgencia> lista = new ArrayList<ContactoUrgencia>();
        try{
            PreparedStatement mostrar = Conexion.getInstancia().getConexion().prepareCall("{call sp_selectContactoUrgencia()}");
            ResultSet resultado = mostrar.executeQuery();
            while(resultado.next()){
                lista.add(new ContactoUrgencia(resultado.getInt("codigoContactoUrgencia"),resultado.getString("nombres"),resultado.getString("apellidos"),resultado.getString("numeroContacto"),resultado.getInt("codigoPaciente")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaContactoUrgencia = FXCollections.observableList(lista);
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
                    tipoDeOperacion=operaciones.NINGUNO;
                    cargarDatos();
                
             break;      
        }
    }
    
    public void agregar(){
        ContactoUrgencia registro = new ContactoUrgencia();
        registro.setNumeroContacto(txtNumeroContacto.getText());
        registro.setNombres(txtNombres.getText());
        registro.setApellidos(txtApellidos.getText());
        registro.setCodigoPaciente(((Paciente) cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
        try{
            PreparedStatement guardar = Conexion.getInstancia().getConexion().prepareCall("{call sp_addContactoUrgencia(?,?,?,?)}");
            guardar.setString(1, registro.getNumeroContacto());                                
            guardar.setString(2, registro.getNombres());
            guardar.setString(3, registro.getApellidos());
            guardar.setInt(4, registro.getCodigoPaciente());
            guardar.execute();
            listaContactoUrgencia.add(registro);
            }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblContactoUrgencia.getSelectionModel().getSelectedItem() != null){
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
            PreparedStatement actualizar = Conexion.getInstancia().getConexion().prepareCall("{call sp_updateContactoUrgencia(?,?,?,?)}");
            
            ContactoUrgencia registro = (ContactoUrgencia)tblContactoUrgencia.getSelectionModel().getSelectedItem();
            registro.setNumeroContacto(txtNumeroContacto.getText());
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            //registro.setCodigoPaciente(((Paciente) cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
            
            actualizar.setInt(1, registro.getCodigoContactoUrgencia());
            actualizar.setString(2, registro.getNumeroContacto());
            actualizar.setString(3, registro.getNombres());
            actualizar.setString(4, registro.getApellidos());
            //actualizar.setInt(5, registro.getCodigoPaciente());

            
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
                if(tblContactoUrgencia.getSelectionModel().getSelectedItem() == null){
                    JOptionPane.showMessageDialog(null,"DEBE SELECCIONAR UN ELEMENTO");
                }
                if(tblContactoUrgencia.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿DESEA ELIMINAR ESTE ELEMENTO?","ELIMINAR TELEFONO MEDICO",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                        if(respuesta == JOptionPane.YES_OPTION){
                            try{
                            PreparedStatement eliminar = Conexion.getInstancia().getConexion().prepareCall("{call sp_deleteContactoUrgencia(?)}");
                            eliminar.setInt(1, ((ContactoUrgencia)tblContactoUrgencia.getSelectionModel().getSelectedItem()).getCodigoContactoUrgencia());
                            eliminar.execute();
                            listaContactoUrgencia.remove(tblContactoUrgencia.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                    }
                }
        }
    }
    
    public void imprimirReporte(){
        Map parametro = new HashMap();
        parametro.put("codigoContactoUrgencia",null);
        HacerReporte.cargarReporte("reportContactoUrgencia.jasper","Reporte Conracto Urgencia", parametro);
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
                tblContactoUrgencia.getSelectionModel().clearSelection();
            break;
            
            case NINGUNO:
                if(tblContactoUrgencia.getSelectionModel().getSelectedItem() != null){
                    imprimirReporte();
                    limpiarControles();
                }else{
                    imprimirReporte();
                }
        }
        
    }
    public void limpiarControles(){
        txtNumeroContacto.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        cmbCodigoPaciente.getSelectionModel().clearSelection();
    }
    
    public void activarControles(){
        txtNumeroContacto.setEditable(true);
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
    }
    
    public void desactivarControles(){
        txtNumeroContacto.setEditable(false);
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        cmbCodigoPaciente.setEditable(false);
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
