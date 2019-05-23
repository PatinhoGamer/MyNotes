package pt.isec.trabandroid.Entries;

import org.json.JSONObject;

import java.util.Calendar;

public final class EntryTextWeather extends Entry {

    private String text;
    private String jsonString;

    public EntryTextWeather(String title, Calendar date, String text, JSONObject jsonObject) throws Exception {
        super(title, date);
        this.text = text;
        this.jsonString = jsonObject.toString();
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public JSONObject getJsonObject() {
        try {
            return new JSONObject(jsonString);
        }catch (Exception ex){}
        return null;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonString = jsonObject.toString();
    }
}
