package ggc;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.management.Notification;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import ggc.exceptions.*;
// FIXME import classes (cannot import from pt.tecnico or ggc.app)

/**
 * Class Warehouse implements a warehouse.
 */
public class Warehouse implements Serializable {

  /** Serial number for serialization. */
  private static final long serialVersionUID = 202109192006L;

  // FIXME define attributes
  /** List of all Partners of Warehouse */
  private Map<String, Partner> _partners;

  /** List of all Purchases */
  private Map<Integer, Transaction> _transactionsPurchase;

  /** List of all Sales and Desaggregations */
  private Map<Integer, Transaction> _transactionsSaleAndDesaggregation;

  /** List of all Products */
  private Map<String, Product> _products;

  /** Date */
  private int _date;

  /**
   * Constructor for Warehouse Class.
   * 
   * Initializes the lists for all the partners, 
   * transactions and products.
   */
  public Warehouse(){
    _partners = new TreeMap<>(new CollatorWrapper());
    _transactionsPurchase = new HashMap<>();
    _transactionsSaleAndDesaggregation = new HashMap<>();
    _products = new TreeMap<>(new CollatorWrapper());
    _date = 0;
  }

  /** Getter and for the Date */
  public int getDate() {
    return _date;
  }

  /**
   * Advances the date
   * @param day
   * @throws DaysToAdvanceNotValidException
   */
  public void advanceDate(int days) throws DaysToAdvanceNotValidException {
    if (days <= 0){
      throw new DaysToAdvanceNotValidException(days); 
    }
    else {
      _date += days;

      for (Map.Entry<Integer, Transaction> entry : _transactionsSaleAndDesaggregation.entrySet()) {
        Transaction transaction = entry.getValue();
        transaction.UpdateValueToPay(_date);
      }
    }
  }

  /**
   * Returns a Collection of Partners
   * @return collection of objects type Partner
   */
  public Collection<Partner> getPartners() {
    return Collections.unmodifiableCollection(_partners.values());
  }

  /**
   * Registers a new object partner and adds it to 
   * the List of all partners
   * @param id
   * @param name
   * @param address
   * @throws DuplicatePartnerException
   */
  public void registerPartner(String id, String name, String address) throws DuplicatePartnerException {
    if (_partners.containsKey(id)) {
      throw new DuplicatePartnerException(id);
    }
    Partner p = new Partner(id, name, address, getProductsMapStruct());
    _partners.put(id, p);
    /** add all products to notifiedProducts */
  }

  /**
   * Return an object Partner with attribute id of the input id
   * @param id
   * @return partner
   * @throws UnknownPartnerException
   */
  public Partner getPartner(String id) throws UnknownPartnerException {
    Partner partner = _partners.get(id);
    if (partner == null)
      throw new UnknownPartnerException(id);
    return partner;
  }

  /**
   * Returns a Collection of Products
   * @return collection of objects type Product
   */
  public Collection<Product> getProducts() {
    return Collections.unmodifiableCollection(_products.values());
  }

  public Map<String, Product> getProductsMapStruct(){
    return _products;
  }
  /**
   * Creates a new Product, adds it to the list of
   * all Products and return the Product
   * @param id
   * @param price
   * @return product    the Product that was just registered
   */
  public Product registerNewProduct(String id, double price) {
    Product product = new Product(id);
    product.setMaxPrice(price);
    _products.put(id, product);
    addNotifiedProductAllPartners(product);
    return product;
  }

  /**
   * Created new object of type Product with ProductDerivate's costructor, 
   * add it to list of Products and return it
   * @param id            unique identifier of Product
   * @param aggravation   aggravation (a double)
   * @param recipe        object of type Recipe associated to the Product
   * @param price         double value of Product's price
   * @return product      Product that was just created
   */
  public Product registerNewProductDerivate(String id, double aggravation, Recipe recipe, double price) {
    Product product = new ProductDerivate(id, aggravation, recipe);
    product.setMaxPrice(price);
    _products.put(id, product);
    addNotifiedProductAllPartners(product);
    return product;
  }

  /**
   * Returns object of type Product with the id of param id
   * @param id
   * @return product with identifier id
   * @throws UnknownProductException
   */
  public Product getProduct(String id) throws UnknownProductException {
    Product product = _products.get(id);
    if (product == null)
      throw new UnknownProductException(id);
    return product;
  }

