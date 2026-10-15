package ni.edu.ni.bdempleados.controller;

import ni.edu.ni.bdempleados.database.ConexionBD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.ni.bdempleados.model.Empleado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoController {

    @FXML private TextField txtCodigo, txtNombre, txtCorreo, txtCargo, txtSalario;
    @FXML private ComboBox<String> cmbFiltro1, cmbFiltro2, cmbFiltro3;
    @FXML private TextField txtBuscar1, txtBuscar2, txtBuscar3;

    @FXML private TableView<Empleado> tablaEmpleados;
    @FXML private TableColumn<Empleado, String> colCodigo, colNombre, colCorreo, colCargo;
    @FXML private TableColumn<Empleado, Double> colSalario;

    private ObservableList<Empleado> empleadosList = FXCollections.observableArrayList();
    private Empleado empleadoSeleccionado;

    @FXML
    public void initialize() {

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        colSalario.setCellValueFactory(new PropertyValueFactory<>("salario"));

        String[] columnas = {"codigo", "nombre", "correo", "cargo", "salario"};
        cmbFiltro1.setItems(FXCollections.observableArrayList(columnas));
        cmbFiltro2.setItems(FXCollections.observableArrayList(columnas));
        cmbFiltro3.setItems(FXCollections.observableArrayList(columnas));

        cargarEmpleados();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void guardar() {
        String sql = "INSERT INTO empleado (codigo, nombre, correo, cargo, salario) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, txtCodigo.getText());
            pstmt.setString(2, txtNombre.getText());
            pstmt.setString(3, txtCorreo.getText());
            pstmt.setString(4, txtCargo.getText());
            pstmt.setDouble(5, Double.parseDouble(txtSalario.getText()));

            pstmt.executeUpdate();
            mostrarAlerta("Éxito", "Empleado registrado correctamente.");
            limpiarFormulario();
            cargarEmpleados();
        } catch (SQLException | NumberFormatException e) {
            mostrarAlerta("Error", "No se pudo registrar: " + e.getMessage());
        }
    }

    @FXML
    private void actualizar() {
        if (empleadoSeleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un empleado de la tabla.");
            return;
        }

        String sql = "UPDATE empleado SET codigo=?, nombre=?, correo=?, cargo=?, salario=? WHERE id_empleado=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, txtCodigo.getText());
            pstmt.setString(2, txtNombre.getText());
            pstmt.setString(3, txtCorreo.getText());
            pstmt.setString(4, txtCargo.getText());
            pstmt.setDouble(5, Double.parseDouble(txtSalario.getText()));
            pstmt.setInt(6, empleadoSeleccionado.getId_empleado());

            pstmt.executeUpdate();
            mostrarAlerta("Éxito", "Empleado actualizado correctamente.");
            limpiarFormulario();
            cargarEmpleados();
        } catch (SQLException | NumberFormatException e) {
            mostrarAlerta("Error", "No se pudo actualizar: " + e.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        if (empleadoSeleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un empleado para dar de baja.");
            return;
        }


        String sql = "UPDATE empleado SET activo=FALSE WHERE id_empleado=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, empleadoSeleccionado.getId_empleado());
            pstmt.executeUpdate();

            mostrarAlerta("Éxito", "Empleado dado de baja lógicamente.");
            limpiarFormulario();
            cargarEmpleados();
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo dar de baja: " + e.getMessage());
        }
    }

    @FXML
    private void buscar() {
        StringBuilder sql = new StringBuilder("SELECT * FROM empleado WHERE activo = TRUE");
        List<String> values = new ArrayList<>();

        agregarFiltroQuery(sql, values, cmbFiltro1.getValue(), txtBuscar1.getText());
        agregarFiltroQuery(sql, values, cmbFiltro2.getValue(), txtBuscar2.getText());
        agregarFiltroQuery(sql, values, cmbFiltro3.getValue(), txtBuscar3.getText());

        ejecutarConsulta(sql.toString(), values);
    }

    private void agregarFiltroQuery(StringBuilder sql, List<String> values, String columna, String valor) {
        if (columna != null && valor != null && !valor.trim().isEmpty()) {
            sql.append(" AND ").append(columna).append("::text ILIKE ?");
            values.add("%" + valor.trim() + "%");
        }
    }

    private void ejecutarConsulta(String sql, List<String> values) {
        empleadosList.clear();
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.size(); i++) {
                pstmt.setString(i + 1, values.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                empleadosList.add(new Empleado(
                        rs.getInt("id_empleado"),
                        rs.getString("codigo"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("cargo"),
                        rs.getDouble("salario")
                ));
            }
            tablaEmpleados.setItems(empleadosList);
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al consultar la base de datos: " + e.getMessage());
        }
    }

    private void cargarEmpleados() {
        ejecutarConsulta("SELECT * FROM empleado WHERE activo = TRUE", new ArrayList<>());
    }

    @FXML
    private void seleccionarRegistro() {
        empleadoSeleccionado = tablaEmpleados.getSelectionModel().getSelectedItem();
        if (empleadoSeleccionado != null) {
            txtCodigo.setText(empleadoSeleccionado.getCodigo());
            txtNombre.setText(empleadoSeleccionado.getNombre());
            txtCorreo.setText(empleadoSeleccionado.getCorreo());
            txtCargo.setText(empleadoSeleccionado.getCargo());
            txtSalario.setText(String.valueOf(empleadoSeleccionado.getSalario()));
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtCodigo.clear();
        txtNombre.clear();
        txtCorreo.clear();
        txtCargo.clear();
        txtSalario.clear();
        empleadoSeleccionado = null;
    }

    @FXML
    private void quitarFiltros() {
        cmbFiltro1.setValue(null); txtBuscar1.clear();
        cmbFiltro2.setValue(null); txtBuscar2.clear();
        cmbFiltro3.setValue(null); txtBuscar3.clear();
        cargarEmpleados();
    }
}