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
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.cristiantoma.bean.Horario;
import org.cristiantoma.db.Conexion;
import org.cristiantoma.sistema.Principal;

public class HorariosController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NINGUNO, NUEVO, EDITAR, ELIMINAR, ACTUALIZAR, CANCELAR, GUARDAR};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList <Horario> listaHorario;
     
    @FXML private TextField txtHorarioInicio;
    @FXML private TextField txtHorarioSalida;
    @FXML private CheckBox cbLunes;
    @FXML private CheckBox cbMartes;
    @FXML private CheckBox cbMiercoles;
    @FXML private CheckBox cbJueves;    
    @FXML private CheckBox cbViernes;
    @FXML private TableView tblHorarios;
    @FXML private TableColumn colCodigoHorario;
    @FXML private TableColumn colHorarioInicio;
    @FXML private TableColumn colHorarioSalida;
    @FXML private TableColumn colLunes;
    @FXML private TableColumn colMartes;
    @FXML private TableColumn colMiercoles;
    @FXML private TableColumn colJueves;
    @FXML private TableColumn colViernes;
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnReporte;
            
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       cargarDatos();
       desactivarControles();
    }
    public void cargarDatos(){
        tblHorarios.setItems(getHorario());
        colCodigoHorario.setCellValueFactory(new PropertyValueFactory<Horario, Integer>("codigoHorario"));
        colHorarioInicio.setCellValueFactory(new PropertyValueFactory<Horario, String>("horarioInicio"));
        colHorarioSalida.setCellValueFactory(new PropertyValueFactory<Horario, String>("horarioSalida"));
        colLunes.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("lunes"));
        colMartes.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("martes"));
        colMiercoles.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("miercoles"));
        colJueves.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("jueves"));
        colViernes.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("viernes"));
    }
     
    public ObservableList<Horario>getHorario(){
        ArrayList<Horario> lista = new ArrayList<>();
        ObservableList<Horario> listaHorario;
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
        Horario registro = new Horario();
        registro.setHorarioInicio(txtHorarioInicio.getText());
        registro.setHorarioInicio(txtHorarioSalida.getText());
        registro.setLunes(cbLunes.isSelected());
        registro.setMartes(cbMartes.isSelected());
        registro.setMiercoles(cbMiercoles.isSelected());
        registro.setJueves(cbJueves.isSelected());
        registro.setViernes(cbViernes.isSelected());
         try{
            PreparedStatement guardar = Conexion.getInstancia().getConexion().prepareCall("{call sp_addHorarios(?,?,?,?,?,?,?)}");
            guardar.setString(1, registro.getHorarioInicio());                                
            guardar.setString(2, registro.getHorarioInicio());
            guardar.setBoolean(3, registro.isLunes());
            guardar.setBoolean(4, registro.isMartes());
            guardar.setBoolean(5, registro.isMiercoles());
            guardar.setBoolean(6, registro.isJueves());
            guardar.setBoolean(7, registro.isViernes());
            guardar.execute();
           
            }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void limpiarControles(){
        txtHorarioSalida.setText("");
        txtHorarioInicio.setText("");
    }
    
    public void activarControles(){
        txtHorarioSalida.setEditable(true);
        txtHorarioInicio.setEditable(true);
    }
    
    public void desactivarControles(){
        txtHorarioInicio.setEditable(false);
        txtHorarioSalida.setEditable(false);
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
