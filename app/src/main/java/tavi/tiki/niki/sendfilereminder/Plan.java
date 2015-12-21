package tavi.tiki.niki.sendfilereminder;

/**
 * Created by Никита on 12.11.2015.
 */
public class Plan {

    private String file;

    public String toString() {
        return "Send " + file + " to " + contact + ";";
    }

    public Plan(String file, String contact) {
        this.file = file;
        this.contact = contact;
    }

    private String contact;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
