package com.novoda.v3rt1ag0.user.data.model;

public class User {

    private String id;
    private String name;
    private String photoUrl;
    private String firebasetoken;

    @SuppressWarnings("unused") //Used by Firebase
    public User() {
    }

    public User(String id, String name, String photoUrl,String firebasetoken) {
        this.id = id;
        this.name = name;
        this.photoUrl = photoUrl;
        this.firebasetoken=firebasetoken;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getFirebasetoken()
    {
        return firebasetoken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) {
            return false;
        }
        if (name != null ? !name.equals(user.name) : user.name != null) {
            return false;
        }
        return photoUrl != null ? photoUrl.equals(user.photoUrl) : user.photoUrl == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (photoUrl != null ? photoUrl.hashCode() : 0);
        return result;
    }
}
