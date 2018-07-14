package berger.mitchell.partyplanningapp.Sources;

public class PartyInfoSource {
    private String name;
    private String date;
    private String guests;
    private String location;
    private String time;
    private String attended;

    public PartyInfoSource() {
    }

    public PartyInfoSource(String name, String date, String guests, String location, String time, String attended) {
        this.name = name;
        this.date = date;
        this.guests = guests;
        this.location = location;
        this.time = time;
        this.attended = attended;
    }

    public String getName() {return name;}

    public String getDate() {
        return date;
    }

    public String getGuests() { return guests; }

    public String getLocation(){ return location; }

    public String getTime(){ return time; }
    public String getAttended(){return attended;}
}