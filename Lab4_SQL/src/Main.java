import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {
        Database db = new Database();
        db.connect();
        db.fill();
        db.showAll();

        Scanner scan = new Scanner(System.in);
        String command;
        int prodid;
        String title;
        double cost;
        double newPrice, bound1, bound2;
        boolean continueReading = true;

        while (continueReading)
        {
            System.out.println("\nВведите команду. . .\n");
            command = scan.next();
            switch (command)
            {
                case "/add":
                    try {
                        prodid = scan.nextInt();
                    }
                    catch (InputMismatchException e)
                    {
                        System.out.println("ОШИБКА: неправильный ввод\n" +
                                            "prodid должен быть целым числом");
                        scan.nextLine();
                        break;
                    }
                    title = scan.next();
                    try {
                        cost = scan.nextDouble();
                    }
                    catch (InputMismatchException e)
                    {
                        System.out.println("ОШИБКА: неправильный ввод\n" +
                                            "Стоимость должна быть числом");
                        scan.nextLine();
                        break;
                    }
                    db.addItem(prodid, title, cost);
                    break;
                case "/delete":
                    title = scan.next();
                    db.deleteItem(title);
                    break;
                case "/show_all":
                    db.showAll();
                    break;
                case "/price":
                    title = scan.next();
                    db.price(title);
                    break;
                case "/change_price":
                    title = scan.next();
                    try {
                        newPrice = scan.nextDouble();
                    }
                    catch (InputMismatchException e)
                    {
                        System.out.println("ОШИБКА: неправильный ввод\n" +
                                            "Необходимо ввести число");
                        scan.nextLine();
                        break;
                    }
                    db.changePrice(title, newPrice);
                    break;
                case "/filter_by_price":
                    try {
                        bound1 = scan.nextDouble();
                        bound2 = scan.nextDouble();
                    }
                    catch (InputMismatchException e)
                    {
                        System.out.println("ОШИБКА: неправильный ввод\n" +
                                            "Необходимо ввести числа");
                        scan.nextLine();
                        break;
                    }

                    db.filterByPrice(bound1, bound2);
                    break;
                case "/exit":
                    continueReading = false;
                    break;
                default:
                    System.out.println("ОШИБКА. Введена неправильная команда");
                    scan.nextLine();
            }
        }
        db.disconnect();
    }
}
