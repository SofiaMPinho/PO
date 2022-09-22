package ggc;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Recipe implements Serializable {

    /** Serial number for serialization. */
    private static final long serialVersionUID = 202109192006L;

    /** String with recipe details */
    private String _recipeString;

    /** List of ingredients that compose Recipe */
    private ArrayList<Ingredient> _ingredients = new ArrayList<>();

    public Recipe(ArrayList<Ingredient> ingredients) {
        _ingredients = ingredients;
    }

    public Recipe(String recipeString) {
        _recipeString = recipeString;
        _ingredients = makeIngredientsList(recipeString);
    }

    public ArrayList<Ingredient> getIngredients(){
        return _ingredients;
    }

    public ArrayList<Ingredient> makeIngredientsList(String recipeStr) {
        /** HIDROGÉNIO:2#OXIGÉNIO:1 */
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        String [] ingredientStrLst;
        String [] els;
        Ingredient i;
        ingredientStrLst = recipeStr.split("#");
        for (String s : ingredientStrLst){
            els = s.split(":"); /** INGREDIENT:QUANTITY */
            i = new Ingredient(els[0], Integer.parseInt(els[1]));
            ingredients.add(i);
        }
        return ingredients;
    }

    @Override
    public String toString() {
        /** for each ingredient get toString */
        ArrayList<Ingredient> ingredients = getIngredients();
        int i;
        String txt = "";
        for (i = 0; i < ingredients.size()-1; i++){
            txt += (ingredients.get(i).toString() + "#");
        }
        txt += ingredients.get(i);
        return txt;
    }
}