package ggc;

import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;
import java.util.Map;

import javax.management.Notification;

import java.util.List;
import java.util.ArrayList;

import ggc.exceptions.*;
//FIXME import classes (cannot import from pt.tecnico or ggc.app)

/** Façade for access. */
public class WarehouseManager {

  /** Name of file storing current store. */
  private String _filename = "";

  /** The warehouse itself. */
  private Warehouse _warehouse = new Warehouse();

  //FIXME define other attributes
  /**
   * @@throws IOException
   * @@throws FileNotFoundException
   * @@throws MissingFileAssociationException
   */
  public void save() throws IOException, FileNotFoundException, MissingFileAssociationException {
    //FIXME implement serialization method
    if (_filename.equals("")) {
      throw new MissingFileAssociationException();
    }
    else {
    ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(_filename)));
    out.writeObject(this._warehouse);
    out.close();
    }
  }

  /**
   * @@param filename
   * @@throws MissingFileAssociationException
   * @@throws IOException
   * @@throws FileNotFoundException
   */
  public void saveAs(String filename) throws MissingFileAssociationException, FileNotFoundException, IOException {
    _filename = filename;
    save();
  }

  /**
   * @@param filename
   * @@throws UnavailableFileException
   */
  public void load(String filename) throws UnavailableFileException {
    try { 
      ObjectInputStream oi = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)));
      _warehouse = (Warehouse) oi.readObject();
      oi.close();
      _filename = filename;
    } catch (FileNotFoundException e) {
      throw new UnavailableFileException(filename);
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param textfile
   * @throws ImportFileException
   */
  public void importFile(String textfile) throws ImportFileException {
    try {
	    _warehouse.importFile(textfile);
    } catch (IOException | BadEntryException /* FIXME maybe other exceptions */ e) {
	    throw new ImportFileException(textfile);
    }
  }

  /** Getter and for the Date */
  public int getDate(){
    return _warehouse.getDate();
  }

  /** catch exception on a higher level */
  public void advanceDate(int days) throws DaysToAdvanceNotValidException {
    _warehouse.advanceDate(days);
  }

  public Collection<Partner> getPartners() {
    return _warehouse.getPartners();
  }

  public void registerPartner(String id, String name, String address) throws DuplicatePartnerException {
    _warehouse.registerPartner(id, name, address);
  }

  public Partner getPartner(String id) throws UnknownPartnerException {
    return _warehouse.getPartner(id);
  }

  public Collection<Product> getProducts() {
    return _warehouse.getProducts();
  }

  public Product getProduct(String id) throws UnknownProductException {
    return _warehouse.getProduct(id);
  }

  public List<Batch> getAllBatches() {
    return _warehouse.getAllBatches();
  }

  public TreeSet<Batch> getBatchesByProduct(String id) throws UnknownProductException {
    return _warehouse.getBatchesByProduct(id);
  }

  public List<Batch> getBatchesByPartner(String id) throws UnknownPartnerException {
    return _warehouse.getBatchesByPartner(id);
  }

  public Collection<Batch> getBatchesUnderPrice(double limit) {
    return Collections.unmodifiableCollection(_warehouse.getBatchesUnderPrice(limit));
  }

  public void registerBatchSimple(String id_prod, String id_par, double price, int stock) {
    _warehouse.registerBatchSimple(id_prod, id_par, price, stock);
  }

  public void registerPurchaseExistingProduct(String partnerId, String productId, double price, int amount) {
    _warehouse.registerPurchaseExistingProduct(partnerId, productId, price, amount);
  }

  public void registerPurchaseNewSimpleProduct(String partnerId, String productId, double price, int amount) {
    _warehouse.registerPurchaseNewSimpleProduct(partnerId, productId, price, amount);
  }

  public void registerPurchaseNewDerivateProduct(String partnerId, String productId, double price, int amount, double aggravation, ArrayList<String> productsIds, ArrayList<Integer> productsQuantity) {
    _warehouse.registerPurchaseNewDerivateProduct(partnerId, productId, price, amount, aggravation, productsIds, productsQuantity);
  }

  public boolean productExists(String id) {
    return _warehouse.productExists(id);
  }

  public void addProductNotification(Product product, Partner partner){
    _warehouse.addProductNotification(product, partner);
  }

  public void registerTransactionSale(String partnerId, String productId, int paymentDeadline, int amount) throws OutOfStockProductException {
    _warehouse.registerTransactionSale(partnerId, productId, paymentDeadline, amount);
  }

  public Collection<Transaction> getPartnerTransactionsPurchase(Partner partner){
    return _warehouse.getPartnerTransactionsPurchase(partner);
  }

  public Collection<Transaction> getPartnerTransactionsSale(Partner partner){
    return _warehouse.getPartnerTransactionsSale(partner);
  }

  public Collection<NotificationX> getNotifications(Partner p){
    return Collections.unmodifiableCollection(_warehouse.getNotifications(p));
  }

  public String getNotificationsString(Partner p){
    return p.getNotifications().toString();
  }

  public Collection<Transaction> getTransactionsPurchase(){
    return Collections.unmodifiableCollection(_warehouse.getTransactionsList(_warehouse.getTransactionsPurchase()));
  }

  public Collection<Transaction> getTransactionsSaleAndDesaggregation(){
    return Collections.unmodifiableCollection(_warehouse.getTransactionsList(_warehouse.getTransactionsSaleAndDesaggregation()));
  }

  public Transaction getTransaction(int id) throws UnknownTransactionException{
    return _warehouse.getTransaction(id);
  }

  public Transaction getPurchase(int id){
    return _warehouse.getPurchase(id);
  }

  public Transaction getSaleOrDesaggregation(int id){
    return _warehouse.getSaleOrDesaggregation(id);
  }

  public double getAvailableBalance(){
    return _warehouse.getAvailableBalance();
  }

  public double getAccountingBalance(){
    return _warehouse.getAccountingBalance();
  }

  public void receivePaymentTransaction(int id) throws UnknownTransactionException {
    _warehouse.receivePaymentTransaction(id);
  }

  public Collection<Transaction> getPartnerPayedSales(Partner p) throws UnknownPartnerException{
    return Collections.unmodifiableCollection(_warehouse.getPartnerPayedSales(p));
  }

  public void deletePartnerNotifications(Partner p){
    _warehouse.deletePartnerNotifications(p);
  }

  public void registerBreakdown(Partner partner, Product product, int amount) throws OutOfStockProductException, UnknownPartnerException, UnknownProductException {
    _warehouse.registerBreakdown(partner, product, amount);
  }
}
