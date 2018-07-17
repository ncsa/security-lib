package edu.uiuc.ncsa.security.util.functor.parser.old;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Parses functional notation into a JSON object.
 * THIS IS INCOMPLETE and is mostly just done for a wee bit out of curiosity.
 * <p>Created by Jeff Gaynor<br>
 * on 7/13/18 at  2:15 PM
 */
public class JSONHandler implements DefaultHandler {

    @Override
    public void endBrace(String name) {

    }

    @Override
    public void startBrace(String name) {

    }

    public JSONObject getJson() {
        return json;
    }

    @Override
    public void reset() {
        currentJSON = null;
        currentArray = null;
        json = new JSONObject();
    }

    JSONObject currentJSON;

    JSONObject json = new JSONObject();

    @Override
    public void endBracket(String name) {
         // close out any arrays.
        currentArray.add(currentJSON);

        currentArray = null;
    }

    @Override
    public void startBracket(String name) {
        currentJSON = new JSONObject();
        json.put("$" + name, currentJSON);
    }

    @Override
    public void startParenthesis(String name) {
        JSONObject nextJSON = new JSONObject();
        currentJSON.put("$" + name, nextJSON);
        currentJSON = nextJSON;
    }

    @Override
    public void endParenthesis(String name) {

    }

    JSONArray currentArray = null;

    @Override
    public void foundComma(String name) {
         if(currentArray == null){
             currentArray = new JSONArray();
         }
        currentArray.add(currentJSON);
        currentJSON = new JSONObject();
    }

    @Override
    public void foundDoubleQuotes(String content) {
         if(currentArray == null){
             currentArray = new JSONArray();
         }
         currentArray.add(content);
    }
}
