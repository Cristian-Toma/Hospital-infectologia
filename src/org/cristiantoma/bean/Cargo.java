package org.cristiantoma.bean;

public class Cargo {
    private int codigoCargo;
    private String nombreCargo;

    public Cargo() {
    }

    public Cargo(int codigoCargo, String nombreCargo) {
        this.codigoCargo = codigoCargo;
        this.nombreCargo = nombreCargo;
    }

    public int getCodigoCargo() {
        return codigoCargo;
    }

    public void setCodigoCargo(int codgigoCargo) {
        this.codigoCargo = codgigoCargo;
    }

    public String getNombreCargo() {
        return nombreCargo;
    }

    public void setNombreCargo(String nombreCargo) {
        this.nombreCargo = nombreCargo;
    }

    @Override
    public String toString() {
        return  "" + codigoCargo;
    }
                    
}
