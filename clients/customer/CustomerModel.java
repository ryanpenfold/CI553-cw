package clients.customer;

import catalogue.Basket;
import catalogue.Product;
import debug.DEBUG;
import middle.MiddleFactory;
import middle.OrderProcessing;
import middle.StockException;
import middle.StockReader;

import javax.swing.*;
import java.util.Observable;

/**
 * Implements the Model of the customer client
 * @author  Mike Smith University of Brighton
 * @version 1.0
 */
public class CustomerModel extends Observable
{
  private Product     theProduct = null;          // Current product
  private Basket      theBasket  = null;          // Bought items

  private String      pn = "";                    // Product being processed

  private StockReader     theStock     = null;
  private OrderProcessing theOrder     = null;
  private ImageIcon       thePic       = null;

  /*
   * Construct the model of the Customer
   * @param mf The factory to create the connection objects
   */
  public CustomerModel(MiddleFactory mf)
  {
    try                                          // 
    {  
      theStock = mf.makeStockReader();           // Database access
    } catch ( Exception e )
    {
      DEBUG.error("CustomerModel.constructor\n" +
                  "Database not created?\n%s\n", e.getMessage() );
    }
    theBasket = makeBasket();                    // Initial Basket
  }
  
  /**
   * return the Basket of products
   * @return the basket of products
   */
  public Basket getBasket()
  {
    return theBasket;
  }

  /**
   * Check if the product is in Stock
   * @param productNum The product number
   */
  public void doCheck(String productNum )
  {
    theBasket.clear();                          // Clear s. list
    String theAction = "";
    try {
    	pn  = productNum.trim();                    // Product no.
    } catch (Exception e) {
    	boolean keepLoop = true;					// Infinite Loop
    	int i = 0;									// Iterator (for Product List)
    	while(keepLoop == true) {
    		i++;
    		String s = Integer.toString(i);
    		try {
				keepLoop = theStock.exists(s);
			} catch (StockException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		if (keepLoop == true) {
    			Product p;
				try {
					p = theStock.getDetails(s);
					System.out.println(p.getDescription());
					if(p.getDescription() == productNum) {
	    				pn = s.trim();
	    				keepLoop = false;
	    			}
				} catch (StockException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    		}
    	}
    }
    int    amount  = 1;                         //  & quantity
    
    try
    {
      if ( theStock.exists( pn ) )              // Stock Exists?
      {                                         // T
        Product pr = theStock.getDetails( pn ); //  Product
        if ( pr.getQuantity() >= amount )       //  In stock?
        { 
          theAction =                           //   Display 
            String.format( "%s : %7.2f (%2d) ", //
              pr.getDescription(),              //    description
              pr.getPrice(),                    //    price
              pr.getQuantity() );               //    quantity
          pr.setQuantity( amount );             //   Require 1
          theBasket.add( pr );                  //   Add to basket
          thePic = theStock.getImage( pn );     //    product
        } else {                                //  F
          theAction =                           //   Inform
            pr.getDescription() +               //    product not
            " not in stock" ;                   //    in stock
        }
      } else {                                  // F
        theAction =                             //  Inform Unknown
          "Unknown product number " + pn;       //  product number
      }
    } catch( StockException e )
    {
      DEBUG.error("CustomerClient.doCheck()\n%s",
      e.getMessage() );
    }
    setChanged(); notifyObservers(theAction);
  }
  
  /**
   * Find the stock by name
   */

  public void getStockIdFromName(String name) {
	  String pn;
	  for(int i = 0; i < 50; i++) {
		  if (i < 10) {
			  pn = "000" + Integer.toString(i);
		  } else if (i < 100) {
			  pn = "00" + Integer.toString(i);
		  } else if (i < 1000) {
			  pn = "0" + Integer.toString(i);
		  } else {
			  pn = Integer.toString(i);
		  }
		  try {
			if (theStock.exists(pn)) {
				  Product pr = theStock.getDetails(pn);
				  if (pr.getDescription().contains(name)) {
					  doCheck(pn);
					  break;
					  
				  }
			  }
		} catch (StockException e) {
			// TODO Auto-generated catch block
			DEBUG.error("Error", e);
		}
	  }
  }
  
  /**
   * Clear the products from the basket
   */
  public void doClear()
  {
    String theAction = "";
    theBasket.clear();                        // Clear s. list
    theAction = "Enter Product Number";       // Set display
    thePic = null;                            // No picture
    setChanged(); notifyObservers(theAction);
  }
  
  /**
   * Return a picture of the product
   * @return An instance of an ImageIcon
   */ 
  public ImageIcon getPicture()
  {
    return thePic;
  }
  
  /**
   * ask for update of view callled at start
   */
  private void askForUpdate()
  {
    setChanged(); notifyObservers("START only"); // Notify
  }

  /**
   * Make a new Basket
   * @return an instance of a new Basket
   */
  protected Basket makeBasket()
  {
    return new Basket();
  }
}

