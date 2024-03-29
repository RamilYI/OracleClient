package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    // переменные, представленные в fxml-документе
    @FXML private Button entry;
    @FXML private Button swap;
    @FXML private TextField login;
    @FXML private PasswordField password;
    @FXML private TextField empId;
    @FXML private TextField empZk;
    @FXML private TextField empFam;
    @FXML private TextField empIm;
    @FXML private TextField empOt;
    @FXML private TextField empSpec;
    @FXML private TextField empKurs;
    @FXML private TextField empGr;
    @FXML private TableView<OracleCl> tableStudents;
    @FXML private TableColumn<OracleCl, String> FAM;
    @FXML private TableColumn<OracleCl, String> IM;
    @FXML private TableColumn<OracleCl, String> OT;
    @FXML private TableColumn<OracleCl, String> SPEC;
    @FXML private TableColumn<OracleCl, String> GR;
    @FXML private TableColumn<OracleCl, Integer> NO_ZK;
    @FXML private TableColumn<OracleCl, Integer> STUD_ID;
    @FXML private TableColumn<OracleCl, Integer> KURS;

    private DBConnection con; // экземпляр класса, нужного для коннекта с БД
    private Connection connection;
    private ResultSet rs = null;
    // здесь (переменная ниже) будут храниться данные, которые мы передадим табличной форме
    private ObservableList<OracleCl> usersData = FXCollections.observableArrayList();
    private Alert alert = new Alert(Alert.AlertType.INFORMATION); // для исключений
    private static String loginBD, passwordBD; // инициализируем значения логина и пароля, передаваемые при входе


    @Override
    public void initialize(URL location, ResourceBundle resources) { // авто-ки иниц. экземпляр класса DBConnection
        con = new DBConnection();
    }


    @FXML
    private void entryInBD(ActionEvent event) { // метод для входа непосредственно в клиент с послед. сменой fxml-дока

            try {
                loginBD = login.getText();
                passwordBD = password.getText();
                connection = con.Connect(loginBD, passwordBD);
                if (!connection.isClosed()) {
                    Stage stage = (Stage) entry.getScene().getWindow();
                    stage.close();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DB.fxml"));
                    Parent root1 = fxmlLoader.load();
                    stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setTitle("База данных");
                    stage.setScene(new Scene(root1));
                    stage.show();
                }
            }
            catch (Exception ex) {err(ex); System.out.println(ex);}
    }

    @FXML
    private void entryAuth(ActionEvent event) throws IOException { // метод, возвращающий в форму авторизации, с послед. сменой fxml-дока
        Stage stage = (Stage) swap.getScene().getWindow();
        stage.close();

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Авторизация");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void loadDataFromDatabase(ActionEvent event) { // загрузка таблицы
        refreshtable();
        FAM.setCellValueFactory(new PropertyValueFactory<OracleCl, String>("FAM"));
        IM.setCellValueFactory(new PropertyValueFactory<OracleCl, String>("IM"));
        OT.setCellValueFactory(new PropertyValueFactory<OracleCl, String>("OT"));
        SPEC.setCellValueFactory(new PropertyValueFactory<OracleCl, String>("SPEC"));
        STUD_ID.setCellValueFactory(new PropertyValueFactory<OracleCl, Integer>("STUD_ID"));
        GR.setCellValueFactory(new PropertyValueFactory<OracleCl, String>("GR"));
        KURS.setCellValueFactory(new PropertyValueFactory<OracleCl, Integer>("KURS"));
        NO_ZK.setCellValueFactory(new PropertyValueFactory<OracleCl, Integer>("NO_ZK"));
        tableStudents.setItems(usersData);
        refreshtable();
    }

    private void err(Exception ex) { // вывод ошибки на экран
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText("" + ex);
        alert.showAndWait();
    }

    private void refreshtable() { // обновление таблицы
        usersData.clear();
        try {
            connection = con.Connect(loginBD, passwordBD);
            rs = connection.createStatement().executeQuery("SELECT * FROM LISTOFSTUDENTS.STUDENTS");
            while (rs.next()) {
                usersData.add(new OracleCl(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getInt(7), rs.getString(8)));

            }
            tableStudents.setItems(usersData);
        }
        catch (Exception ex) {err(ex); System.out.println(ex);}
    }

    @FXML
    private void delStud(ActionEvent event) { // удаления определенной строки

        try {
            Integer delID = Integer.parseInt(empId.getText());
            connection = con.Connect(loginBD, passwordBD);
            rs = connection.createStatement().executeQuery("DELETE FROM LISTOFSTUDENTS.STUDENTS\n" +
                    "WHERE STUD_ID = "+ delID +" ");
        }
        catch (Exception ex) {err(ex); System.out.println(ex);}

        refreshtable();
    }

    @FXML
    private void changeelem(ActionEvent event) { // изменение строки

       try {
            Integer id = Integer.parseInt(empId.getText()); //Integer.parseInt()
            Integer zk = Integer.parseInt(empZk.getText());
            Integer kr = Integer.parseInt(empKurs.getText());
            String fam = empFam.getText();
            String im = empIm.getText();
            String ot = empOt.getText();
            String sc = empSpec.getText();
            String gr = empGr.getText();
            /*String[] listTField = {fam, im, ot, sc, gr};
            Integer[] listIField = {zk, kr};
            String[] listCol = {"FAM", "IM", "OT", "SPEC", "GR"};
            String[] listICol = {"NO_ZK", "KURS"};
            String inquiry = "";
           for (int i = 0; i < listICol.length; i++) {
               if (listIField[i] != null) {
                   inquiry += listCol[i] + " = "  + listTField[i] + ",";
               }
           }
            for (int i = 0; i < listCol.length; i++) {
                if (listTField[i] != "") {
                    inquiry += listCol[i] + " = " + "'" + listTField[i] + "'" + ",";
                }
            }
            inquiry = inquiry.substring(0, inquiry.length() - 1); */
            connection = con.Connect(loginBD, passwordBD);
            rs = connection.createStatement().executeQuery(
                     "UPDATE LISTOFSTUDENTS.STUDENTS\n" +
                             "SET " +
                             " NO_ZK = "+ zk +"," +
                              "FAM = '"+ fam +"'," +
                              "IM = '"+ im +"'," +
                              "OT = '"+ ot +"'," +
                              "SPEC = '" + sc + "'," +
                              "KURS = "+ kr +"," +
                              "GR = '" + gr + "'\n" +
                             "WHERE STUD_ID = "+ id +"\n"
            );
        }
        catch (Exception ex) {err(ex); System.out.println(ex);}
        refreshtable();
          /* delStud(event);
           addelem(event);
           refreshtable();*/
    }

    @FXML
    private void addelem(ActionEvent event) { // добавление новой строки
        try {
            /*String[] listCol = {empId.getText(), empZk.getText(), empKurs.getText(),
                    empFam.getText(), empIm.getText(), empOt.getText(), empSpec.getText(),
                    empGr.getText()};*/

            Integer id = Integer.parseInt(empId.getText());
            Integer zk = Integer.parseInt(empZk.getText());
            Integer kr = Integer.parseInt(empKurs.getText());
            String fam = empFam.getText();
            String im = empIm.getText();
            String ot = empOt.getText();
            String sc = empSpec.getText();
            String gr = empGr.getText();
            connection = con.Connect(loginBD, passwordBD);
            rs = connection.createStatement().executeQuery("INSERT INTO  LISTOFSTUDENTS.STUDENTS\n" +
                    "(STUD_ID, NO_ZK, FAM, IM, OT, SPEC, KURS, GR)\n" +
                    "VALUES\n" +
                    "("+ id +", "+ zk +", '"+ fam +"', '"+ im +"', '"+ ot +"', '"+ sc +"', "+ kr +", '"+ gr +"')");
        }
        catch (Exception ex) {err(ex); System.out.println(ex);}
        refreshtable();
    }
}