  /**
   * Returns all the batches in the warehouse by the following order:
   * Product id, Partner id, ascending price and ascending stock
   * @return allbatches
   */
  public List<Batch> getAllBatches() {
    List<Batch> allbatches = new ArrayList<>();

    for (Map.Entry<String, Product> entry : _products.entrySet()) {
      for (Batch batch : entry.getValue().getBatches()) {
        allbatches.add(batch);
      }
    }
    return allbatches;
  }

  public TreeSet<Batch> getBatchesByProduct(String id) throws UnknownProductException {
    Product product = _products.get(id);
    if (product == null)
      throw new UnknownProductException(id);
    return product.getBatches();
  }

  public List<Batch> getBatchesByPartner(String id) throws UnknownPartnerException {
    List<Batch> batches = new ArrayList<>();

    Partner partner = _partners.get(id);
    if (partner == null)
      throw new UnknownPartnerException(id);
    
    for (Map.Entry<String, Product> entry : _products.entrySet()) {
      for (Batch batch : entry.getValue().getBatches()) {
        if (partner.equals(batch.getPartner()))
          batches.add(batch);
      }
    }
    return batches;
  }

  public List<Batch> getBatchesUnderPrice(double limit) {
    List<Batch> batches = new ArrayList<>();

    for (Map.Entry<String, Product> entry : getProductsMapStruct().entrySet()) {
      for (Batch batch : entry.getValue().getBatches()) {
        if (batch.getPrice() < limit)
          batches.add(batch);
      }
    }
    return batches;
  }

  /**
   * Creates a new object of type Batch, adds
   * it to the list of batches for each Product 
   * and updates maxPrice attribute for the Product
   * @param idProd id of Product
   * @param idPar id of Partner
   * @param price price (double) of Batch
   * @param stock stock (int) of Batch
   */
  public void registerBatchSimple(String idProd, String idPar, double price, int stock) {
    Partner partner = _partners.get(idPar);
    Product product;
    Batch batch;
    if (_products.containsKey(idProd)) {
      product = _products.get(idProd); 
      if (price > product.getMaxPrice()) {
        product.setMaxPrice(price);
      }
      batch = new Batch(stock, price, product, partner);
      registerNotification(batch, product);
    }
    else {
      product = registerNewProduct(idProd, price);
      batch = new Batch(stock, price, product, partner);
    }
    product.addBatches(batch);
  }

  public void registerBatchDerivateImport(String idProd, String idPar, double price, int stock, double aggravation, String recipestr) {
    Recipe recipe = new Recipe(recipestr);

    registerBatchDerivate(idProd, idPar, price, stock, aggravation, recipe);
  }

  /**
   * Creates new BatchDerivate and adds it to Product's batches
   * @param idProd     id of Product 
   * @param idPar      id of Partner
   * @param price       (double) price of Batch
   * @param stock       (int) stock of Batch
   * @param aggravation (double) aggravation
   * @param recipestr   String of the recipe of the ProductDerivate
   */
  public void registerBatchDerivate(String idProd, String idPar, double price, int stock, double aggravation, Recipe recipe) {
    Partner partner = _partners.get(idPar);
    Product product;
    
    if (_products.containsKey(idProd)) {
      product = _products.get(idProd); 
      if (price > product.getMaxPrice()) {
        product.setMaxPrice(price);
      }
    }
    else {
      product = registerNewProductDerivate(idProd, aggravation, recipe, price);
    }
    Batch batch = new BatchDerivate(stock, price, product, partner, recipe, aggravation);
    product.addBatches(batch);
    registerNotification(batch, product);
  }

  public void registerPurchaseExistingProduct(String partnerId, String productId, double price, int amount) {
    Partner partner = _partners.get(partnerId);
    Product product = _products.get(productId);

    product.register(this, partnerId, price, amount);

    registerTransactionPurchase(partner, product, price, amount);
  }

  public void registerPurchaseNewSimpleProduct(String partnerId, String productId, double price, int amount) {
    registerBatchSimple(productId, partnerId, price, amount);
    
    Partner partner = _partners.get(partnerId);
    Product product = _products.get(productId);

    registerTransactionPurchase(partner, product, price, amount);
  }

  public void registerPurchaseNewDerivateProduct(String partnerId, String productId, double price, int amount, double aggravation, ArrayList<String> productsIds, ArrayList<Integer> productsQuantity) {
    ArrayList<Ingredient> ingredients = new ArrayList<>();
    int size = productsIds.size();

    for(int i = 0; i < size; i++) {
      Ingredient ingredient = new Ingredient(productsIds.get(i), productsQuantity.get(i));
      ingredients.add(ingredient);
    }
    Recipe recipe = new Recipe(ingredients);

    registerBatchDerivate(productId, partnerId, price, amount, aggravation, recipe);

    Partner partner = _partners.get(partnerId);
    Product product = _products.get(productId);

    registerTransactionPurchase(partner, product, price, amount);
  }

