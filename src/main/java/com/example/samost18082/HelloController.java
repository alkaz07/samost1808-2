package com.example.samost18082;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HelloController {

    ObservableList<Book> books = FXCollections.observableArrayList();
    HashMap<Integer, Book> isbnMap = new HashMap<>();

    @FXML
    TableView<Book> table;
    @FXML
    Button saveBtn;

    public void initialize() throws SQLException {
        enableChangeTracking();
        try {
            zapolnitBooksFromDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        initTable();

        //startChangeTracking();

        saveBtn.setOnAction(actionEvent -> {
            System.out.println(isbnMap);
            writeChangedIsbns("out.txt");}
        );
    }


    public void writeChangedIsbns(String fname)
    {
        try {
            List<Integer> keyList = new ArrayList(isbnMap.keySet());
            Files.write(new File(fname).toPath(), keyList.toString().getBytes());

        }
        catch (IOException e)
        {
            System.out.println("aaaaaaa "+e.getMessage());
        }
    }

    public void startChangeTracking(){
        for (Book b: books     ) {
            b.titleProperty().addListener((val, o, n)->  isbnMap.put(b.getIsbn(), b) );
            b.yearProperty().addListener( (val, o, n)->  isbnMap.put(b.getIsbn(), b) );
        }
    }

    public void enableChangeTracking(){
        books.addListener((ListChangeListener<Book>) change -> {
            while(change.next())
            {
                if (change.wasAdded())
                    for (Book b: change.getAddedSubList() ) {
                        b.titleProperty().addListener((val, o, n)->  isbnMap.put(b.getIsbn(), b) );
                        b.yearProperty().addListener( (val, o, n)->  isbnMap.put(b.getIsbn(), b) );
                    }
            }
        });
    }

    @FXML
    private void zapolnitBooksFromDB() throws SQLException {
        books.clear();
        isbnMap.clear();
        Connection conn =  connectToDB();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT isbn, title, year FROM book ORDER BY title");
        rs.next();

        while (rs.next()) {
            int isbn = rs.getInt("isbn");
            String title = rs.getString("title");
            int year = rs.getInt("year");
            Book book = new Book(isbn, title,year);
            books.add(book);
            //books.add(new Book(rs.getInt("isbn"), rs.getString("title"), rs.getInt("year")));
        }
        rs.close();
        st.close();
        //System.out.println(books);
    }

    private void initTable() {
        table.getColumns().clear();

        TableColumn<Book, Integer> columnISBN = new TableColumn<>("ISBN");
        columnISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        //columnISBN.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        TableColumn<Book, String> columnTitle = new TableColumn<>("Title");
        columnTitle.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
        columnTitle.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Book, Integer> columnYear = new TableColumn<>("Year");
        columnYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        columnYear.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        table.getColumns().add(columnISBN);
        table.getColumns().add(columnTitle);
        table.getColumns().add(columnYear);
        table.setItems(books);
        table.setEditable(true);

    }

    private static Connection connectToDB() {
        String url = "jdbc:postgresql://10.10.104.166:5432/Lib?user=postgres&password=123";//&ssl=true
        try {
            Connection conn = DriverManager.getConnection(url);
            System.out.println("подключено");
            return conn;
        } catch (Exception e) {
            System.out.println("не удалось подключиться к базе. " + e.getMessage());
            return null;
        }
    }

    @FXML
    public void saveChangesToDB() throws SQLException {
        Connection conn =  connectToDB();
        //Statement st = conn.createStatement();
        PreparedStatement pst = conn.prepareStatement("UPDATE book SET title = ? , year = ? WHERE isbn = ? ");
        for (Book b: isbnMap.values()    ) {
            //String query = "UPDATE book SET title = '"+ b.getTitle() +"' , year = "+b.getYear()+" where isbn = "+b.getIsbn();
            //st.execute(query);
            pst.setString(1, b.getTitle());
            pst.setInt(   2, b.getYear() );
            pst.setInt(   3, b.getIsbn() );
            pst.execute();
            System.out.println(pst.toString());
        }
        pst.close();
        //st.close();
    }

}