/* Primary Author : Robert Sanborn
 * Purpose: WWU, CS145 Winter 2017, Lab5
 * File : BookCollection.java
 * Date : 2/15/17
 *
 * Modified by Chris Reedy (Chris.Reedy@wwu.edu).
 *
 * Function of File: Maintain a collection of books.
 * Dependencies:
 *    Book.java, provided
 */


public class BookCollection {

   public class BookNotFound extends IllegalArgumentException {
      public BookNotFound(String msg) {
         super(msg);
      }
   }

   public class DuplicateBook extends IllegalArgumentException {
      public DuplicateBook(String msg) {
         super(msg);
      }
   }

   public class CollectionFull extends IllegalStateException {
      public CollectionFull(String msg) {
         super(msg);
      }
   }

   // Limit on size of collection.
   public static final int LIMIT = 200;

   // Array of Book Objects
   private Book[] aBookCollection;

   // Number of non-null Books Objects in the Array
   // aBookCollection
   private int collectionLength;


   /* Create an empty book collection of the given capacity.
    *
    * The capacity should not exceed the preset maximum capacity
    * given by LIMIT.
    */

   public BookCollection(int size) {

      // If the int parameter, size, is larger than
      // the constant LIMIT, IllegalArgumentException is thrown
      if (size > LIMIT){
         throw new IllegalArgumentException();

         // Otherwise array of Book Objects is constructed
      } else {
         aBookCollection = new Book[size];
      }
   }


   /* Change the price of a book.
    *
    * Change the price of the book specified by the given ISBN to
    * the given price.
    */
   public void changePrice(String isbn, double price) {
      Book theBook = findBook(isbn);

      // If the isbn cannot be found in the collection
      // a BookNotFound exception is thrown.
      if (theBook == (null)) {
         throw new BookNotFound(isbn);
      }

      theBook.setPrice(price);
   }


   /* Change the stock for a book.
    *
    * Change the stock of the book specified by the given ISBN by
    * the given quantity. The parameter quantity can be positive
    * (books added to stock) or negative (books sold).
    */
   public void changeStock(String isbn, int quantity) {
      Book theBook = findBook(isbn);

      // If the isbn is not found in the collection
      // a BookNotFound exception is thrown.
      if (theBook == (null)){
         throw new BookNotFound(isbn);
      }

      // If the change would result in negative stock, an InsufficientStockException
      // will be thrown by the given Book object.
      theBook.changeStock(quantity);
   }


   /* Return the size--the actual number of books--in the collection.
    */
   public int getSize() {
      return collectionLength;
   }


   /* Return the total dollar value of all the books in the collection.
    */
   public double getStockValue() {

      double sum = 0.0;

      // This is computed by summing the values for each book in the BookCollection
      // as returned by the getStockValue method of Book.
      for (int i = 0; i < getSize(); i++){
         sum += aBookCollection[i].getStockValue();
      }
      return sum;
   }


   /* Add a new book to the collection.*/
   public void addBook(Book aBook) {

      // If there are no empty spots in aBookCollection,
      // bookAdded will remain false and an exception will be thrown
      boolean bookAdded = false;

      // BookCollection array is traversed for an open spot
      for (int i = 0; i < getSize() + 1 && i  < aBookCollection.length; i++){

         // If the book is already in the collection,
         // a DuplicateBook exception is thrown.
         if ( aBookCollection[i] != (null) &&
                 (aBookCollection[i].getIsbn()).equals(aBook.getIsbn())){

            throw new DuplicateBook(aBook.getTitle() +
                    " is already inside collection");

         } else if ( aBookCollection[i] == (null) ){
            aBookCollection[i] = aBook;
            bookAdded = true;
            collectionLength += 1;
            break;
         }
      }
      // If the collection has reached it's capacity,
      // a CollectionFull exception is thrown.
      if(!bookAdded){
         throw new CollectionFull("Cannot add anymore books" +
                 " to existing collection");
      }
   }


   /* Return the Book object at index i of the collection.*/
   public Book objectAt(int i) {

      // If i < 0 or i >= getSize() an IndexOutOfBoundsException is thrown.
      if ( i < 0 || i >= getSize()){
         throw new IndexOutOfBoundsException();
      }
      return aBookCollection[i];
   }


   /* Merge the given book collections, returning a new collection which
    * contains all the books found in either or both collections.
    *
    * The capacity of the new collection will be sufficient to hold all the
    * books in the merged collection but can be larger. The collection will
    * consist of new Book objects that are distinct from the book objects in
    * the original two collections.
    */
   public static BookCollection merge(BookCollection coll1, BookCollection coll2) {

      BookCollection combinedColl;

      // If the number of "actual" Book objects inside the two BookCollections
      // is greater than the public field constant LIMIT the new merged
      // BookCollection will have a capacity equal to the LIMIT.
      int numBooks = coll1.getSize() + coll2.getSize();
      if (numBooks > LIMIT){
         combinedColl = new BookCollection(LIMIT);

         // Otherwise the merged collection will have a capacity equal to
         // the number of "actual" Book objects in the two BookCollections
      } else {
         combinedColl = new BookCollection(numBooks);
      }

      // The Book objects from the first BookCollection are added into the
      // new merged BookCollection
      for(int i = 0; i < coll1.getSize(); i++){

         // Note that each Book object added into the merged BookCollection
         // is a new Object with its state equal to that of one of the Book
         // Objects in the previous BookCollection, the first one in this case
         Book theBook1 = new Book(coll1.objectAt(i));
         combinedColl.addBook(theBook1);
      }

      // The Book objects from the second BookCollection are added into the
      // new merged BookCollection
      for (int j = 0; j < coll2.getSize(); j++){

         // Again, note that each new Book Object that the program attempts to add
         // is a new Book object that has the same state as one of the Book objects
         // inside the second BookCollection
         Book theBook2 = new Book(coll2.objectAt(j));

         // Also note that if the Book object from the second collection has
         // the same ISBN as a pre-existing Book object in the merged
         // BookCollection, the Book object inside the merged BookCollection has
         // its Stock and Price fields updated
         if ( combinedColl.findBook(theBook2.getIsbn()) != null ){
            Book matchingBook = combinedColl.findBook(theBook2.getIsbn());

            // The Book object inside the merged BookCollection has its Stock field
            // increased by the Stock of the Book Object (the "matching book") from
            // the second BookCollection
            matchingBook.changeStock(theBook2.getStock());

            // The Book object inside the merged BookCollection has its Price field
            // updated to the lowest Price value listed among the first and second
            // BookCollections
            matchingBook.setPrice(Math.min(matchingBook.getPrice(), theBook2.getPrice()));

            // If Book Object from the second BookCollection does not have a
            // duplicate present in the merged BookCollection, it is added
         } else {
            combinedColl.addBook(theBook2);
            // Note that in the possibility that the merged BookCollection has no more
            // free space, the addBook method will throw a CollectionFull exception
         }
      }
      return combinedColl;
   }


   /* Find the book with the given ISBN in this collection.
    */
   private Book findBook(String isbn) {

      // If the book cannot be found, null is returned.
      Book theBook = null;

      // Otherwise, the Book Object with the matching ISBN is returned
      for (int i = 0; i < getSize(); i++){
         if((aBookCollection[i].getIsbn()).equals(isbn)){
            theBook = aBookCollection[i];
            break;
         }
      }
      return theBook;
   }
}
