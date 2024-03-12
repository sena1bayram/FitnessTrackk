package com.example.fitnesstrack.memberlist;

public class Members {
    private String FullName;
    private String Username;
    private String Email;
    private String Birth;
    private String Team;


    public Members(){

    }
    public Members(String fullName, String username, String email, String birth, String team) {
        FullName = fullName;
        Username = username;
        Email = email;
        Birth = birth;
        Team = team;
    }

    public String getTeam() { return Team; }

    public String getFullName() {
        return FullName;
    }

    public String getUsername() {
        return Username;
    }

    public String getEmail() {
        return Email;
    }

    public String getBirth() {
        return Birth;
    }
}
