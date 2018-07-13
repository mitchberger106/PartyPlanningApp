package berger.mitchell.partyplanningapp.Sources;

public class GuestListSource {
    private String name;
    private String status;
    private String number;

    public GuestListSource() {
    }

    public GuestListSource(String name, String status, String number) {
        this.name = name;
        this.status = status;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
    public String getNumber(){return number;}
}