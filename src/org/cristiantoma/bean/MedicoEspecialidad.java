package org.cristiantoma.bean;

public class MedicoEspecialidad {
    private int codigoMedicoEspecialidad;
    private int codigoEspecialidad;
    private int codigoHorario;

    public MedicoEspecialidad() {
    
    }

    public MedicoEspecialidad(int codigoMedicoEspecialidad, int codigoEspecialidad, int codigoHorario) {
        this.codigoMedicoEspecialidad = codigoMedicoEspecialidad;
        this.codigoEspecialidad = codigoEspecialidad;
        this.codigoHorario = codigoHorario;
    }

    public int getCodigoMedicoEspecialidad() {
        return codigoMedicoEspecialidad;
    }

    public void setCodigoMedicoEspecialidad(int codigoMedicoEspecialidad) {
        this.codigoMedicoEspecialidad = codigoMedicoEspecialidad;
    }

    public int getCodigoEspecialidad() {
        return codigoEspecialidad;
    }

    public void setCodigoEspecialidad(int codigoEspecialidad) {
        this.codigoEspecialidad = codigoEspecialidad;
    }

    public int getCodigoHorario() {
        return codigoHorario;
    }

    public void setCodigoHorario(int codigoHorario) {
        this.codigoHorario = codigoHorario;
    }

    @Override
    public String toString() {
        return "" + codigoMedicoEspecialidad;
    }
    
}
