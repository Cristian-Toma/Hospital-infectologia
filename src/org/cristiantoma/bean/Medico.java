package org.cristiantoma.bean;

public class Medico{ 
    
    private int codigoMedico;
    private int licenciaMedica;
    private String nombres;
    private String apellidos;
    private String sexo;
    private String horaEntrada;
    private String horaSalida;
    private int turnoMaximo;

    public Medico() {
    }

    public Medico(int codigoMedico, int licenciaMedica, String nombres, String apellidos, String sexo, String horaEntrada, String horaSalida, int turnoMaximo) {
        this.codigoMedico = codigoMedico;
        this.licenciaMedica = licenciaMedica;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.sexo = sexo;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.turnoMaximo = turnoMaximo;
    }

    public int getCodigoMedico() {
        return codigoMedico;
    }

    public void setCodigoMedico(int codigoMedico) {
        this.codigoMedico = codigoMedico;
    }

    public int getLicenciaMedica() {
        return licenciaMedica;
    }

    public void setLicenciaMedica(int licenciaMedica) {
        this.licenciaMedica = licenciaMedica;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(String horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public int getTurnoMaximo() {
        return turnoMaximo;
    }

    public void setTurnoMaximo(int turnoMaximo) {
        this.turnoMaximo = turnoMaximo;
    }

    @Override
    public String toString() {
        return "" + codigoMedico;
    }
            
    
}