  public boolean productExists(String id) {
    return _products.containsKey(id);
  }

  /**
   * @param txtfile filename to be loaded.
   * @throws IOException
   * @throws BadEntryException
   */
  void importFile(String txtfile) throws IOException, BadEntryException /**DuplicatePartnerException */{
    try (BufferedReader in = new BufferedReader(new FileReader(txtfile))) {
      String s;
      while ((s = in.readLine()) != null) {
        String line = new String(s.getBytes(), "UTF-8");
        if (line.charAt(0) == '#')
          continue;

        String[] fields = line.split("\\|");
        switch (fields[0]) {
        case "PARTNER" -> registerPartner(fields[1], fields[2], fields[3]);
        case "BATCH_S" -> registerBatchSimple(fields[1], fields[2], Double.parseDouble(fields[3]), Integer.parseInt(fields[4]));
        case "BATCH_M" -> registerBatchDerivateImport(fields[1], fields[2], Double.parseDouble(fields[3]), Integer.parseInt(fields[4]), Double.parseDouble(fields[5]), fields[6]);
        default -> throw new BadEntryException(fields[0]);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (DuplicatePartnerException e) {
      e.printStackTrace();
    } catch (BadEntryException e) {
      e.printStackTrace();
    }
  }

  public void addNotificationPartners(NotificationX notif, Product p){
    for (Partner partner : getPartners()){
      if (partner.isProductNotifiable(p)){
        partner.addNotification(notif);
      }
    }
  }

  public void registerNotification(Batch batch, Product product){
    /** create notification specific for this operation and */
    NotificationX notif = batch.createNotification();
    if (notif!=null){
      addNotificationPartners(notif, product);
    }
  }

  public void addProductNotification(Product product, Partner partner){
    partner.addNotifiedProduct(product);
  }

  public Map<Integer, Transaction> getTransactionsPurchase(){
    return _transactionsPurchase;
  }

  public Map<Integer, Transaction> getTransactionsSaleAndDesaggregation(){
    return _transactionsSaleAndDesaggregation;
  }

  public int getTransactionsLength(){
    return getTransactionsPurchase().size() + getTransactionsSaleAndDesaggregation().size();
  }

  public void registerTransactionPurchase(Partner partner, Product product, double price, int amount) {
    int id = getTransactionsLength();
    Transaction t = new TransactionPurchase(id, getDate(), partner, product, price, amount);
    addTransactionPurchase(t, id);
    /** update Product existences */
    partner.addTransactionPurchase(id, t);
  }

  public void registerTransactionSale(String partnerId, String productId, int paymentDeadline, int amount) throws OutOfStockProductException {
    Partner partner = _partners.get(partnerId);
    Product product = _products.get(productId);
    int id = getTransactionsLength();
    int available = getTotalProductStock(product);

    if (!product.checkStock(this, amount)) {
      throw new OutOfStockProductException(product.getId(), amount, available);
    }
    
    Transaction t = new TransactionSale(id, getDate(), partner, product, paymentDeadline, amount, product.getN(), getProductsMapStruct());
    addTransactionSaleAndDesaggregation(t, id);
    partner.addTransactionSaleAndDesaggregation(id, t);
  }

  public void addTransactionPurchase(Transaction t, int id){
    getTransactionsPurchase().put(id, t);
  }

  public void addTransactionSaleAndDesaggregation(Transaction t, int id){
    getTransactionsSaleAndDesaggregation().put(id, t);
  }

  public int getTotalProductStock(Product product){
    int totalStock = 0;
    for (Batch b : product.getBatches()){
      totalStock += b.getStock();
    }
    return totalStock;
  }

  public void updateBatchStock(Batch batch, int quantity){
    batch.decreaseStock(quantity);
  }
  
  public Collection<Transaction> getPartnerTransactionsPurchase(Partner partner){
    return Collections.unmodifiableCollection(partner.getTransactionsPurchase().values());
  }

  public Collection<Transaction> getPartnerTransactionsSale(Partner partner){
    return Collections.unmodifiableCollection(partner.getTransactionsSaleAndDesaggregation().values());
  }

  public ArrayList<NotificationX> getNotifications(Partner p){
    return p.getNotifications();
  }
  
  public void deletePartnerNotifications(Partner p){
    p.clearNotifications();
  }

  public Transaction getTransaction(int id) throws UnknownTransactionException{
    if (id >= getTransactionsLength() || id < 0){ 
      throw new UnknownTransactionException(id);
    }
    if (getTransactionsPurchase().containsKey(id)){
      return getPurchase(id);
    }
    else{
      return getSaleOrDesaggregation(id);
    }
  }

  public Transaction getPurchase(int id){
    return getTransactionsPurchase().get(id);
  }

  public Transaction getSaleOrDesaggregation(int id){
    return getTransactionsSaleAndDesaggregation().get(id);
  }

  public double getAvailableBalance(){
    double balance = 0;
    for (Map.Entry<Integer, Transaction> entry : getTransactionsSaleAndDesaggregation().entrySet()){
      if (entry.getValue().isPayed()){
        balance += entry.getValue().getTransactionValue();
      }
    }
    for (Map.Entry<Integer, Transaction> entry : getTransactionsPurchase().entrySet()){
      balance -= entry.getValue().getTransactionValue();
    }
    return balance;
  }

  public double getAccountingBalance(){
    double balance = 0;
    for (Map.Entry<Integer, Transaction> entry : getTransactionsSaleAndDesaggregation().entrySet()){
      balance += entry.getValue().getTransactionValue();
    }
    for (Map.Entry<Integer, Transaction> entry : getTransactionsPurchase().entrySet()){
      balance -= entry.getValue().getTransactionValue();
    }
    return balance;
  }

  public void receivePaymentTransaction(int id) throws UnknownTransactionException{
    Transaction t = getTransaction(id);
    if(t != null)
      t.pay(getDate());
  }

  public ArrayList<Transaction> getPartnerPayedSales(Partner p){
    ArrayList<Transaction> payedSales = new ArrayList<>();
    for (Map.Entry<Integer, Transaction> entry : p.getTransactionsSaleAndDesaggregation().entrySet()){
      if (entry.getValue().isPayed()){
        payedSales.add(entry.getValue());
      }
    }
    return payedSales;
  }

  public ArrayList<Transaction> getTransactionsList(Map<Integer, Transaction> transactions){
    ArrayList<Transaction> transactionsList = new ArrayList<>();
    for (Map.Entry<Integer, Transaction> entry : transactions.entrySet()){
      transactionsList.add(entry.getValue());
    }
    return transactionsList;
  }

  public void registerBreakdown(Partner partner, Product product, int amount) throws OutOfStockProductException, UnknownPartnerException, UnknownProductException {
    ArrayList<Batch> ingredientBatches = new ArrayList<>();
    /** verificar existência dos produtos */
    int currentStock = product.getCurrentStock();
    if (currentStock < amount){
      throw new OutOfStockProductException(product.getId(), amount, currentStock);
    }

    Recipe recipe = product.getRecipe();
    if (recipe==null){return;}
    /** atualização dos produtos e valores de stock */
    for (Ingredient i : recipe.getIngredients()){
      Product p = _products.get(i.getProductId());
      if (p==null){continue; }
      /** calculate product price of new batch */
      double productPrice;
      if (!p.isStockEmpty()){ productPrice=p.getLowestPrice(); }
      else { productPrice = p.getMaxPrice(); }
      i.setValue(productPrice);
      ingredientBatches.add(registerAndReturnBatchSimple(p.getId(), partner.getId(), productPrice, amount*i.getQuantity()));
    }
    /** registo da transação desagregação */
    int id = getTransactionsLength();
    TransactionBreakdown t = new TransactionBreakdown(id, getDate(), partner, product, amount, 5, getProductsMapStruct(), ingredientBatches);
    addTransactionSaleAndDesaggregation(t, id);
    partner.addTransactionSaleAndDesaggregation(id, t);
  }


  public void addNotifiedProductAllPartners(Product product){
    for (Partner partner : getPartners()){
      partner.addNotifiedProduct(product);
    }
  }

  /** copiado do registerBatchSimple mas retorna batch */
  public Batch registerAndReturnBatchSimple(String idProd, String idPar, double price, int stock) {
    Partner partner = _partners.get(idPar);
    Product product;
    Batch batch;
    if (_products.containsKey(idProd)) {
      product = _products.get(idProd); 
      if (price > product.getMaxPrice()) {
        product.setMaxPrice(price);
      }
      batch = new Batch(stock, price, product, partner);
      registerNotification(batch, product);
    }
    else {
      product = registerNewProduct(idProd, price);
      batch = new Batch(stock, price, product, partner);
    }
    product.addBatches(batch);
    return batch;
  }
}
