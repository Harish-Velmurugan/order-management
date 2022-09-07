
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

class Main {
    static Connection con;
    static int customer_id = -1;
    static int adminLogin = 0;
    static Scanner sc = new Scanner(System.in);

    public static void dbconnect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MINI?characterEncoding=utf8", "root", "Harishwin@123");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void setAdmin() {
        int choice;
        System.out.println("Do you want to continue as a admin?\n1.Yes\n2.No\nEnter Your Choice: ");
        choice = sc.nextInt();
        if (choice == 1) {
            adminLogin = 1;
        }
    }

    public static void test() {
        try {
            //test any defective code block
            int item_id = 1;
            int total = 0;
            int quantity = 2;
            PreparedStatement cost = con.prepareStatement("SELECT Selling_cost FROM Items WHERE Item_ID=? ");
            cost.setInt(1, item_id);
            ResultSet tot = cost.executeQuery();
            tot.next();
            total = total + (tot.getInt("Selling_cost")) * quantity;
            System.out.println(total);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void viewOrderDetailS(int Order_ID) {
        System.out.println("Viewing Order Details");
        try {
            int choice = 1;
            PreparedStatement stmt = con.prepareStatement("select Items.Item_ID,Name,Selling_cost,OrderItems.Quantity from Items join OrderItems on Items.Item_ID=OrderItems.Item_ID where OrderItems.Order_ID = ?;");
            PreparedStatement tot = con.prepareStatement("select Total from Orders where Order_ID = ?");
            tot.setInt(1, Order_ID);
            ResultSet r = tot.executeQuery();
            r.next();
            stmt.setInt(1, Order_ID);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Item ID  Item Name  Cost  Quantity");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " " + rs.getString(2) + "  " + rs.getInt(3) + "  " + rs.getInt(4));

            }
            System.out.println("Total : " + r.getInt("Total"));

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public static void viewOrders() {
        try {
            int choice = 1;
            PreparedStatement stmt = con.prepareStatement("select Order_Id,CreatedAt,Total,Discount from Orders join Customers on Orders.Customer_ID = Customers.Customer_ID where Customers.Customer_ID=?");
            int id = customer_id;
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Order Id  Placed At  Total  Discount");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " " + rs.getString(2) + "  " + rs.getInt(3) + "  " + rs.getInt(4));

            }
            while (choice != -1) {
                System.out.println("Select the order ID you wish to view( -1 to go back ):");
                choice = sc.nextInt();
                if (choice == -1) {
                    break;
                } else {
                    viewOrderDetailS(choice);
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void viewItems() {
        try {
            //int choice = 1;
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Item_ID, Name, Current_stock, Selling_cost from Items");
            System.out.println("Item ID  Name  Stock  Cost");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " " + rs.getString(2) + "  " + rs.getInt(3) + "  " + rs.getInt(4));

            }
        } catch (Exception e) {
            System.out.println(e);
        }
        // select Items.Item_ID,Name,Selling_cost,OrderItems.Quantity from Items join OrderItems on Items.Item_ID=OrderItems.Item_ID where OrderItems.Order_ID = 1;
        return;
    }

    public static void placeOrder() {

        int choice = 1;
        int item_id, quantity;
        int total = 0;
        int current_stock,updated_stock;
        HashMap<Integer, Integer> cart = new HashMap<>();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Order_ID FROM Orders");
            int id = 0;
            if (rs.last()) {
                id = rs.getInt("Order_ID") + 1;
            }
            else{
                id = 1;
            }
            viewItems();
            while (choice != 3) {
                System.out.println("1.AddItem\n2.Confirm Order\n3.Exit");
                choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        try {
                            System.out.print("Enter the item_id:");
                            item_id = sc.nextInt();
                            System.out.print("Enter the quantity:");
                            quantity = sc.nextInt();
                            cart.put(item_id, quantity);
                            PreparedStatement cost = con.prepareStatement("SELECT Selling_cost FROM Items WHERE Item_ID=? ");
                            cost.setInt(1, item_id);
                            ResultSet iss = cost.executeQuery();
                            iss.next();
                            total = total + (iss.getInt("Selling_cost")) * quantity;
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                        ;
                        break;
                    case 2:
                        PreparedStatement gs = con.prepareStatement("SELECT Current_stock FROM Items where Item_ID=?");
                        PreparedStatement ps = con.prepareStatement("INSERT INTO OrderItems VALUES (?,?,?)");
                        PreparedStatement order = con.prepareStatement("INSERT INTO Orders VALUES (?,?,?,?,?,?)");
                        PreparedStatement ds = con.prepareStatement("UPDATE Items SET Current_stock=? WHERE Item_ID=?");
                        Calendar cal = Calendar.getInstance();
                        Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
                        order.setInt(1, id);
                        order.setTimestamp(2, timestamp);
                        order.setInt(3, total);
                        order.setInt(4, 10);
                        order.setInt(5, 18);
                        order.setInt(6, customer_id);
                        order.executeUpdate();
                        for (Map.Entry<Integer, Integer> curr_item : cart.entrySet()) {
                            item_id = curr_item.getKey();
                            quantity = curr_item.getValue();
                            gs.setInt(1,item_id);
                            ResultSet stock_set = gs.executeQuery();
                            stock_set.next();
                            current_stock = stock_set.getInt("Current_stock");
                            updated_stock = current_stock - quantity;
                            if(updated_stock<0){
                                System.out.println("Low stock couldn't place order");
                                break;
                            }
                            ds.setInt(1,updated_stock);
                            ds.setInt(2,item_id);
                            ds.executeUpdate();

                            ps.setInt(1, item_id);
                            ps.setInt(2, id);
                            ps.setInt(3, quantity);
                            ps.executeUpdate();

                        }
                        System.out.println("Order Placed Successfully");
                        return;

                    case 3:
                        return;


                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void viewProfile() {
        try {
            int choice = 1;
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM Customers WHERE Customer_ID=?");
            int id = customer_id;
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Customer Id || Name || Age || Phone || Mail");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " " + rs.getString(2) + "  " + rs.getInt(3) + "  " + rs.getString(4) + " " + rs.getString(5));

            }
            if (adminLogin == 0) {
                showMenu();
            } else {
                showAdminMenu();
                return;
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public static void updateProfile() {
        try {
            int age;
            String name, phone, mail;
            sc.nextLine();
            PreparedStatement stmt = con.prepareStatement("UPDATE Customers SET Name=?,Age=?,Phone=?,Mail=? WHERE Customer_ID=?");
            System.out.print("Enter Your Name:");
            name = sc.nextLine();
            System.out.print("Enter Your Age:");
            age = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter Your Phone Number:");
            phone = sc.nextLine();
            System.out.print("Enter Your Mail: ");
            mail = sc.nextLine();
            stmt.setInt(5, customer_id);
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.setString(3, phone);
            stmt.setString(4, mail);
            stmt.executeUpdate();
            System.out.println("profile Updated");
            viewProfile();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void showMenu() {
        int choice = 1;
        while (choice != 4) {
            System.out.println("1.View Orders\n2.Place Order\n3.View Items\n4.View Profile\n5.Update Profile\n6.Exit");
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
                    viewProfile();
                    break;
                case 5:
                    updateProfile();
                    break;
                case 6:
                    System.exit(0);
                default:
                    break;
            }
        }
    }

    public static void viewAllOrders() {
        try {
            int choice = 1;
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Orders");
            System.out.println("Customer ID  Order ID  Placed On  Total  Discount  Tax");
            while (rs.next()) {
                System.out.println(rs.getInt(6) + " " + rs.getInt(1) + " " + rs.getString(2) + "  " + rs.getInt(3) + "  " + rs.getInt(4) + " " + rs.getInt(5));

            }
            while (choice != -1) {
                System.out.println("Select the order ID you wish to view( -1 to go back ):");
                choice = sc.nextInt();
                if (choice == -1) {
                    break;
                } else {
                    viewOrderDetailS(choice);
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public static void updateItems() {
        try {
            int item_ID, stock, buyingCost, sellingCost;
            String itemName, smanufacturedDate, sexpiryDate;
            viewItems();
            System.out.print("Enter Item ID to update : ");
            item_ID = sc.nextInt();
            sc.nextLine();
            PreparedStatement stmt = con.prepareStatement("UPDATE Items SET Name=?,Current_stock=?,Manufactured_Date=?,Expiry_Date=?,Buying_Cost=?,Selling_cost=? WHERE Item_ID=?");
            System.out.print("Enter Item Name:");
            itemName = sc.nextLine();
            System.out.print("Enter Current Stock:");
            stock = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter Manufactured Date:");
            smanufacturedDate = sc.nextLine();
            Date manufacturedDate = new SimpleDateFormat("dd/MM/yyyy").parse(smanufacturedDate);
            System.out.print("Enter Expiry Date:");
            sexpiryDate = sc.nextLine();
            Date expiryDate = new SimpleDateFormat("dd/MM/yyyy").parse(sexpiryDate);
            System.out.print("Enter Buying Cost:");
            buyingCost = sc.nextInt();
            System.out.print("Enter Selling Cost:");
            sellingCost = sc.nextInt();
            stmt.setInt(7, item_ID);
            stmt.setString(1, itemName);
            stmt.setInt(2, stock);
            stmt.setTimestamp(3, new Timestamp(manufacturedDate.getTime()));
            stmt.setTimestamp(4, new Timestamp(expiryDate.getTime()));
            stmt.setInt(5, buyingCost);
            stmt.setInt(6, sellingCost);
            stmt.executeUpdate();
            System.out.println("Item Updated");
            viewItems();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public static void createItem() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Item_ID FROM Items");
            int id = 0, stock, buyingCost, sellingCost;
            String itemName, smanufacturedDate, sexpiryDate;
            if (rs.last()) {
                id = rs.getInt("Item_ID") + 1;
            }
            sc.nextLine();
            System.out.print("Enter Item Name:");
            itemName = sc.nextLine();
            System.out.print("Enter Current Stock:");
            stock = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter Manufactured Date:");
            smanufacturedDate = sc.nextLine();
            Date manufacturedDate = new SimpleDateFormat("dd/MM/yyyy").parse(smanufacturedDate);
            System.out.print("Enter Expiry Date:");
            sexpiryDate = sc.nextLine();
            Date expiryDate = new SimpleDateFormat("dd/MM/yyyy").parse(sexpiryDate);
            System.out.print("Enter Buying Cost:");
            buyingCost = sc.nextInt();
            System.out.print("Enter Selling Cost:");
            sellingCost = sc.nextInt();
            PreparedStatement ins = con.prepareStatement("INSERT INTO Items VALUES (?,?,?,?,?,?,?) ");
            ins.setInt(1, id);
            ins.setString(2, itemName);
            ins.setInt(3, stock);
            ins.setTimestamp(4, new Timestamp(manufacturedDate.getTime()));
            ins.setTimestamp(5, new Timestamp(expiryDate.getTime()));
            ins.setInt(6, buyingCost);
            ins.setInt(7, sellingCost);
            ins.executeUpdate();
            System.out.println("Item Created");


        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void deleteItem() {
        try {
            int item_ID;
            viewItems();
            System.out.print("Enter Item ID to delete : ");
            item_ID = sc.nextInt();
            PreparedStatement stmt = con.prepareStatement("DELETE FROM Items WHERE Item_ID=?;");
            stmt.setInt(1, item_ID);
            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public static void showAdminMenu() {
        int choice = 0;
        while (choice != 6) {
            System.out.println("1.View Orders\n2.View Items\n3.Edit Item\n4.Create Item\n5.Delete Item\n6.View Profile\n7.Update Profile\n8.Exit");
            choice = sc.nextInt();
            switch (choice) {
                case 1:
                    viewAllOrders();
                    break;
                case 2:
                    viewItems();
                    break;
                case 3:
                    updateItems();
                    break;
                case 4:
                    createItem();
                    break;
                case 5:
                    deleteItem();
                    break;
                case 6:
                    viewProfile();
                    break;
                case 7:
                    updateProfile();
                    break;
                case 8:
                    System.exit(0);
                    break;


            }
        }
    }

    public static void signUp() {
        // System.out.println("SignedUp");
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Customer_ID FROM Customers");
            int id = 0, age;
            String username, password, phone, email;
            if (rs.last()) {
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
            PreparedStatement ins = con.prepareStatement("INSERT INTO Customers VALUES (?,?,?,?,?,?,?) ");
            ins.setInt(1, id);
            ins.setString(2, username);
            ins.setInt(3, age);
            ins.setString(4, phone);
            ins.setString(5, email);
            ins.setInt(6, 0);
            ins.setString(7, password);
            ins.executeUpdate();
            System.out.println("SignedUp");
            signIn();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void signIn() {
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM Customers where Mail=?");
            String username, password;
            sc.nextLine();
            System.out.print("Enter username:");
            username = sc.nextLine();
            System.out.print("Enter password:");
            password = sc.nextLine();
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            if (rs.getString("Password").equals(password)) {
                customer_id = rs.getInt("Customer_ID");
                System.out.println("authentication sucessfull");
                if (rs.getInt("isAdmin") == 1) {
                    setAdmin();
                }
                if (adminLogin == 0) {
                    showMenu();
                } else {
                    showAdminMenu();
                }

            } else {
                System.out.println("wrong password");
            }


        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public static void main(String args[]) {
        System.out.println("----------------------------------------\n   Welcome to Order Management System\n----------------------------------------");
        int choice = 1;
        //test();
        try {
            dbconnect();
            while (choice != 3) {
                System.out.print("1.SignUp\n2.SignIn\n3.Exit\nEnter Your Choice : ");
                choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        signUp();
                        break;
                    case 2:
                        signIn();
                        break;
                    case 3:
                        con.close();
                        break;
                    default:
                        break;

                }
            }

        } catch (Exception e) {
            System.out.print(e);
        }
    }
}