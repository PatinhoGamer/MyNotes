package pt.isec.trabandroid.Entries;

import java.util.ArrayList;

import pt.isec.trabandroid.Other.User;

public final class EntriesList extends ArrayList<Entry> {

    private User user;

    public EntriesList(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean add(Entry entry) {
        if (entry.getUser() == null)
            entry.setUser(user);
        return super.add(entry);
    }




}
