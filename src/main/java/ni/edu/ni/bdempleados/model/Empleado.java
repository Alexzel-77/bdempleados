package ni.edu.ni.bdempleados.model;

import javafx.beans.property.*;

public class Empleado {
    private final IntegerProperty id_empleado;
    private final StringProperty codigo;
    private final StringProperty nombre;
    private final StringProperty correo;
    private final StringProperty cargo;
    private final DoubleProperty salario;

    public Empleado(int id_empleado, String codigo, String nombre, String correo, String cargo, double salario) {
        this.id_empleado = new SimpleIntegerProperty(id_empleado);
        this.codigo = new SimpleStringProperty(codigo);
        this.nombre = new SimpleStringProperty(nombre);
        this.correo = new SimpleStringProperty(correo);
        this.cargo = new SimpleStringProperty(cargo);
        this.salario = new SimpleDoubleProperty(salario);
    }

    public int getId_empleado() { return id_empleado.get(); }
    public String getCodigo() { return codigo.get(); }
    public String getNombre() { return nombre.get(); }
    public String getCorreo() { return correo.get(); }
    public String getCargo() { return cargo.get(); }
    public double getSalario() { return salario.get(); }
}