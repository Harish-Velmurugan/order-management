import java.sql.*;
import java.io.*;
import java.util.*;

class Main{
    static int customer_id=-1;
    static Scanner sc = new Scanner(System.in);
    public static void viewOrderDetailS(int Order_ID){
        System.out.println("Viewing Order Details");
        try{
            int choice = 1;
            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/MINI?characterEncoding=utf8","root","Harishwin@123");
            PreparedStatement stmt = con.prepareStatement("select Items.Item_ID,Name,Selling_cost,OrderItems.Quantity from Items join OrderItems on Items.Item_ID=OrderItems.Item_ID where OrderItems.Order_ID = ?;");


            stmt.setInt(1,Order_ID);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                System.out.println(rs.getInt(1)+" "+rs.getString(2)+"  "+rs.getInt(3)+"  "+rs.getInt(4) );

            }

            con.close();
        }catch(Exception e){ System.out.println(e);}

    }
    public static void viewOrders(){
        try{
            int choice = 1;
            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/MINI?characterEncoding=utf8","root","Harishwin@123");
            PreparedStatement stmt = con.prepareStatement("select Order_Id,CreatedAt,Total,Discount from Orders join Customers on Orders.Customer_ID = Customers.Customer_ID where Customers.Customer_ID=?");

            int id = customer_id;
            stmt.setInt(1,id);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                System.out.println(rs.getInt(1)+" "+rs.getString(2)+"  "+rs.getInt(3)+"  "+rs.getInt(4) );

            }


            while(choice!=-1){
                System.out.println("Select the order ID you wish to view( -1 to go back ):");
                choice = sc.nextInt();
                if(choice==-1){
                    break;
                }
                else {
                    viewOrderDetailS(choice);
                }
            }
            con.close();
        }catch(Exception e){ System.out.println(e);}
    }
    public static void placeOrder(){
        System.out.println("Order Placed");
    }

    public static void viewItems(){
       // select Items.Item_ID,Name,Selling_cost,OrderItems.Quantity from Items join OrderItems on Items.Item_ID=OrderItems.Item_ID where OrderItems.Order_ID = 1;

    }
    public static void showMenu() {
        int choice = 1;
        while (choice != 3) {
            System.out.println("1.View Orders\n2.Place Order\n3.View Items\n4.Exit");
            choice = sc.nextInt();
            switch (choice) {
                case 1:
                    viewOrders();
                    break;
                case 2:
                    placeOrder();
                    break;
                case 3:
                    viewItems();
                    break;
                case 4:
                    break;
                default:
                    break;
            }
        }
    }
    public static void signUp() {
       // System.out.println("SignedUp");
        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/MINI?characterEncoding=utf8","root","Harishwin@123");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Customer_ID FROM Customers");
            int id = 0,age;
            String username,password,phone,email;
            if(rs.last()){
                id = rs.getInt("Customer_ID") + 1;
            }
            sc.nextLine();
            System.out.print("Enter username:");
            username = sc.nextLine();
            System.out.print("Enter password:");
            password = sc.nextLine();
            System.out.print("Enter phone:");
            phone = sc.nextLine();
            System.out.print("Enter email:");
            email = sc.nextLine();
            System.out.print("Enter age:");
            age = sc.nextInt();
            PreparedStatement ins=con.prepareStatement("INSERT INTO Customers VALUES (?,?,?,?,?,?,?) ");

            ins.setInt(1,id);
            ins.setString(2,username);
            ins.setInt(3,age);
            ins.setString(4,phone);
            ins.setString(5,email);
            ins.setInt(6,0);
            ins.setString(7,password);
            int i = ins.executeUpdate();
            con.close();
            System.out.println("SignedUp");
            signIn();

        }catch(Exception e){ System.out.println(e);}
    }
    public static void signIn(){
        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/MINI?characterEncoding=utf8","root","Harishwin@123");
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM Customers where Mail=?");

            String username,password;
            sc.nextLine();
            System.out.print("Enter username:");
            username = sc.nextLine();
            System.out.print("Enter password:");
            password = sc.nextLine();
            stmt.setString(1,username);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            if(rs.getString("Password").equals(password)){
                //session.setAttribute("customer_id", rs.getInt("Customer_ID"));
                customer_id = rs.getInt("Customer_ID");
                System.out.println("authentication sucessfull");
                showMenu();
	}
            else{
                System.out.println("wrong password");
            }


            con.close();
        }catch(Exception e){ System.out.println(e);}

    }

    public static void main(String args[]){

        int choice=1;
        while(choice!=3){
            System.out.println("1.SignUp\n2.SignIn\n3.Exit");
            choice = sc.nextInt();
            switch (choice){
                case 1:signUp();
                        break;
                case 2:signIn();
                        break;
                case 3:break;
                default:break;
            }
        }
    }
}