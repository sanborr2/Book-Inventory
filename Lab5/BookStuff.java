/*
 * WWU: CS145 Winter 2017, Lab5
 *
 * BookStuff.java
 *
 * Modified by Chris Reedy (Chris.Reedy@wwu.edu).
 *
 * Test the functions of BookCollection class.
 *
 * Dependencies:
 *    Book.java, provided
 *    BookCollection.java, to be completed by students
 */

import java.io.*;
import java.util.Scanner;

public class BookStuff {

    public static void main(String args[]) {

        // check for the number of command line arguments
        if(args.length != 4){
            System.out.println("Please enter four file names: list1 change1 list2 change2.");
            return; // Terminate program.
        }

        /* three empty collections */
        BookCollection collection1 = new BookCollection(100);
        BookCollection collection2 = new BookCollection(50);

        /* open the first file and add each book to collection1 */
        System.out.println("Reading " + args[0]);
        if (!getBooks(collection1, args[0])) {
            return;
        }

        /* make some changes to the first book collection */
        System.out.println("Using " + args[1] + " to change collection1");
        if (!changeCollection(collection1, args[1])) {
            return;
        }

        /* display the first collection */
        displayCollection(collection1, "Collection number 1");

        /* open the second file and add each book to collection2 */
        System.out.println("Reading " + args[2]);
        if (!getBooks(collection2, args[2])) {
            return;
        }

        /* make some changes to the second book collection */
        System.out.println("Using " + args[3] + " to change collection2");
        if (!changeCollection(collection2, args[3])) {
            return;
        }

        /* display the second collection */
        displayCollection(collection2, "Collection number 2");

        /* merge collection1 and collection2 and copy them to collection3 */
        System.out.println("Merging collection 1 and collection 2");
        BookCollection collection3 = BookCollection.merge(collection1, collection2);

        /* display all collections */
        displayCollection(collection1, "Collection number 1");
        displayCollection(collection2, "Collection number 2");
        displayCollection(collection3, "Collection number 3");
    }


    /* Change the price of a book in a collection.
     *
     * Change the price of the book with the given ISBN number in the
     * given collection. The price of the book will be set to the
     * parameter price. If the book is not present in the collection
     * an appropriate error message is displayed. */
    public static void tryChangePrice(BookCollection collection, String isbn, double price) {
        try {
            collection.changePrice(isbn, price);
        } catch (BookCollection.BookNotFound ex){
            System.out.println("ISBN " + isbn + " not found in collection. Cannot change price.");
        }
    }

    /* Adjust the stock on hand after a sale.
     *
     * The stock on hand of the book with the given ISBN number in the
     * given collection is reduced by the given quantity.
     */
    public static void trySale(BookCollection collection, String isbn, int quantity) {
        try {
            collection.changeStock(isbn, -quantity);
        } catch (BookCollection.BookNotFound ex){
            System.out.println("ISBN " + isbn + " not found in the collection. Cannot sell.");
        } catch (Book.InsufficientStock ex) {
                System.out.println("ISBN " + isbn + " has insufficient stock for sale.");
        }
    }

    /* Add new inventory to the stock.
     *
     * The stock on hand of the book with the given ISBN number in the
     * given collection is increased by the given quantity.
     */
    public static void tryNewStock(BookCollection collection, String isbn, int quantity) {
        try {
            collection.changeStock(isbn, quantity);
        } catch (BookCollection.BookNotFound ex) {
            System.out.println("ISBN " + isbn + " not found in the collection. Cannot change stock.");
        }
    }

    /* Add books from input file to collection. */
    public static boolean getBooks(BookCollection collection, String fileName) {
        Scanner input = null;
        try {
            input = new Scanner(new File(fileName));
        } catch (FileNotFoundException ex) {
            System.out.println("Error: File " + fileName + " not found. Exiting program.");
            return false;
        }

        while (input.hasNextLine()) {
            String line = input.nextLine();
            Scanner lineData = new Scanner(line);
            lineData.useDelimiter("\\,");
            try {
                String title = lineData.next();
                String isbn = lineData.next();
                double price = lineData.nextDouble();
                String format = lineData.next();
                Book book = new Book(title, isbn, price, format);
                try {
                    collection.addBook(book);
                } catch (BookCollection.CollectionFull ex) {
                    System.out.println("The collection is full. No more books can be added.");
                } catch (BookCollection.DuplicateBook ex) {
                    System.out.println("Duplicate book " + isbn + " not added to the collection.");
                }
            } catch (java.util.InputMismatchException ex) {
                System.out.println("Line: " + line);
                System.out.println("Mismatched token: " + lineData.next());
                throw ex;
            }
        }
        
        return true;
    }

    /* display the contents of a book collection */
    public static void displayCollection(BookCollection collection, String heading) {
        System.out.println();
        System.out.println(heading);
        for(int i = 0; i < heading.length(); i++){
            System.out.print("*");
        }
        System.out.println();

        for(int i = 0; i < collection.getSize(); i++){
            System.out.println(collection.objectAt(i));
        }
        System.out.printf("%nThe total value of %s is $%.2f.%n%n",
                heading, collection.getStockValue());
    }

    /* do some stuff to a collection */
    public static boolean changeCollection(BookCollection collection, String changeFile) {
        Scanner input = null;
        try {
            input = new Scanner(new File(changeFile));
        } catch (FileNotFoundException ex) {
            System.out.println("Error: File " + changeFile + " not found. Exiting program.");
            return false;
        }

        while (input.hasNext()) {
            String type = input.next();
            String isbn = input.next();
            if (type.equals("stock")) {
                int amount = input.nextInt();
                tryNewStock(collection, isbn, amount);
            } else if (type.equals("price")) {
                double price = input.nextDouble();
                tryChangePrice(collection, isbn, price);
            } else if (type.equals("sale")) {
                int amount = input.nextInt();
                trySale(collection, isbn, amount);
            }
        }
        
        return true;
    }
}