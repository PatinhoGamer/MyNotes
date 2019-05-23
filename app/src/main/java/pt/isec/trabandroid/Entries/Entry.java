package pt.isec.trabandroid.Entries;

import java.io.Serializable;
import java.util.Calendar;

import pt.isec.trabandroid.Other.User;

public abstract class Entry implements Serializable {

    protected User user;
    protected String title;
    protected Calendar date;

    public Entry(String title, Calendar date) throws Exception {
        this.title = title;
        this.date = date;
        this.user = null;
        if (title.trim().equals("")) {
            throw new Exception();
        }
    }

    @Override
    public String toString() {
        return title + " (" + getDateString() + ")";
    }

    public String[] getDateString() {
        return new String[]{
                String.format("\n%d:%d:%d", date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), date.get(Calendar.SECOND)),
                String.format("\n%d/%d/%d", date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.MONTH) + 1, date.get(Calendar.YEAR))};
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
