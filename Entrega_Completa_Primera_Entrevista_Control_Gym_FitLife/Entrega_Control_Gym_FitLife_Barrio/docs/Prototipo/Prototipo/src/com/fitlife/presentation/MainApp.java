package com.fitlife.presentation;

import com.fitlife.data.repository.GimnasioRepositoryImpl;
import com.fitlife.domain.model.Cliente;
import com.fitlife.domain.model.MovimientoCaja;
import com.fitlife.domain.repository.IGimnasioRepository;
import com.fitlife.usecase.GestionGimnasioUseCase;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.time.LocalDate;

public class MainApp extends Application {

    private GestionGimnasioUseCase useCase;

    @Override
    public void init() {
        // Inyección de dependencias respetando Clean Architecture
        IGimnasioRepository repository = new GimnasioRepositoryImpl();
        this.useCase = new GestionGimnasioUseCase(repository);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("FitLife Barrio - Prototipo Clean Architecture");

        TabPane tabPane = new TabPane();

        Tab tabSemaforo = new Tab("RP-01: Semáforo y Consulta", crearVistaSemaforo());
        Tab tabPlanes = new Tab("RP-02: Planes y Parejas", crearVistaPlanes());
        Tab tabRegistro = new Tab("RP-03: Registro y Renovación", crearVistaRegistro());
        Tab tabCaja = new Tab("RP-04: Caja y Cierre", crearVistaCaja());

        tabSemaforo.setClosable(false);
        tabPlanes.setClosable(false);
        tabRegistro.setClosable(false);
        tabCaja.setClosable(false);

        tabPane.getTabs().addAll(tabSemaforo, tabPlanes, tabRegistro, tabCaja);

        Scene scene = new Scene(tabPane, 850, 550);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // RP-01: Vista de Semáforo y Consulta
    private VBox crearVistaSemaforo() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        Label lbl = new Label("RP-01: Semáforo de Entrada y Consulta de Membresía");
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        HBox hb = new HBox(10);
        TextField txtFiltro = new TextField();
        txtFiltro.setPromptText("Buscar por cédula, nombre o celular...");
        txtFiltro.setPrefWidth(280);
        Button btnBuscar = new Button("Consultar");
        hb.getChildren().addAll(txtFiltro, btnBuscar);

        TableView<Cliente> tabla = new TableView<>();
        tabla.setItems(FXCollections.observableArrayList(useCase.obtenerTodosLosClientes()));

        TableColumn<Cliente, String> c1 = new TableColumn<>("Cédula");
        c1.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        TableColumn<Cliente, String> c2 = new TableColumn<>("Nombre");
        c2.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        TableColumn<Cliente, String> c3 = new TableColumn<>("Plan");
        c3.setCellValueFactory(new PropertyValueFactory<>("plan"));
        TableColumn<Cliente, LocalDate> c4 = new TableColumn<>("Vencimiento");
        c4.setCellValueFactory(new PropertyValueFactory<>("fechaVencimiento"));
        TableColumn<Cliente, String> c5 = new TableColumn<>("Semáforo Estado");
        c5.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Lógica visual del Semáforo
        c5.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    Circle indicator = new Circle(6);
                    if (item.equals("Vigente")) indicator.setFill(Color.GREEN);
                    else if (item.equals("Por vencer")) indicator.setFill(Color.ORANGE);
                    else indicator.setFill(Color.RED);
                    setGraphic(indicator);
                    setGraphicTextGap(10);
                }
            }
        });

        tabla.getColumns().addAll(c1, c2, c3, c4, c5);
        tabla.setPrefHeight(280);

        btnBuscar.setOnAction(e -> tabla.setItems(FXCollections.observableArrayList(useCase.buscarClientes(txtFiltro.getText()))));

        vbox.getChildren().addAll(lbl, hb, tabla);
        return vbox;
    }

    // RP-02: Vista de Planes
    private VBox crearVistaPlanes() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        Label lbl = new Label("RP-02: Administración de Planes (Día, Quincena, Mensualidad, Pareja)");
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        ComboBox<String> cbCliente = new ComboBox<>();
        useCase.obtenerTodosLosClientes().forEach(c -> cbCliente.getItems().add(c.getCedula() + " - " + c.getNombre()));

        ComboBox<String> cbPlan = new ComboBox<>();
        cbPlan.getItems().addAll("Día", "Quincena", "Mensualidad", "Pase de Pareja");

        grid.add(new Label("Seleccionar Cliente:"), 0, 0); grid.add(cbCliente, 1, 0);
        grid.add(new Label("Nuevo Plan:"), 0, 1); grid.add(cbPlan, 1, 1);

        Button btnAsignar = new Button("Actualizar Plan");
        Label lblMsg = new Label();

        btnAsignar.setOnAction(e -> {
            if (cbCliente.getValue() != null && cbPlan.getValue() != null) {
                String cedula = cbCliente.getValue().split(" - ")[0];
                useCase.asignarPlan(cedula, cbPlan.getValue());
                lblMsg.setText("¡Plan actualizado correctamente!");
                lblMsg.setTextFill(Color.GREEN);
            }
        });

        vbox.getChildren().addAll(lbl, grid, btnAsignar, lblMsg);
        return vbox;
    }

    // RP-03: Vista de Registro y Renovación
    private VBox crearVistaRegistro() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        Label lbl = new Label("RP-03: Registro Rápido y Renovación de Clientes");
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        TextField txtCedula = new TextField();
        TextField txtNombre = new TextField();
        TextField txtCelular = new TextField();
        ComboBox<String> cbPlan = new ComboBox<>();
        cbPlan.getItems().addAll("Día", "Quincena", "Mensualidad", "Pase de Pareja");

        grid.add(new Label("Cédula:"), 0, 0); grid.add(txtCedula, 1, 0);
        grid.add(new Label("Nombre Completo:"), 0, 1); grid.add(txtNombre, 1, 1);
        grid.add(new Label("Celular:"), 0, 2); grid.add(txtCelular, 1, 2);
        grid.add(new Label("Plan Inicial:"), 0, 3); grid.add(cbPlan, 1, 3);

        Button btnReg = new Button("Registrar Cliente");
        Label lblRes = new Label();

        btnReg.setOnAction(e -> {
            if (!txtCedula.getText().isEmpty() && !txtNombre.getText().isEmpty() && cbPlan.getValue() != null) {
                useCase.registrarClienteNuevo(txtCedula.getText(), txtNombre.getText(), txtCelular.getText(), cbPlan.getValue());
                lblRes.setText("¡Cliente registrado con éxito e ingreso sumado a caja!");
                lblRes.setTextFill(Color.GREEN);
                txtCedula.clear(); txtNombre.clear(); txtCelular.clear();
            } else {
                lblRes.setText("Complete los campos obligatorios.");
                lblRes.setTextFill(Color.RED);
            }
        });

        vbox.getChildren().addAll(lbl, grid, btnReg, lblRes);
        return vbox;
    }

    // RP-04: Vista de Caja y Cierre Diario
    private VBox crearVistaCaja() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        Label lbl = new Label("RP-04: Control de Ingresos, Gastos y Cierre de Caja");
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TableView<MovimientoCaja> tabla = new TableView<>();
        tabla.setItems(FXCollections.observableArrayList(useCase.obtenerMovimientos()));

        TableColumn<MovimientoCaja, String> t1 = new TableColumn<>("Tipo");
        t1.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        TableColumn<MovimientoCaja, String> t2 = new TableColumn<>("Descripción");
        t2.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        TableColumn<MovimientoCaja, Double> t3 = new TableColumn<>("Valor ($)");
        t3.setCellValueFactory(new PropertyValueFactory<>("valor"));

        tabla.getColumns().addAll(t1, t2, t3);
        tabla.setPrefHeight(220);

        HBox hbCierre = new HBox(15);
        Button btnCierre = new Button("Calcular Cierre Diario");
        Label lblTotal = new Label("Saldo Esperado en Caja: $--");
        lblTotal.setStyle("-fx-font-weight: bold;");

        btnCierre.setOnAction(e -> lblTotal.setText("Saldo Esperado en Caja: $" + useCase.calcularSaldoCaja()));
        hbCierre.getChildren().addAll(btnCierre, lblTotal);

        vbox.getChildren().addAll(lbl, tabla, hbCierre);
        return vbox;
    }

    public static void main(String[] args) {
        launch(args);
    }
}